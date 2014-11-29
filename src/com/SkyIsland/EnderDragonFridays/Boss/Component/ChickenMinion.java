package com.SkyIsland.EnderDragonFridays.Boss.Component;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.SkyIsland.EnderDragonFridays.EnderDragonFridaysPlugin;
import com.SkyIsland.EnderDragonFridays.Boss.Boss;

public class ChickenMinion {
	
	private Chicken chicken;
	
	private Wolf vehicle;
	
	private Boss boss;
	
	public ChickenMinion(Boss boss, Location loc) {
		this.boss = boss;
		vehicle = (Wolf) loc.getWorld().spawnEntity(loc, EntityType.WOLF);
		chicken = (Chicken) loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
		vehicle.setPassenger(chicken);
		vehicle.setBaby();
		vehicle.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999999, 1));
		vehicle.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
		vehicle.setTamed(false);
		vehicle.setAngry(true);
		vehicle.setRemoveWhenFarAway(false);
		chicken.setRemoveWhenFarAway(false);
		vehicle.setAgeLock(true);
		
		List<Player> players = loc.getWorld().getPlayers();
		vehicle.setTarget(players.get(EnderDragonFridaysPlugin.rand.nextInt(players.size())));
		//lulz attack a random player in the world
		
		
	}
	
	
	@EventHandler
	public void regroup(ChickenRegroupEvent e) {
		//time to regroup. Play a teleport effect and kill self
		if (!chicken.isDead()) {
			chicken.getWorld().playEffect(chicken.getLocation(), Effect.ENDER_SIGNAL, 2003);
			chicken.remove();
		}
		if (!vehicle.isDead()) {
			vehicle.remove();
		}
	}
	
	@EventHandler
	public void chickenDeath(EntityDeathEvent e) {
		if (e.getEntity() == chicken) {
			if (!vehicle.isDead()) {
				vehicle.remove();
			}
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent e) {
		if (!vehicle.isAngry() || vehicle.getTarget() == null) {
			//get a new target
			vehicle.setAngry(true);
			List<Player> players = vehicle.getWorld().getPlayers();
			vehicle.setTarget(players.get(EnderDragonFridaysPlugin.rand.nextInt(players.size())));
		}
	}
	
	
	
	public Chicken getChicken() {
		return this.chicken;
	}

}
