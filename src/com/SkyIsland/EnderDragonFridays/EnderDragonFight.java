package com.SkyIsland.EnderDragonFridays;


import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;
import com.griefcraft.model.Protection;
import com.griefcraft.sql.PhysDB;

/**
 * An EnderDragonFight represents a single fight between an EnderDragon and the players.
 *
 */
public class EnderDragonFight {
	
	private EnderDragon dragon;
	
	private Location chestAreaBL;
	
	private boolean isFighting;
	
	public EnderDragonFight(Location chestArea) {
		this.chestAreaBL = chestArea;
		this.isFighting = false;
	}
	
	public void CreateDragon(int level, Location loc, String name) {
		if (dragon == null || !dragon.isLiving()){
			LivingEntity drags;
			drags = (LivingEntity) (loc.getWorld().spawnEntity(loc.add(0, 20, 0), EntityType.ENDER_DRAGON));
			drags.setMaxHealth((level + 1) * drags.getMaxHealth()); //scale up with players
			drags.setHealth(drags.getMaxHealth());
			drags.setCustomName(name);
			
			dragon = new EnderDragon(level, drags);
			
			Bukkit.getPluginManager().registerEvents(dragon, EnderDragonFridaysPlugin.plugin);
		}
	}
	
	public void killDragon() {
		if (dragon == null || !dragon.isLiving()) {
			return;
		}
		
		dragon.kill();
		isFighting = false;
	}
	
	/**
	 * Kills the dragon. Does clean up of plugin and spawned classes. Does not spawn rewards.
	 */
	public void endFight() {
		killDragon();
		this.dragon = null;
	}
	
	/**
	 * Specifies that the fight was won. This is different from endFight in that this method spawns rewards and
	 * kills the fight.
	 */
	public void win() {
		
		Map<Player, Inventory> rewardMap = ChestContentGenerator.generate(5 + (dragon.getLevel() / 5), dragon.getDamageMap());
		spawnRewards(rewardMap);
		hailPlayers(dragon.getDamageMap());
		endFight();
		
	}
	
	public void spawnRewards(Map<Player, Inventory> map) {
		//spawn chests at random in 10x10 area with bottom left block at location chestAreaBL
		
		//first make sure map isn't empty. If it is... something went wrong, but we're just 
		//giong to ignore it for now
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
		for (Entry<Player, Inventory> entry : map.entrySet()) {
			Player player = entry.getKey();
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
			
//			chestAreaBL.add(x, 0, y).getBlock().setType(Material.CHEST);
//			Chest chest = (Chest) chestAreaBL.add(x, 0, y).getBlock().getState();
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
//		chest.getLocation().add(0, 1, 0).getBlock().setType(Material.SIGN);
//		Sign sign = (Sign) chest.getLocation().add(0,1,0).getBlock().getState();
		
		Block block = chest.getLocation().add(0,1,0).getBlock();
		block.setType(Material.SIGN_POST);
		
		Sign sign = (Sign) block.getState();
		
		sign.setLine(1, player.getName());
		sign.update();
		
	}
	
	/**
	 * Print out custom message to player letting htem know how they did
	 * @param map
	 */
	public void hailPlayers(Map<Player, Double> map) {
		for (Entry<Player, Double> entry : map.entrySet()) {
			
			Player player = entry.getKey();
			player.sendMessage("Nice Fight!\n  "
					+ "You did " + (entry.getValue().intValue() * dragon.getDragon().getMaxHealth()) + " points of damage!\n"
							+ "Your contribution was " + entry.getValue()*100 + "%!");
		}
	}
	
	public boolean isFighting(){
		return isFighting;
	}
}
