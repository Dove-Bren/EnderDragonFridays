package com.SkyIsland.EnderDragonFridays;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.SkyIsland.EnderDragonFridays.Boss.Boss;

import net.minecraft.server.v1_8_R3.Material;

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
	
	private Chest lootChest;
	
	private Location chestLocation;
	
	private Map<UUID, Inventory> inventories;
	
	public DragonFight(Boss boss, Location chestLocation) {
		this.boss = boss;
		this.chestLocation = chestLocation;
		
		this.state = State.PREFIGHT;
		
		inventories = new HashMap<UUID, Inventory>();
	}
	
	public void start() {
		
	}
	
	public void stop() {
		
	}
	
	@EventHandler
	public void onLoot(PlayerInteractEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
		if (e.getClickedBlock() == null || e.getClickedBlock() == Material.AIR) {
			return;
		}
		
		if (inventories == null || inventories.isEmpty()) {
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
