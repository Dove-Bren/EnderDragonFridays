package com.SkyIsland.EnderDragonFridays.Boss.Component;

/**
 * Types of targeting methods used in cannons.
 * 
 * <p><ul>
 * <li><b>NEAREST</b>: attack the closest valid target</li>
 * <li><b>MOSTDAMAGE</b>: attack the entity that has done the most damage</li>
 * <li><b>RANDOM</b>: pick a player in the world at random to attack</li>
 * <li><b>ALL_CYCLE</b>: attack all players in the world, one at a time in a cyclic fashion</li> 
 * </ul></p>
 * @author Skyler
 *
 */
public enum TargetType {
	
	NEAREST,
	MOSTDAMAGE,
	RANDOM,
	ALL_CYCLE;
	
}
