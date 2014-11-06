package com.SkyIsland.EnderDragonFridays.Dragon.Cannon;

/**
 * Types of targeting methods used in cannons.
 * 
 * <p><ul>
 * <li><i>NEAREST</i>: attack the closest valid target</li>
 * <li><i>MOSTDAMAGE</i>: attack the entity that has done the most damage</li>
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
