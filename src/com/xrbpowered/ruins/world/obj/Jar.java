package com.xrbpowered.ruins.world.obj;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import com.xrbpowered.ruins.RandomUtils;
import com.xrbpowered.ruins.Ruins;
import com.xrbpowered.ruins.render.prefab.Prefab;
import com.xrbpowered.ruins.render.prefab.PrefabRenderer;
import com.xrbpowered.ruins.world.World;
import com.xrbpowered.ruins.world.item.Item;

public class Jar extends SmallObject {

	public Item item = null;
	public int coins = 0;
	public boolean broken = false;
	
	private static float[] wvariants = {2f, 1f};
	private int variant;

	private static float[] witems = {26f, 0.1f, 1.7f, 0.2f, 2f};
	private static Item[] items = {null, Item.amuletOfEscape, Item.key, Item.emptyFlask, Item.healingHerbs};
	
	public Jar(World world, Random random) {
		super(world);
		variant = RandomUtils.weighted(random, wvariants);
		item = items[RandomUtils.weighted(random, witems)];
		if(item==null) {
			coins = random.nextInt(8)-4;
			if(coins<0) {
				coins = 0;
				if(random.nextInt(4)<2)
					broken = true;
			}
		}
	}
	
	@Override
	public void loadState(DataInputStream in) throws IOException {
		broken = in.readBoolean();
		if(broken) {
			item = null;
			coins = 0;
		}
	}
	
	@Override
	public void saveState(DataOutputStream out) throws IOException {
		out.writeBoolean(broken);
	}
	
	@Override
	public float getScaleRange() {
		return 0.35f;
	}
	
	@Override
	public float getRadius(float scale) {
		return 0.25f*scale;
	}

	@Override
	public Prefab getPrefab() {
		return broken ? PrefabRenderer.broken : PrefabRenderer.jars[variant];
	}
	
	@Override
	public String getPickName() {
		return "Jar";
	}
	
	@Override
	public String getActionString() {
		return coins>0 || item!=null ? "[Right-click to search]" : "[Empty]";
	}
	
	@Override
	public void interact() {
		if(!broken) {
			if(item!=null) {
				Ruins.hud.popup.popup("Found "+item.countString(1));
				world.player.inventory.add(item, 1);
				Ruins.hud.updateInventoryPreview();
				item = null;
			}
			else if(coins>1)
				Ruins.hud.popup.popup(String.format("Found %d coins", coins));
			else if(coins==1)
				Ruins.hud.popup.popup("Found a coin");
			else
				Ruins.hud.popup.popup("Nothing here");
			world.player.coins += coins;
			coins = 0;
			broken = true;
			Ruins.prefabs.updateAllInstances(world);
		}
	}
	
}
