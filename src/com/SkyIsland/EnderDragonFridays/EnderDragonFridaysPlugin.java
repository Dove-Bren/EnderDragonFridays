package com.SkyIsland.EnderDragonFridays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderDragonFridaysPlugin extends JavaPlugin {
	
	private EnderDragon dragon;
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onLoad() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		/**
		 * Temp command that creates the dragon
		 */
		if (cmd.getName().equalsIgnoreCase("makestuff"))
		if (dragon.isLiving()){
			LivingEntity drags;
			drags = (LivingEntity) ((Player) sender).getWorld().spawnEntity(((Player) sender).getLocation().add(0, 20, 0), EntityType.ENDER_DRAGON);
			drags.setMaxHealth(((Player) sender).getWorld().getPlayers().size() * drags.getMaxHealth()); //scale up with players
			drags.setCustomName("Young Ender Dragon");
		}
		
		
		
		return false;
	}
}
