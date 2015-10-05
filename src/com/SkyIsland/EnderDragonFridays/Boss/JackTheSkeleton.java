package com.SkyIsland.EnderDragonFridays.Boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.BlindnessVeil;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events.BlindnessVeilEvent;
import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;

public class JackTheSkeleton implements Listener, Boss {

	private int level;							//The level of the dragon
	private LivingEntity dragon;				//The actual Entity for the Ender Boss
	private Map<UUID, Double> damageMap;		//The damage each player has done to the ender dragon
	private double damageTaken;
	private Location chestAreaBL;
	private org.bukkit.entity.EnderDragon healthbar;
	
	/**
	 * Creates a default enderdragon
	 * @param plugin The EnderDragonFridays plugin this dragon is associated with
	 * @param level The level of the dragon
	 * @param loc
	 * @param name it's name
	 */
	public JackTheSkeleton(World world, int level, String name) {
		
		this.chestAreaBL = world.getSpawnLocation();
		
		this.damageTaken = 0;
		
		//Ensure level is a positive integer
		if (level <= 0) {
			level = 1;
		}
		this.level = level;

		//Spawn an ender dragon
		dragon = (LivingEntity) world.spawnEntity(world.getSpawnLocation(), EntityType.SKELETON);
		((Skeleton) dragon).setSkeletonType(SkeletonType.WITHER);
		dragon.setRemoveWhenFarAway(false);
		
		dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2));
		
		
		dragon.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 4);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
		dragon.getEquipment().setItemInHand(sword);
		
		//Set the dragon's name
		if (name != null && name.length() > 0) {
			dragon.setCustomName(name + " (Lvl " + level + ")");
			dragon.setCustomNameVisible(true);
		}
		this.healthbar = (org.bukkit.entity.EnderDragon) world.spawnEntity(world.getSpawnLocation().add(0, -40000, 0), EntityType.ENDER_DRAGON);
		
		//Set the dragon's health
		dragon.setMaxHealth(healthbar.getMaxHealth() * (2 + (Math.log(level)/Math.log(2))));
		dragon.setHealth(dragon.getMaxHealth());
		
		healthbar.setMaxHealth(dragon.getMaxHealth());
		healthbar.setHealth(dragon.getMaxHealth());
		healthbar.setCustomName(dragon.getCustomName());

		//Initialize the map of damage each player does to the dragon
		damageMap = new HashMap<UUID, Double>();
		
		//Start firing the dragon's fireballs
		//Bukkit.getScheduler().scheduleSyncRepeatingTask(EnderDragonFridaysPlugin.plugin, new FireballCannon(this, 500, 2000), 20, (long) (20 / (1 + (Math.log(level)/Math.log(2)))));
		//Removed ^^ and handle this in FireballCannon instead
		
		new BlindnessVeil(this, 20, 30);
		//least delay is what it was before. Max is the same + 5 ticks
		
		EnderDragonFridaysPlugin.plugin.getServer().getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);

	}
	
	
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
		
		//update healthbar
		healthbar.setHealth(dragon.getHealth());
		Random rand = new Random();
		
		//Try to get the player who damaged the dragon
		Player player = null;
		if (event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
			Player tmp;
			tmp = dragon.getWorld().getPlayers().get(rand.nextInt(dragon.getWorld().getPlayers().size()));
			dragon.teleport(tmp.getLocation().add(rand.nextInt(10), 0, rand.nextInt(10)));
		}
		else if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getShooter() instanceof Player){
				player = (Player) proj.getShooter();
				dragon.teleport(player.getLocation().add(rand.nextInt(10), 0, rand.nextInt(10)));
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
	public void win() {
		kill();
		Location loc = dragon.getLocation();
		World world = dragon.getWorld();
		Random rand = new Random();
		for (int i = 0; i < 3000; i++) {
			world.spawnEntity(loc.add(rand.nextFloat() * 10, 0, rand.nextFloat()), EntityType.EXPERIENCE_ORB);
		}
		Map<UUID, Inventory> rewardMap = ChestContentGenerator.generate(7 + (this.level / 5), this.damageMap);
		spawnRewards(rewardMap);
		congradulatePlayers(this.damageMap);
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
	public void cannonFired(BlindnessVeilEvent event){
		//reset health bar just cause
		healthbar.teleport(event.getShooter().getWorld().getSpawnLocation().add(0, -100, 0));
		//blind everyone
		for (Player p : event.getShooter().getWorld().getPlayers()) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 1));
		}
		
		Skeleton skel = (Skeleton) dragon;
		if (skel.getTarget() == null) {
			List<Player> plays = dragon.getWorld().getPlayers();
			if (plays.isEmpty()) {
				return;
			}
			Random rand = new Random();
			skel.setTarget(plays.get(rand.nextInt(plays.size())));
		}
	}

	public void kill() {
		healthbar.damage(healthbar.getMaxHealth());
		healthbar.remove();
		if (!dragon.isDead()) {
			dragon.damage(dragon.getMaxHealth());
		}
	}
	
	public boolean isAlive(){
		
		if (dragon == null) {
			return false;
		}
		
		return (!dragon.isDead());
	}


	@Override
	public List<UUID> getDamageList() {
		return new ArrayList<UUID>(damageMap.keySet());
	}
}
