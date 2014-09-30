package com.SkyIsland.EnderDragonFridays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderDragonFridaysPlugin extends JavaPlugin {
	
	private EnderDragonFight fight;
	
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
		if (cmd.getName().equalsIgnoreCase("makestuff")) {
			if (fight == null) {
				fight = new EnderDragonFight(this);
				fight.CreateDragon( ((Player) sender).getLocation(), "Young Ender Dragon");
			}
		}
		
		
		
		return false;
	}
}
