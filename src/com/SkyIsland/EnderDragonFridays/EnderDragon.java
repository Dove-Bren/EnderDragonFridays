package com.SkyIsland.EnderDragonFridays;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;

public class EnderDragon implements Listener {
	private LivingEntity dragon;
	private EnderDragonFridaysPlugin plugin;
	private Map<Player, Integer> damageMap;
	private Map<Player, Inventory> chestMap;
	private FireballCannon cannon;
	
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
		
		damageMap = new HashMap<Player, Integer>();
		chestMap = new HashMap<Player, Inventory>();
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
	
	public LivingEntity getDragon() {
		return this.dragon;
	}
	
	public Player getMostDamage() {
		return null;
	}
	
	@EventHandler
	public void dragonDeath(EntityDeathEvent event) {
		if (event.getEntity().equals(dragon)) {
			//if the dragon has died
		}
	}
}
