package com.SkyIsland.EnderDragonFridays;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EnderDragon implements Listener {
	private LivingEntity dragon;
	private EnderDragonFridaysPlugin plugin;
	private Map<Player, Double> damageMap;
	private FireballCannon cannon;
	
	/**
	 * Creates a default enderdragon
	 * @param loc
	 * @param name it's name
	 */
	public EnderDragon(EnderDragonFridaysPlugin plugin, Location loc, String name) {
		this.setPlugin(plugin);
		dragon = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
		if (name != null && name.length() > 0) {
			dragon.setCustomName(name);
			dragon.setCustomNameVisible(true);
		}


		//register this as an event listener
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		
		
		damageMap = new HashMap<Player, Double>();
		
		cannon = new FireballCannon(this, 3000, 10000);
		cannon.start();
		plugin.getLogger().info("Ender Dragon Created!");
	}
	
	public EnderDragon(EnderDragonFridaysPlugin plugin, LivingEntity dragon) {
		this.dragon = dragon;

		//register this as an event listener
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		
		
		damageMap = new HashMap<Player, Double>();
		cannon = new FireballCannon(this, 3000, 10000);
		cannon.start();
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
		if (damageMap.isEmpty()) {
			return null;
		}
		
		Player player = null;
		double max = -999999.0;
		for (Entry<Player, Double> entry : damageMap.entrySet()) {
			if (entry.getValue() > max) {
				player = entry.getKey();
			}
		}
		
		
		return player;
	}
	
	@EventHandler
	public void dragonDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity().equals(dragon)) {
			//if it was our dragon
			if (event.getDamager() instanceof Player) {
				if (!damageMap.containsKey((Player) event.getDamager())) {
					//new player that's damaged it
					damageMap.put((Player) event.getDamager(), event.getDamage() / dragon.getMaxHealth());
				}
				else {
					double old = damageMap.get((Player) event.getDamager());
					old = old + (event.getDamage() / dragon.getMaxHealth());
					damageMap.put((Player) event.getDamager(), old); 
				}
			}
		}
	}
	
	@EventHandler
	public void dragonDeath(EntityDeathEvent event) {
		if (event.getEntity().equals(dragon)) {
			//if the dragon has died
		}
	}

	public EnderDragonFridaysPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(EnderDragonFridaysPlugin plugin) {
		this.plugin = plugin;
	}
}
