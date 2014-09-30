package com.SkyIsland.EnderDragonFridays.Items;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Generates a chest-ful of equipment for players!<br />
 * Takes a mapping of players to their contribution and generates loot. The results are returned
 * as a map between players and chests.<br />The chest the players are mapped to contain the loot
 * generated for that player.
 * @author Skyler
 * @note This should be abstracted out when converting to a framework. Let people make their own ChestGenerators that don't,<br />
 * for example, exclude 0% contribution
 */
public class ChestContentGenerator {
	
	public static Map<Player, Inventory> generate(Map<Player, Double> inputMap) {
		Chest chest;
		for (Player player : inputMap.keySet()) {
			//Create a chest
			
		}
		
		return null;
	}
}
