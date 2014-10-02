package com.SkyIsland.EnderDragonFridays;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EnderDragon implements Listener {
	
	private int level;							//The level of the dragon
	private LivingEntity dragon;				//The actual Entity for the Ender Dragon
	private Map<UUID, Double> damageMap;		//The damange each player has done to the ender dragon
	private FireballCannon cannon;				//The cannon which fires fireballs at the players
	
	/**
	 * Creates a default enderdragon
	 * @param plugin The EnderDragonFridays plugin this dragon is associated with
	 * @param level The level of the dragon
	 * @param loc
	 * @param name it's name
	 */
	public EnderDragon(int level, Location loc, String name) {
		dragon = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
		if (name != null && name.length() > 0) {
			dragon.setCustomName(name);
			dragon.setCustomNameVisible(true);
		}
		
		this.level = level;
		
		if (this.level <= 0) {
			this.level = 1; //reset to lvl 1 with invalid input
		}
		
		dragon.setMaxHealth(dragon.getMaxHealth() * level);
		dragon.setHealth(dragon.getMaxHealth());

		//register this as an event listener
		EnderDragonFridaysPlugin.plugin.getServer().getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);
		
		
		damageMap = new HashMap<UUID, Double>();
		
		cannon = new FireballCannon(this, 500, 2000);
		cannon.start();
		EnderDragonFridaysPlugin.plugin.getLogger().info("Ender Dragon Created!");
		

		//register this as an event listener
		EnderDragonFridaysPlugin.plugin.getServer().getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);
	}
	
	public EnderDragon(int level, LivingEntity dragon) {
		this.dragon = dragon;
		this.level = level;
		
		if (level <= 0) {
			this.level = 1;
		}

		//register this as an event listener
		EnderDragonFridaysPlugin.plugin.getServer().getPluginManager().registerEvents(this, EnderDragonFridaysPlugin.plugin);
		
		
		damageMap = new HashMap<UUID, Double>();
		cannon = new FireballCannon(this, 500, 2000);
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
		for (Entry<UUID, Double> entry : damageMap.entrySet()) {
			if (entry.getValue() > max) {
				player = Bukkit.getPlayer(entry.getKey());
			}
		}
		
		
		return player;
	}
	
	public void kill() {
		dragon.damage(dragon.getMaxHealth());
	}
	
	@EventHandler
	public void dragonDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity().equals(dragon)) {
			//if it was our dragon
			if (event.getDamager() instanceof Player) {
				if (!damageMap.containsKey((Player) event.getDamager())) {
					//new player that's damaged it
					damageMap.put(((Player) event.getDamager()).getUniqueId(), event.getDamage() / dragon.getMaxHealth());
					}
				else {
					double old = damageMap.get((Player) event.getDamager());
					old = old + (event.getDamage() / dragon.getMaxHealth());
					damageMap.put(((Player) event.getDamager()).getUniqueId(), old); 
					}
			}
			else if (event.getDamager() instanceof Projectile) {
				Projectile proj = (Projectile) event.getDamager();
				UUID cause = ((Player) proj.getShooter()).getUniqueId();
				if (!damageMap.containsKey(cause)) {
					//new player that's damaged it
					damageMap.put(cause, event.getDamage() / dragon.getMaxHealth());
					}
				else {
					double old = damageMap.get(cause);
					old = old + (event.getDamage() / dragon.getMaxHealth());
					damageMap.put(cause, old); 
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

	public Map<UUID, Double> getDamageMap() {
		return this.damageMap;
	}
	
	public int getLevel() {
		return level;
	}
}
