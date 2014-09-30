package com.SkyIsland.EnderDragonFridays.Name;


public interface NameGenerator  {
	
	/**
	 * Gets a full name combined in one string.
	 * @return
	 */
	public String getName();
	
	/**
	 * Gets a random first name
	 * @return
	 */
	public String getFirstName();
	
	/**
	 * Gets a random last name
	 * @return
	 */
	public String getLastName();
}
