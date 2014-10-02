package com.SkyIsland.EnderDragonFridays.Items;

import org.bukkit.enchantments.Enchantment;

/**
 * Defines a loot enchantment that can be applied to loot.
 * <p>This class is made to tie together the weight of an enchantment, any hard limit on the enchantment
 * level that may exist, and the enchantment itself.</p>
 * <p>This class is abstract, meaning there's no such thing as a general loot enchantment. Instead, there are
 * specific implementations of a LootEnchantment (see {@link com.SkyIsland.EnderDragonFridays.Items.Enchantments.BowEnchantment BowEnchantment}).<br />
 * This class is not an interface because you cannot specify class fields in an interface without them being static final.
 * @author Skyler
 *
 */
public class LootEnchantment {
	
	/**
	 * The weight of the enchantment. In other words, how <i>heavy</i> is this enchantment? That was a pun.
	 * <p>The weight of an enchantment affects how much a level of this enchantment costs for a piece of loot.
	 * <br />Enchantments with more weight have less chance of getting on an item and a significantly reduced
	 * final enchantment level.</p>
	 */
	private double weight;
	
	/**
	 * What enchantment this lootenchantment carries. This is an enchantment enumerated in
	 * {@link org.bukkit.enchantments.Enchantment}
	 */
	private Enchantment enchantment;
	
	/**
	 * A hard level cap. This is the final word on how high an enchantment's level can go. For example, it makes
	 * sense to have a hard level cap of 1 on Infinity. We don't want to cap Sharpness to V, however, as sharpness
	 * VI+ actually mean something. Infinity II does not. This also prevents Protection 80. 
	 */
	private int hardLevelCap;
	
	

	public LootEnchantment(Enchantment enchantment,  Double weight, int cap) {
		this.enchantment = enchantment;
		this.weight = weight;
		this.hardLevelCap = cap;
	}
	
	/**
	 * Calculate the highest level of this enchantment you can get with the passed {@link com.SkyIsland.EnderDragonFridays.Items.LootGenerator#itemQuality quality}. 
	 * @param quality
	 * @return
	 */
	public int calculateLevelCap(double quality) {
		//default follows the algo that Skyler created: the cap is (2 * quality) - enchantment.weight rounded down
		int cap;
		cap = (int) Math.floor((2 * quality) - weight);
		
		//make sure this number doesn't exceed the hard cap
		if (cap > hardLevelCap) {
			cap = hardLevelCap;
		}
		
		return cap;
	}
	
	/**
	 * Returns the level of enchantment an item can get with the given quality and enchanting points.
	 * @param quality The quality, as obtained through {@link com.SkyIsland.EnderDragonFridays.Items.LootGenerator#itemQuality quality}
	 * @param enchantingPoints How many points this has towards getting any enchantment. 
	 * @return
	 */
	public int getEnchantmentLevel(double quality, double enchantingPoints) {
		int level = 0, cap;
		cap = calculateLevelCap(quality);
		
		level = (int) (enchantingPoints / weight); //how many levels could we get with just our points vs weight?
		
		if (level > cap) {
			//we can get out more enchantment levels than we have points for
			level = cap; //just take out max
		}
		
		return level;		
	}
	
	public double getWeight() {
		return weight;
	}
	
	public Enchantment getEnchantment() {
		return enchantment;
	}

	
	
}
