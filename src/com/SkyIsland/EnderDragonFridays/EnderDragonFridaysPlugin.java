package com.SkyIsland.EnderDragonFridays;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.EnderDragonFridays.Boss.Boss;
import com.SkyIsland.EnderDragonFridays.Boss.EnderDragon;
import com.SkyIsland.EnderDragonFridays.Boss.JackTheSkeleton;
import com.SkyIsland.EnderDragonFridays.Boss.MegaDragon;
import com.SkyIsland.EnderDragonFridays.Boss.Turkey;
import com.SkyIsland.EnderDragonFridays.Name.BossNameGenerator;

/**
 * The EnderDragonFridaysPlugin makes an Ender Boss appear once a week in the end.
 * Upon killing it, awesome custom loot is dropped.
 *
 */
public class EnderDragonFridaysPlugin extends JavaPlugin {
	
	private BossNameGenerator bossName;
	
	public static EnderDragonFridaysPlugin plugin;
	
	public static Random rand;
	
	public static final String savePrefix = "FightSave_";
	
	private Set<DragonFight> fights;
	
	public void onLoad() {
		EnderDragonFridaysPlugin.plugin = this;
	}
	
	public void onEnable() {
		fights = new HashSet<DragonFight>();
		bossName = new BossNameGenerator();
		
		rand = new Random();
	}
	
	public void onDisable() {
		if (!fights.isEmpty()) {
			Iterator<DragonFight> it = fights.iterator();
			while (it.hasNext()) {
				it.next().stop(false);
				it.remove();
			}
		}
	}
	
	public void reload() {
		onDisable();
		onEnable();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if (cmd.getName().equalsIgnoreCase("killalldragons")) {
			for (Entity e : ((Player) sender).getWorld().getEntities()) {
				if (e.getType() == EntityType.ENDER_DRAGON) {
					LivingEntity dragon = (LivingEntity) e;
					dragon.damage(dragon.getMaxHealth());
				}
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("enderdragonfridays")) {
			if (args.length == 0) {
				return false;
			}
			
			if (args[0].equalsIgnoreCase("start")) {
				commandStart(sender, args);
				return true;
//				if (Bukkit.getWorld(worldName) == null){
//					sender.sendMessage("World does not exist!");
//					return false;
//				}
//				
//				if (boss != null && boss.isAlive()) {
//					sender.sendMessage("Fight already in progress!");
//				}
//				else {
//					//start a boss fight. If they put mega after, it will be a mega boss
//					if (args.length >= 2 && args[1].equalsIgnoreCase("mega")) {
//						boss = new MegaDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), "Mega Boss");
//						
//						return true;
//					}
//					if (args.length >= 2 && args[1].equalsIgnoreCase("halloween") && Bukkit.getWorld(worldName).getDifficulty() != Difficulty.PEACEFUL) {
//						boss = new JackTheSkeleton(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), (new WitherNameGenerator()).getName());
//						return true;
//					}
//					if (args.length >= 2 && args[1].equalsIgnoreCase("thanksgiving") && Bukkit.getWorld(worldName).getDifficulty() != Difficulty.PEACEFUL) {
//						boss = new Turkey(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), (new TurkeyNameGenerator()).getName());
//						return true;
//					}
//					//else they didn't sepcify or it is something else
//					boss = new EnderDragon(Bukkit.getWorld(worldName), Bukkit.getWorld(worldName).getPlayers().size(), bossName.getName());
//					return true;
//				}
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				commandReload(sender, args);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("create")) {
				commandCreate(sender, args);
				return true;
			}
		}
		
		return false;
	}
	
	private void commandCreate(CommandSender sender, String[] args) {
		if (args.length < 3 || args.length > 4) {
			sender.sendMessage("/edf create " + ChatColor.DARK_PURPLE + "[sessionName]" + ChatColor.RESET + " [type] {basedifficulty}");
			return;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command!");
			return;
		}
		
		//make sure there aren't any with that name already
		if (getFight(args[1]) != null) {
			sender.sendMessage(ChatColor.DARK_RED + "A session named " + ChatColor.DARK_PURPLE + args[1] 
					+ ChatColor.DARK_RED + " already exists!" + ChatColor.RESET);
			return;
		}
		
		//go ahead with it.
		Player player = (Player) sender;
		
		//count players
		int playerCount = player.getWorld().getPlayers().size();
		
		Boss boss;
		if (args[2].equals("mega")) {
			boss = new MegaDragon(playerCount, bossName.getName());
		} else if (args[2].equals("halloween")) {
			boss = new JackTheSkeleton(playerCount, bossName.getName());
		} else if (args[2].equals("thanksgiving")) {
			boss = new Turkey(playerCount, bossName.getName());
		} else {
			//just do default dragon
			boss = new EnderDragon(playerCount, bossName.getName());
		}
		
		int base;
		if (args.length == 4) {
			try {
				base = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage("Unable to parse number: " + args[3]);
				boss = null;
				return;
			}
		} else {
			base = 5;
		}
		
		DragonFight fight = new DragonFight(args[1],
				player.getWorld(),
				boss, 
				playerCount,
				base,
				player.getLocation());

		fights.add(fight);
		
		sender.sendMessage("Successfully created session: " + ChatColor.DARK_PURPLE + fight.getName() + ChatColor.RESET);
		sender.sendMessage("Chest location set to your position!");
	}
	
	private void commandStart(CommandSender sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("/edf start " + ChatColor.DARK_PURPLE + "[sessionName]" + ChatColor.RESET);
			return;
		}
		
		DragonFight fight = getFight(args[1]);
		
		if (fight == null) {
			sender.sendMessage(ChatColor.DARK_RED + "Unable to find fight " + ChatColor.DARK_PURPLE + args[1] + ChatColor.RESET);
			return;
		}
		
		if (fight.isStarted()) {
			sender.sendMessage(ChatColor.DARK_RED + "The fight " + ChatColor.DARK_PURPLE + fight.getID() 
				+ ChatColor.DARK_RED + " is already started!" + ChatColor.RESET);
			return;
		}
		
		fight.start();
		sender.sendMessage(ChatColor.DARK_GREEN + "Fight successfully started!" + ChatColor.RESET);
	}
	
	private void commandReload(CommandSender sender, String[] args) {
		; //not implemented
	}
	
	/**
	 * Tries to look up a fight by it's sessionName
	 * @param sessionNAme
	 * @return The fight, if we have record of it. Null otherwise
	 */
	private DragonFight getFight(String sessionName) {
		for (DragonFight fight : fights) {
			if (fight.getName().equals(sessionName)) {
				return fight;
			}
		}
		
		return null;
	}
}
