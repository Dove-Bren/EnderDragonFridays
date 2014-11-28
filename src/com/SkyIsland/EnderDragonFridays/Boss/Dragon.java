package com.SkyIsland.EnderDragonFridays.Boss;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.FireballCannon;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.TargetType;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events.FireFireballEvent;
import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;
import com.griefcraft.model.Protection;
import com.griefcraft.sql.PhysDB;

public abstract class Dragon implements Listener, Boss {

	protected int level;							//The level of the dragon
	protected LivingEntity dragon;				//The actual Entity for the Ender Boss
	protected Map<UUID, Double> damageMap;		//The damage each player has done to the ender dragon
	protected double damageTaken;
	protected Location chestAreaBL;
	
	public boolean isLiving() {
		if (dragon == null) {
			return false;
		}

		return !dragon.isDead();
		//Register this class as a listener
	}
	
	public LivingEntity getEntity() {
		return this.dragon;
	}
	
	public Player getMostDamage() {
		if (damageMap.isEmpty()) {
			return null;
		}
		
		Player player = null;
		double max = -999999.0;
		Player play;
		for (Entry<UUID, Double> entry : damageMap.entrySet()) {
			play = Bukkit.getPlayer(entry.getKey());
			if (play != null && entry.getValue() > max && play.getWorld().getName().equals(dragon.getWorld().getName()))
			{
				player = play;
				max = entry.getValue();
			}
		}
		
		
		return player;
	}
	
	@EventHandler
	public void dragonDamage(EntityDamageByEntityEvent event) {
		
		//Do nothing if the dragon wasn't damaged
		if (!event.getEntity().equals(dragon)) {
			return;
		}
		
		//Add the damage to the total counter
		damageTaken += event.getDamage();
		
		
		//Try to get the player who damaged the dragon
		Player player = null;
		if (event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
		}
		else if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getShooter() instanceof Player){
				player = (Player) proj.getShooter();
			}
		}
		
		//If we couldn't find a player, do nothing
		if (player == null){
			return;
		}
		
		
		//Add the player to the hashmap if needed
		if (!damageMap.containsKey(player.getUniqueId())) {
			damageMap.put(player.getUniqueId(), 0.0);
		}
		
		//Update the damage for the player
		double oldDamage = damageMap.get(player.getUniqueId());
		damageMap.put(player.getUniqueId(), oldDamage + event.getDamage()); 
	}
	
	@EventHandler
	public void dragonDeath(EntityDeathEvent event) {
		
		//if the dragon has died
		if (event.getEntity().equals(dragon)) {
			win();
		}
	}
	
	/**
	 * Specifies that the fight was won. This is different from endFight in that this method spawns rewards and
	 * kills the fight.
	 */
	public void win(int base) {
		kill();
		Map<UUID, Inventory> rewardMap = ChestContentGenerator.generate(base + (this.level / 5), this.damageMap);
		spawnRewards(rewardMap);
		congradulatePlayers(this.damageMap);
	}
	
	/**
	 * Generic win command. <br />
	 * Uses a base value of <b>5</b>
	 */
	public void win() {
		win(5);
	}
	
	public void spawnRewards(Map<UUID, Inventory> map) {
		//spawn chests at random in 10x10 area with bottom left block at location chestAreaBL
		
		//first make sure map isn't empty. If it is... something went wrong, but we're just 
		//going to ignore it for now
		if (map.isEmpty()) {
			EnderDragonFridaysPlugin.plugin.getLogger().info("Map of contributions was empty!\nSpawning no rewards...");
			return;
		}
		System.out.println("Called \"Spawn Rewards\" ! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		//We just put chests in a linear fashion. We do cap x to 10. <b>this is a magic number</b>
		int index = 0;
		double x, y;
		for (Entry<UUID, Inventory> entry : map.entrySet()) {
			x = (index % 11);
			y = (int) Math.floor(index / 11);
			Player player = Bukkit.getPlayer(entry.getKey());
			
			Block block = chestAreaBL.getBlock().getLocation().add(x,0,y).getBlock();
			block.setType(Material.CHEST);
			Chest chest = (Chest) block.getState();
			chest.getInventory().setContents(entry.getValue().getContents()); //bummer I thought we would be able to just hand it the inv
			doExtras(chest, player);
			index += 2;
			System.out.println("Index now equals : " + index);
			
			EnderDragonFridaysPlugin.plugin.getLogger().info("Created a chest for player " + player.getDisplayName() + " at " + chest.getLocation().toString());

			}
		
	}
	
	@SuppressWarnings("deprecation")
	public void doExtras(Chest chest, Player player) {
		//for EDF's, we want to lock the chest and put a sign above it telling who's it is
		//Protection protection;
		PhysDB physDb = EnderDragonFridaysPlugin.lwcPlugin.getLWC().getPhysicalDatabase();
		
		String worldName = chestAreaBL.getWorld().getName();
		/*protection = */
		physDb.registerProtection(chest.getTypeId(), Protection.Type.PRIVATE, worldName, player.getName(), "", chest.getX(), chest.getY(), chest.getZ());
		
		EnderDragonFridaysPlugin.plugin.getLogger().info("success?");

		//Now create a sign above it
		Block block = chest.getLocation().add(0,1,0).getBlock();
		block.setType(Material.SIGN_POST);
		Sign sign = (Sign) block.getState();
		sign.setLine(1, player.getName());
		sign.update();
		//register the sign
		physDb.registerProtection(sign.getTypeId(), Protection.Type.PRIVATE, worldName, player.getName(), "", sign.getX(), sign.getY(), sign.getZ());
		
	}
	
	/**
	 * Print out custom message to player letting them know how they did
	 * @param map
	 */
	public void congradulatePlayers(Map<UUID, Double> map) {
		for (Entry<UUID, Double> entry : map.entrySet()) {
			
			Player player = Bukkit.getPlayer(entry.getKey());
			try{
				player.sendMessage("Nice Fight!\n  "
					+ "You did " + (entry.getValue().intValue()) + " points of damage!\n"
							+ "Your contribution was " + ((entry.getValue() / damageTaken) * 100) + "%!");
			}
			catch (Exception e){
				//Player wasn't online anymore
			}
		}
	}
	
	@EventHandler
	public void cannonFired(FireFireballEvent event){
		LivingEntity target = event.getTarget();
		LivingEntity shooter = event.getShooter();
		
		Vector launchV;
		Location pPos, dPos;
		dPos = shooter.getEyeLocation();
		pPos = target.getEyeLocation();
		launchV = pPos.toVector().subtract(dPos.toVector());
		
		LargeFireball f = shooter.launchProjectile(LargeFireball.class);
		f.setDirection(launchV.normalize());
	}

	public void kill() {
		if (!dragon.isDead())
		dragon.damage(dragon.getMaxHealth());
	}
	
	public boolean isAlive(){
		
		if (dragon == null) {
			return false;
		}
		
		return (!dragon.isDead());
	}
	
	public List<UUID> getDamageList() {
		return new ArrayList<UUID>(damageMap.keySet());
	}
}
