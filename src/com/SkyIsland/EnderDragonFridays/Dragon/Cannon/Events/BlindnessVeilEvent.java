package com.SkyIsland.EnderDragonFridays.Dragon.Cannon.Events;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class BlindnessVeilEvent extends CannonFireEvent {

	public BlindnessVeilEvent(LivingEntity shooter, Player target, Location fromLocation) {
		super(shooter, target, fromLocation);
	}
	
	
}
