package com.SkyIsland.EnderDragonFridays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.EnderDragonFridays.Name.BossNameGenerator;
import com.griefcraft.lwc.LWCPlugin;

public class EnderDragonFridaysPlugin extends JavaPlugin {
	
	private EnderDragonFight fight;
	private BossNameGenerator bossName;
	
	private LWCPlugin lwcPlugin;
		
	
	public void onEnable() {
		fight = null;
		bossName = new BossNameGenerator();
		lwcPlugin = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
		if (lwcPlugin == null) {
			getLogger().info("lwc is null");
		}
	}
	
	public void onDisable() {
		if (fight != null) {
			fight.endFight();
		}
		lwcPlugin = null;
		fight = null;
	}
	
	public void onLoad() {
		
	}
	
	public void reload() {
		onDisable();
		onEnable();
	}
	
	public LWCPlugin getLWC() {
		return this.lwcPlugin;
	}
	
	public void closeFight() {
		this.fight = null;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		/**
		 * Temp command that creates the dragon
		 */
		if (cmd.getName().equalsIgnoreCase("makestuff")) {
			if (fight == null) {
				fight = new EnderDragonFight(this, ((Player) sender).getLocation());
				fight.CreateDragon(((Player) sender).getWorld().getPlayers().size(), ((Player) sender).getLocation(), "Young Ender Dragon");
			}
			else {
				sender.sendMessage("Fight already in progress!");
			}
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("killdragon")) {
			if (fight==null) {
				sender.sendMessage("No fight currently engaged");
				return true;
			}
			fight.killDragon();
			fight = null;
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
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					getLogger().info("Reloading...");
					this.reload();
					getLogger().info("Reload complete!");
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("windragonwars")) {
			this.getLogger().info("winning...");
			this.fight.win();
			
			return true;
		}
		
		return false;
	}
}
