package com.SkyIsland.EnderDragonFridays;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class EnderDragonFight {
	
	/**
	 * list of players who have participated
	 */
	private List<Player> players;
	
	private EnderDragonFridaysPlugin plugin;
	
	private EnderDragon dragon;
	
	public EnderDragonFight(EnderDragonFridaysPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void CreateDragon(Location loc, String name) {
		if (dragon == null || !dragon.isLiving()){
			LivingEntity drags;
			drags = (LivingEntity) (loc.getWorld().spawnEntity(loc.add(0, 20, 0), EntityType.ENDER_DRAGON));
			drags.setMaxHealth((players.size() + 1) * drags.getMaxHealth()); //scale up with players
			drags.setCustomName("Young Ender Dragon");
			
			dragon = new EnderDragon(plugin, drags);
		}
	}
}
