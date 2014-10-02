package com.SkyIsland.EnderDragonFridays;


import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.SkyIsland.EnderDragonFridays.Items.ChestContentGenerator;

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
			
			dragon = new EnderDragon(plugin, level, drags);
		}
	}
	
	public void killDragon() {
		if (dragon == null || !dragon.isLiving()) {
			return;
		}
		
		dragon.kill();
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
		
		Map<Player, Inventory> rewardMap = ChestContentGenerator.generate(dragon.getLevel(), dragon.getDamageMap());
		
		endFight();
		
	}
}
