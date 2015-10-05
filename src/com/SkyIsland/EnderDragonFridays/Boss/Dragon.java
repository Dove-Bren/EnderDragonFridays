package com.SkyIsland.EnderDragonFridays.Boss;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
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
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events.FireFireballEvent;
import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;

public abstract class Dragon implements Listener, Boss {

	protected int level;							//The level of the dragon
	protected LivingEntity dragon;				//The actual Entity for the Ender Boss
	protected Map<UUID, Double> damageMap;		//The damage each player has done to the ender dragon
	protected double damageTaken;
	protected Location chestAreaBL;
	
	
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
		//spawn the loot chest, and create inventories for every player
		
		//first make sure map isn't empty. If it is... something went wrong, but we're just 
		//going to ignore it for now
		if (map.isEmpty()) {
			EnderDragonFridaysPlugin.plugin.getLogger().info("Map of contributions was empty!\nSpawning no rewards...");
			return;
		}
		
		//do fancy stuff
		chestAreaBL.getWorld().spawnEntity(chestAreaBL, EntityType.LIGHTNING);

		//Create our loot chest
		chestAreaBL.getBlock().setType(Material.CHEST);
		Chest chest = (Chest) chestAreaBL.getBlock().getState();
		
		//tell players it's there
		for (Player p : chestAreaBL.getWorld().getPlayers()) {
			p.sendMessage("The loot chest has been generated at (" 
		+ chestAreaBL.getBlockX() + ", "
		+ chestAreaBL.getBlockY() + ", "
		+ chestAreaBL.getBlockZ() + ")");
		}
		
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
