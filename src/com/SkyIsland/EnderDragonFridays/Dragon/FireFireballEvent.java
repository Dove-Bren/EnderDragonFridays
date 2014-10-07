package com.SkyIsland.EnderDragonFridays.Dragon;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FireFireballEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player target;
	
	private final LivingEntity shooter;
	
	private final Location fromLocation;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public FireFireballEvent(LivingEntity shooter, Player target, Location fromLocation) {
		this.target = target;
		this.shooter = shooter;
		this.fromLocation = fromLocation;
	}
	
	public Player getTarget() {
		return this.target;
	}
	
	public LivingEntity getShooter() {
		return this.shooter;
	}
	
	public Location getFromLocation() {
		return this.fromLocation;
	}

}
