package com.SkyIsland.EnderDragonFridays;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.EnderDragonFridays.Boss.Boss;
import com.SkyIsland.EnderDragonFridays.Boss.EnderDragon;
import com.SkyIsland.EnderDragonFridays.Boss.JackTheSkeleton;
import com.SkyIsland.EnderDragonFridays.Boss.MegaDragon;
import com.SkyIsland.EnderDragonFridays.Boss.Turkey;
import com.SkyIsland.EnderDragonFridays.Name.BossNameGenerator;
import com.SkyIsland.EnderDragonFridays.Name.TurkeyNameGenerator;
import com.SkyIsland.EnderDragonFridays.Name.WitherNameGenerator;
import com.griefcraft.lwc.LWCPlugin;

/**
 * The EnderDragonFridaysPlugin makes an Ender Boss appear once a week in the end.
 * Upon killing it, awesome custom loot is dropped.
 *
 */
public class EnderDragonFridaysPlugin extends JavaPlugin {
	
	private Boss boss;
	private BossNameGenerator bossName;
	
	public static LWCPlugin lwcPlugin;
	public static EnderDragonFridaysPlugin plugin;
	public static Random rand;
	
	private static final String configFilename = "config.yml";
	private File configFile;
	private YamlConfiguration config;
	
	private String worldName;
	
	public void onLoad() {
		EnderDragonFridaysPlugin.plugin = this;
	}
	
	public void onEnable() {
		boss = null;
		bossName = new BossNameGenerator();
		lwcPlugin = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
		if (lwcPlugin == null) {
			getLogger().info("lwc is null");
		}
		
		rand = new Random();
		
		load();
	}
	
	public void onDisable() {
		lwcPlugin = null;
		if (boss != null && boss.isAlive()){
			boss.kill();
		}
		boss = null;
		save();
	}
	
	public void reload() {
		onDisable();
		onEnable();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		/**
		 * Temp command that creates the boss
		 */
		if (cmd.getName().equalsIgnoreCase("startdragonfight")) {
			
			//!!!!If you change something here, change it in EnderDragonFriday command as well!!!!!//
			
			
			
			
			if (Bukkit.getWorld(worldName) == null){
				sender.sendMessage("World does not exist!");
				return false;
			}
			
			if (boss != null && boss.isAlive()) {
				sender.sendMessage("Fight already in progress!");
			}
			else {
				//boss = new MegaDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), "Mega Boss");
				boss = new EnderDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), bossName.getName());
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("killdragon")) {
			if (boss == null || !boss.isAlive()) {
				sender.sendMessage("No fight currently engaged");
				return true;
			}
			boss.kill();
			boss = null;
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
				
				if (boss != null && boss.isAlive()) {
					sender.sendMessage("Fight already in progress!");
				}
				else {
					//start a boss fight. If they put mega after, it will be a mega boss
					if (args.length >= 2 && args[1].equalsIgnoreCase("mega")) {
						boss = new MegaDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), "Mega Boss");
						
						return true;
					}
					if (args.length >= 2 && args[1].equalsIgnoreCase("halloween") && Bukkit.getWorld(worldName).getDifficulty() != Difficulty.PEACEFUL) {
						boss = new JackTheSkeleton(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), (new WitherNameGenerator()).getName());
						return true;
					}
					if (args.length >= 2 && args[1].equalsIgnoreCase("thanksgiving") && Bukkit.getWorld(worldName).getDifficulty() != Difficulty.PEACEFUL) {
						boss = new Turkey(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), (new TurkeyNameGenerator()).getName());
						return true;
					}
					//else they didn't sepcify or it is something else
					boss = new EnderDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), bossName.getName());
					return true;
				}
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				sender.sendMessage("Cannot reload because it's not properly implemented loooooser");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("win")) {
				boss.kill();
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("windragonwars")) {
			boss.kill();
			
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
