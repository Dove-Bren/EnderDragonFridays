package com.SkyIsland.EnderDragonFridays;


import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class EnderDragonFight {
	
	/**
	 * list of players who have participated
	 */
	
	private EnderDragonFridaysPlugin plugin;
	
	private EnderDragon dragon;
	
	public EnderDragonFight(EnderDragonFridaysPlugin plugin) {
		this.plugin = plugin;
	}
	
	
	
	public void CreateDragon(int level, Location loc, String name) {
		if (dragon == null || !dragon.isLiving()){
			LivingEntity drags;
			drags = (LivingEntity) (loc.getWorld().spawnEntity(loc.add(0, 20, 0), EntityType.ENDER_DRAGON));
			drags.setMaxHealth((level + 1) * drags.getMaxHealth()); //scale up with players
			drags.setHealth(drags.getMaxHealth());
			drags.setCustomName("Young Ender Dragon");
			
			dragon = new EnderDragon(plugin, drags);
		}
	}
	
	public void killDragon() {
		if (dragon == null || !dragon.isLiving()) {
			return;
		}
		
		dragon.kill();
	}
	
	public void endFight() {
		killDragon();
		this.dragon = null;
	}
}
