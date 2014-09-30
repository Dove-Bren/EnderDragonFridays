package com.SkyIsland.EnderDragonFridays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
		
		if (cmd.getName().equalsIgnoreCase("makestuff"))
		if (dragon.isLiving()){
			dragon = new EnderDragon(( (Player) sender).getLocation().add(0, 30, 0), "Dragon");
			return true;
		}
		
		
		
		return false;
	}
}
