package com.SkyIsland.EnderDragonFridays;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface Dragon {
	
	public boolean isLiving();
	
	public boolean isAlive();
	
	public LivingEntity getDragon();
	
	public Player getMostDamage();
	
	public void win();
	
	public void spawnRewards(Map<UUID, Inventory> map);
	
	public void killDragon();
	
	
}
