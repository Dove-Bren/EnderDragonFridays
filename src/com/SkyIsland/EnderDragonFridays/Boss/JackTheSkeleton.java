package com.SkyIsland.EnderDragonFridays.Boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LargeFireball;
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
import org.bukkit.util.Vector;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.BlindnessVeil;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.FireballCannon;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.TargetType;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events.BlindnessVeilEvent;
import com.SkyIsland.EnderDragonFridays.Boss.Cannon.Events.FireFireballEvent;
import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;
import com.griefcraft.model.Protection;
import com.griefcraft.sql.PhysDB;

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
		
		new BlindnessVeil(this, TargetType.MOSTDAMAGE, 20, 30);
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
	
	public LivingEntity getDragon() {
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
		killDragon();
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

	public void killDragon() {
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
