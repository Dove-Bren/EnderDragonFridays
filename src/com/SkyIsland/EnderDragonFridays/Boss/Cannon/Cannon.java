package com.SkyIsland.EnderDragonFridays.Boss.Cannon;

import com.SkyIsland.EnderDragonFridays.Boss.Component.BossComponent;
import com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType;


public abstract class Cannon extends BossComponent {
	
	/**
	 * The mode of selecting a target. See {@link com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType TargetType}
	 * for more info.
	 */
	protected TargetType targetType;
	
	/**
	 * Stores the smallest amount of time inbetween cannon fires
	 */
	protected double delayMin;
	
	/**
	 * Stores the largest amount of time inbetween cannon fires
	 */
	protected double delayMax;
	
	/**
	 * When time-till-fire is calculated, we choose from a small set of integers instead of a continuous
	 * range of floats. delay_incr keeps track of the smallest difference between calculated firing times
	 * possible.
	 */
	protected double delayIncr;
	
	/**
	 * Because we're using a subset of ints, we need a random number from a range to pick how long our
	 * delay is actually going to be. This variable is here because we very frequently calculate<br /><br />
	 * <center>round( [delay_max - delay_min] / delay_incr)</center>
	 * <br />many times. To speed this up, we store it in a variable.<br />
	 * <b>This is not a good solution, but I can't bring myself to calculate that every time. It's always
	 * the same number!</b>
	 */
	protected int delayRange;
	
	/**
	 * Returns the {@link com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType TargetType} used when
	 * selecting a target to fire on.
	 * @return
	 */
	public TargetType getTargetType() {
		return this.targetType;
	}
	
	
	/**
	 * Returns the shortest amount of time a cannon can go between firing.
	 * @return The time in ticks
	 */
	public double getDelayMin() {
		return delayMin;
	}


	/**
	 * Returns the longest amount of time a cannon can go between firing.
	 * @return The time in ticks
	 */
	public double getDelayMax() {
		return delayMax;
	}


	/**
	 * Returns how big increments are between min and max times.
	 * @return The time in ticks
	 */
	public double getDelayIncr() {
		return delayIncr;
	}



	/**
	 * Gives the cannon the passed method of selecting a target to fire on.
	 * @param type Mode of selecting the target.
	 * @see {@link com.SkyIsland.EnderDragonFridays.Boss.Component.TargetType TargetType}
	 */
	public void setTargetType(TargetType type) {
		this.targetType = type;
	}
	
	/*
	 * setters for our delay variables intentionally left out!!!
	 * values are to be calculated in the constructors of the cannons to follow the guidelines of the cannons.
	 * This allows for the different delays corresponding to different aspects of the in-game objects to be
	 * accounted for on an individual basis.
	 */
	
}
