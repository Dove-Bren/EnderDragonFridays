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

import com.SkyIsland.EnderDragonFridays.Name.BossNameGenerator;
import com.griefcraft.lwc.LWCPlugin;

/**
 * The EnderDragonFridaysPlugin makes an Ender Dragon appear once a week in the end.
 * Upon killing it, awesome custom loot is dropped.
 *
 */
public class EnderDragonFridaysPlugin extends JavaPlugin {
	
	private EnderDragon dragon;
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
			if (Bukkit.getWorld(worldName) == null){
				sender.sendMessage("World does not exist!");
				return false;
			}
			
			if (dragon != null && dragon.isAlive()) {
				sender.sendMessage("Fight already in progress!");
			}
			else {
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
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("windragonwars")) {
			this.getLogger().info("winning...");
			this.dragon.win();
			
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
