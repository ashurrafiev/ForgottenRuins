package com.xrbpowered.ruins.world.item;

import java.awt.Color;

public class EmptyFlask extends Item {

	public EmptyFlask() {
		super("Empty Flask", "icons/empty_flask.png", new Color(0x959190),
				"Fill in a water well");
	}

	@Override
	protected String indefArticle() {
		return "an";
	}
	
	public static void fill(ItemList inventory) {
		int count = inventory.count(Item.emptyFlask);
		inventory.add(Item.emptyFlask, -count);
		inventory.add(Item.waterFlask, count);
	}
	
}
