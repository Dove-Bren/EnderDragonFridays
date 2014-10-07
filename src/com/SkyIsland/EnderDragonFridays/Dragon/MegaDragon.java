package com.SkyIsland.EnderDragonFridays.Dragon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Dragon.Cannon.BlazeCannon;
import com.SkyIsland.EnderDragonFridays.Dragon.Cannon.FireballCannon;
import com.SkyIsland.EnderDragonFridays.Dragon.Cannon.TargetType;
import com.SkyIsland.EnderDragonFridays.Dragon.Cannon.Events.FireBlazeEvent;
import com.SkyIsland.EnderDragonFridays.Dragon.Cannon.Events.FireFireballEvent;
import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;
import com.griefcraft.model.Protection;
import com.griefcraft.sql.PhysDB;

public class MegaDragon implements Listener, Dragon {

	private int level;							//The level of the dragon
	private LivingEntity dragon;				//The actual Entity for the Ender Dragon
	private Map<UUID, Double> damageMap;		//The damage each player has done to the ender dragon
	private double damageTaken;
	private Location chestAreaBL;
	
	/**
	 * Creates a default enderdragon
	 * @param plugin The EnderDragonFridays plugin this dragon is associated with
	 * @param level The level of the dragon
	 * @param loc
	 * @param name it's name
	 */
	public MegaDragon(World world, int level, String name) {
		
		this.chestAreaBL = world.getSpawnLocation();
		
		this.damageTaken = 0;
		
		//Ensure level is a positive integer
		if (level <= 0) {
			level = 1;
		}
		this.level = level;

		//Spawn an ender dragon
		dragon = (LivingEntity) world.spawnEntity(world.getSpawnLocation().add(0, 50, 0), EntityType.ENDER_DRAGON);
		
		//Set the dragon's name
		if (name != null && name.length() > 0) {
			dragon.setCustomName(name + " (Lvl " + level + ")");
			dragon.setCustomNameVisible(true);
		}
		
		//Set the dragon's health
		dragon.setMaxHealth(dragon.getMaxHealth() * (2 + (Math.log(level)/Math.log(2))));
		dragon.setHealth(dragon.getMaxHealth());

		//Initialize the map of damage each player does to the dragon
		damageMap = new HashMap<UUID, Double>();
		
		//Create cannons
		new FireballCannon(this, TargetType.MOSTDAMAGE, (40 / (1 + (Math.log(level)/Math.log(2)))), (40 / (1 + (Math.log(level)/Math.log(2)))) + 5, 5.0, 0.0, 0.0);
		new FireballCannon(this, TargetType.MOSTDAMAGE, (40 / (1 + (Math.log(level)/Math.log(2)))), (40 / (1 + (Math.log(level)/Math.log(2)))) + 5, -5.0, 0.0, 0.0);
		new BlazeCannon(this, TargetType.MOSTDAMAGE, (20 / (1 + (Math.log(level)/Math.log(2)))), (20 / (1 + (Math.log(level)/Math.log(2)))) + 5, 0.0, 0.0, 5.0);
		
		EnderDragonFridaysPlugin.plugin.getServer().getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);

	}
	
	
	public boolean isLiving() {
		if (dragon == null) {
			return false;
		}

		return !dragon.isDead();
		//Register this class as a listener
	}
	
	public LivingEntity getDragon() {
		return this.dragon;
	}
	
	public Player getMostDamage() {
		if (damageMap.isEmpty()) {
			return null;
		}
		
		Player player = null;
		double max = -999999.0;
		for (Entry<UUID, Double> entry : damageMap.entrySet()) {
			if (entry.getValue() > max) {
				player = Bukkit.getPlayer(entry.getKey());
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
		double oldDamage = damageMap.get(player);
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
	public void win() {
		killDragon();
		Map<UUID, Inventory> rewardMap = ChestContentGenerator.generate(5 + (this.level / 5), this.damageMap);
		spawnRewards(rewardMap);
		congradulatePlayers(this.damageMap);
	}
	
	public void spawnRewards(Map<UUID, Inventory> map) {
		//spawn chests at random in 10x10 area with bottom left block at location chestAreaBL
		
		//first make sure map isn't empty. If it is... something went wrong, but we're just 
		//going to ignore it for now
		if (map.isEmpty()) {
			EnderDragonFridaysPlugin.plugin.getLogger().info("Map of contributions was empty!\nSpawning no rewards...");
			return;
		}
		
		//we don't want to make two chests in the same area. We have to keep track of where we have put a chest.
		//for that, we're going to hash the x and y we put a chest following:
		//z = 10*x + y;
		//where z is the hashed index of the chest
		ArrayList<Integer> chestCoords = new ArrayList<Integer>();
		Random rand = new Random();
		int x, y, tries;
		for (Entry<UUID, Inventory> entry : map.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			x = rand.nextInt(10);
			y = rand.nextInt(10);
			tries = 0;
			while (chestCoords.contains((x * 10) + y)) {
				x = rand.nextInt(10);
				y = rand.nextInt(10);
				if (tries > 10) {
					System.out.println("Trying to generate unique chest location... Got x: " + x + "  and y: " + y);
				}
				if (tries == 30) {
					EnderDragonFridaysPlugin.plugin.getLogger().info("Unable to generate a chest for player: " + player.getName());
					break;
				}
			}
			
			if (chestCoords.contains((x * 10) + y)) {
				
				player.sendMessage("An error occurred. Please let an Admin know, and refer them to com.SkyIsland.EnderDragonFridays.EnderDragonFight line 125.\nTake a screenshot so you don't forget!");
				continue;
			}
			
			Block block = chestAreaBL.add(x,0,y).getBlock();
			block.setType(Material.CHEST);
			Chest chest = (Chest) block.getState();
			chest.getInventory().setContents(entry.getValue().getContents()); //bummer I thought we would be able to just hand it the inv
			doExtras(chest, player);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void doExtras(Chest chest, Player player) {
		//for EDF's, we want to lock the chest and put a sign above it telling who's it is
		//Protection protection;
		PhysDB physDb = EnderDragonFridaysPlugin.lwcPlugin.getLWC().getPhysicalDatabase();
		
		String worldName = chestAreaBL.getWorld().getName();
		/*protection = */physDb.registerProtection(chest.getTypeId(), Protection.Type.PRIVATE, worldName, player.getName(), "", chest.getX(), chest.getY(), chest.getZ());
		
		EnderDragonFridaysPlugin.plugin.getLogger().info("success?");

		//Now create a sign above it
		Block block = chest.getLocation().add(0,1,0).getBlock();
		block.setType(Material.SIGN_POST);
		Sign sign = (Sign) block.getState();
		sign.setLine(1, player.getName());
		sign.update();
		
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
	public void blazeCannonFired(FireBlazeEvent event){
		LivingEntity target = event.getTarget();
		LivingEntity shooter = event.getShooter();
		
		Vector launchV;
		Location pPos, dPos;
		dPos = shooter.getEyeLocation();
		pPos = target.getEyeLocation();
		launchV = pPos.toVector().subtract(dPos.toVector());
		
		SmallFireball f = shooter.launchProjectile(SmallFireball.class);
		f.setDirection(launchV.normalize());
	}
	
	@EventHandler
	public void FireballCannonFired(FireFireballEvent event){
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
	
	

	public void killDragon() {
		if (!dragon.isDead())
		dragon.damage(dragon.getMaxHealth());
	}
	
	public boolean isAlive(){
		
		if (dragon == null) {
			return false;
		}
		
		return (!dragon.isDead());
	}
}
