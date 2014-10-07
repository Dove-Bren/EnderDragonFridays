package com.SkyIsland.EnderDragonFridays.Dragon.Cannon.Events;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FireBlazeEvent extends CannonFireEvent {

	public FireBlazeEvent(LivingEntity shooter, Player target, Location fromLocation) {
		super(shooter, target, fromLocation);
	}
	
	
}
