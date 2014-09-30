package com.SkyIsland.EnderDragonFridays;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EnderDragon implements Listener {
	private LivingEntity dragon;
	private EnderDragonFridaysPlugin plugin;
	
	/**
	 * Creates a default enderdragon
	 * @param loc
	 * @param name it's name
	 */
	public EnderDragon(EnderDragonFridaysPlugin plugin, Location loc, String name) {
		this.plugin = plugin;
		
		//register this as an event listener
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		
		dragon = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
		if (name != null && name.length() > 0) {
			dragon.setCustomName(name);
			dragon.setCustomNameVisible(true);
		}
	}
	
	public EnderDragon(EnderDragonFridaysPlugin plugin, LivingEntity dragon) {
		this.dragon = dragon;
	}
	
	
	public boolean isLiving() {
		if (dragon == null) {
			return false;
		}

		return !dragon.isDead();
	}
	
	@EventHandler
	public void dragonDeath(EntityDeathEvent event) {
		if (event.getEntity().equals(dragon)) {
			//if the dragon has died
		}
	}
}
