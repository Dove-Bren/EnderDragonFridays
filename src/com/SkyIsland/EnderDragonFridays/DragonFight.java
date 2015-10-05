package com.SkyIsland.EnderDragonFridays;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.SkyIsland.EnderDragonFridays.Boss.Boss;
import com.SkyIsland.EnderDragonFridays.Boss.BossDeathEvent;
import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;

/**
 * Session for a dragon fight.<br />
 * Holds state and strates session-wide events
 * @author Skyler
 *
 */
public class DragonFight implements Listener {
	
	public enum State {
		PREFIGHT,
		DURING,
		FINISHED;
	}
	
	private State state;
	
	private Boss boss;
	
	private Location chestLocation;
	
	private Map<UUID, Inventory> inventories;
	
	private double difficulty;
	
	private double difficultyBase;
	
	private World world;
	
	public DragonFight(World world, Boss boss, double difficulty, double difficultyBase, Location chestLocation) {
		this.boss = boss;
		this.chestLocation = chestLocation;
		this.difficulty = difficulty;
		this.difficultyBase = difficultyBase;
		this.world = world;
		
		this.state = State.PREFIGHT;
		
		inventories = new HashMap<UUID, Inventory>();
	}
	
	/**
	 * Starts the fight, if it's able to be started.
	 * @return true if the fight is then started, or false if an error occurs
	 */
	public boolean start() {
		if (state != State.PREFIGHT) {
			EnderDragonFridaysPlugin.plugin.getLogger().warning("Cannot start an instance that's already going! State = " + state);
			return false;
		}
		
		state = State.DURING;
		boss.start(world.getSpawnLocation().add(0, 50.0, 0));
		
		return true;
	}
	
	/**
	 * Tries to stop the fight.
	 * @param win should this stop be considered as a win?
	 * @return Whether the stop was successful or not
	 */
	public boolean stop(boolean win) {
		if (state != State.DURING) {
			EnderDragonFridaysPlugin.plugin.getLogger().warning("Cannot stop a session that's not going! State = " + state);
			return false;
		}
		
		if (win) {
			
			boss.win();
			return true;
		} else {
			boss.kill();
			return true;
		}
	}
	
	private void spawnRewards() {
		//spawn the loot chest, and create inventories for every player
		inventories = ChestContentGenerator.generate(difficultyBase + (difficulty / 5), boss.getDamageMap());
		congratulatePlayers(boss.getDamageMap());
		
		//first make sure map isn't empty. If it is... something went wrong, but we're just 
		//going to ignore it for now
		if (inventories.isEmpty()) {
			EnderDragonFridaysPlugin.plugin.getLogger().info("Map of contributions was empty!\nSpawning no rewards...");
			return;
		}
		
		//do fancy stuff
		chestLocation.getWorld().spawnEntity(chestLocation, EntityType.LIGHTNING);

		//Create our loot chest
		chestLocation.getBlock().setType(Material.CHEST);
		
		//tell players it's there
		for (Player p : chestLocation.getWorld().getPlayers()) {
			p.sendMessage("The loot chest has been generated at (" 
		+ chestLocation.getBlockX() + ", "
		+ chestLocation.getBlockY() + ", "
		+ chestLocation.getBlockZ() + ")");
		}
		
	}
	
	/**
	 * Print out custom message to player letting them know how they did
	 * @param map
	 */
	private void congratulatePlayers(Map<UUID, Double> map) {
		for (Entry<UUID, Double> entry : map.entrySet()) {
			
			Player player = Bukkit.getPlayer(entry.getKey());
			try{
				player.sendMessage("Nice Fight!\n  "
					+ "You did " + (entry.getValue().intValue()) + " points of damage!\n"
							+ "Your contribution was " + ((entry.getValue() / boss.getDamageTaken()) * 100) + "%!");
			}
			catch (Exception e){
				; //do nothing. minor bug, will be reported elsewhere
			}
		}
	}
	
	@EventHandler
	public void onBossDeath(BossDeathEvent e) {
		if (e.getBoss().equals(boss)) {
			//our boss has died!
			state = State.FINISHED;
			spawnRewards();
		}
	}
	
	@EventHandler
	public void onLoot(PlayerInteractEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
		if (e.getClickedBlock() == null || e.getClickedBlock().getType() == Material.AIR) {
			return;
		}
		
		if (inventories == null || inventories.isEmpty()) {
			return;
		}
		
		if (state != State.FINISHED) {
			return;
		}
		
		if (e.getClickedBlock().getLocation().equals(chestLocation))
		if (inventories.containsKey(e.getPlayer().getUniqueId())) {
			//player clicked it and has a chest
			e.setCancelled(true);
			e.getPlayer().openInventory(inventories.get(e.getPlayer().getUniqueId()));
		}
	}
}
