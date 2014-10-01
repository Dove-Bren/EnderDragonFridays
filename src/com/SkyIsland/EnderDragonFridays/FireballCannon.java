package com.SkyIsland.EnderDragonFridays;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LargeFireball;
import org.bukkit.util.Vector;

/**
 * 
 * @author Skyler
 * @note This could be extracted out to 'body part' class. There oculd be two. One that's independent and one that just
 * 			exists. I.e. one that extends thread and one that doens't get it's own.
 */
public class FireballCannon extends Thread {
	
	private double d_min;
	private double d_max;
	private double d_incr;
	private int d_incr_range;
	private Random rand;
	private EnderDragon dragon;
	
	public FireballCannon(EnderDragon dragon, double min_delay, double max_delay) {
		this(dragon, min_delay, max_delay, (max_delay-min_delay)/10); //default to 1/10 the range as increments
	}
	
	public FireballCannon(EnderDragon dragon, double min_delay, double max_delay, double increments) {
		this.dragon = dragon;
		
		this.d_min = min_delay;
		this.d_max = max_delay;
		
		//make sure min and max aren't reversed
		if (d_min > d_max) {
			d_max = min_delay;
			d_min = max_delay;
		}
		
		//make sure increments aren't negative
		d_incr = Math.abs(increments);
		
		//we need to make sure the increments are sound
		if ( ((d_max - d_min) / d_incr) % 1 <= .0001) {
			//if increments doesn't 'evenly' divide our range, we're in trouble
			//so we'll round to nearest increment that would
			float even = (int) Math.round((d_max - d_min) / d_incr);
			d_incr = (d_max - d_min) / even; 
		}
		
		d_incr_range = (int) Math.round((d_max - d_min) / d_incr); //store so we don't have to do this math over and over again
		
		//create out random
		rand = new Random();
	}
	
	@Override
	public void run() {
		LivingEntity dDragon = dragon.getDragon();
		while (true) {
			//very first, make sure dragon is still alive. If not, kill self
			if (dragon == null || !dragon.isLiving()) {
				break;
			}
			//sleep first so it doesn't fire balls right away
			try {
				Long time = (long) (d_min + (d_incr * (rand.nextInt(d_incr_range))));
				Bukkit.getServer().getLogger().info("wont fire for: " + time + " milliseconds!");
				sleep(time);
			} catch (InterruptedException e) {
				//do nothing. WE DONT GIVE A MAGNETIC FLUX
			}
			
			//make sure there are players to fire at
			if (dDragon.getWorld().getPlayers().isEmpty()) {
				continue;
			}
			
			//there are players in the world
			//get player doing the most damage
			Player target = dragon.getMostDamage();
			if (target == null) {
				//nobody has hit it yet
				List<Player> players = dDragon.getWorld().getPlayers();
				if (!players.isEmpty())
					target = players.get(rand.nextInt(players.size()));
				else {
					continue; //no players in world. keep sleeping.
				}
			}
			
			Vector launchV;
			Location pPos, dPos;
			dPos = dDragon.getEyeLocation();
			pPos = target.getEyeLocation();
			//launchV = new Vector(dPos.getX(), dPos.getY(), dPos.getZ()).subtract(  new Vector(pPos.getX(), pPos.getY(), pPos.getZ())    );
			launchV = pPos.toVector().subtract(dPos.toVector());
			
			LargeFireball f = dDragon.launchProjectile(LargeFireball.class);
			f.setDirection(launchV.normalize());
		}
	}
}
