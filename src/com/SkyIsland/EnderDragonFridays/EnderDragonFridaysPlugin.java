package com.SkyIsland.EnderDragonFridays;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.EnderDragonFridays.Dragon.Dragon;
import com.SkyIsland.EnderDragonFridays.Dragon.EnderDragon;
import com.SkyIsland.EnderDragonFridays.Dragon.MegaDragon;
import com.SkyIsland.EnderDragonFridays.Name.BossNameGenerator;
import com.griefcraft.lwc.LWCPlugin;

/**
 * The EnderDragonFridaysPlugin makes an Ender Dragon appear once a week in the end.
 * Upon killing it, awesome custom loot is dropped.
 *
 */
public class EnderDragonFridaysPlugin extends JavaPlugin {
	
	private Dragon dragon;
	private BossNameGenerator bossName;
	
	public static LWCPlugin lwcPlugin;
	public static EnderDragonFridaysPlugin plugin;
	
	private static final String configFilename = "config.yml";
	private File configFile;
	private YamlConfiguration config;
	
	private String worldName;
	
	public void onLoad() {
		EnderDragonFridaysPlugin.plugin = this;
	}
	
	public void onEnable() {
		dragon = null;
		bossName = new BossNameGenerator();
		lwcPlugin = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
		if (lwcPlugin == null) {
			getLogger().info("lwc is null");
		}
		load();
	}
	
	public void onDisable() {
		lwcPlugin = null;
		if (dragon != null && dragon.isAlive()){
			dragon.killDragon();
		}
		dragon = null;
		save();
	}
	
	public void reload() {
		onDisable();
		onEnable();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		/**
		 * Temp command that creates the dragon
		 */
		if (cmd.getName().equalsIgnoreCase("startdragonfight")) {
			
			//!!!!If you change something here, change it in EnderDragonFriday command as well!!!!!//
			
			
			
			
			if (Bukkit.getWorld(worldName) == null){
				sender.sendMessage("World does not exist!");
				return false;
			}
			
			if (dragon != null && dragon.isAlive()) {
				sender.sendMessage("Fight already in progress!");
			}
			else {
				//dragon = new MegaDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), "Mega Dragon");
				dragon = new EnderDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), bossName.getName());
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("killdragon")) {
			if (dragon == null || !dragon.isAlive()) {
				sender.sendMessage("No fight currently engaged");
				return true;
			}
			dragon.killDragon();
			dragon = null;
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("killalldragons")) {
			for (Entity e : ((Player) sender).getWorld().getEntities()) {
				if (e.getType() == EntityType.ENDER_DRAGON) {
					LivingEntity dragon = (LivingEntity) e;
					dragon.damage(dragon.getMaxHealth());
				}
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("enderdragonfridays") || cmd.getName().equalsIgnoreCase("edf")) {
			if (args.length == 0) {
				return false;
			}
			
			if (args[0].equalsIgnoreCase("start")) {
				if (Bukkit.getWorld(worldName) == null){
					sender.sendMessage("World does not exist!");
					return false;
				}
				
				if (dragon != null && dragon.isAlive()) {
					sender.sendMessage("Fight already in progress!");
				}
				else {
					//start a dragon fight. If they put mega after, it will be a mega dragon
					if (args.length >= 2 && args[1].equalsIgnoreCase("mega")) {
						dragon = new MegaDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), "Mega Dragon");
						
						return true;
					}
					//else they didn't sepcify or it is something else
					dragon = new EnderDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), bossName.getName());
					return true;
				}
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				sender.sendMessage("Cannot reload because it's not properly implemented loooooser");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("win")) {
				dragon.killDragon();
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("windragonwars")) {
			dragon.killDragon();
			
			return true;
		}
		
		return false;
	}
			
	public void load(){
		if (!this.getDataFolder().exists()){
			this.getDataFolder().mkdir();
		}
		configFile = new File(this.getDataFolder(), configFilename);
		if (!configFile.exists()){
			try {
				configFile.createNewFile();
				config = YamlConfiguration.loadConfiguration(configFile);
				config.set("world", "world_the_end");
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		worldName = config.contains("world") ? config.getString("world") : "world_the_end";
	}
	
	public void save(){
		try {
			config.set("world", worldName);
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
