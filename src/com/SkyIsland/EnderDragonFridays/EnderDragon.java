package com.SkyIsland.EnderDragonFridays;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class EnderDragon {
	private LivingEntity dragon;
	
	/**
	 * Creates an enderdragon
	 * @param loc
	 * @param name it's name
	 */
	public EnderDragon(Location loc, String name) {
		dragon = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
		if (name != null && name.length() > 0) {
			dragon.setCustomName(name);
			dragon.setCustomNameVisible(true);
		}
	}
	
	
	
	
	public boolean isLiving() {
		if (dragon == null) {
			return false;
		}

		return !dragon.isDead();
	}
}
