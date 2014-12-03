package com.SkyIsland.EnderDragonFridays.Boss.Component;

import org.bukkit.entity.Chicken;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ChickenLandEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();

	private final Chicken chicken;
	
	public ChickenLandEvent(Chicken chicken) {
		this.chicken = chicken;
	}
	
	public Chicken getChicken() {
		return this.chicken;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}	
	
}
