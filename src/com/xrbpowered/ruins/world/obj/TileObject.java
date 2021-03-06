package com.xrbpowered.ruins.world.obj;

import org.joml.Vector3f;

import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.ruins.render.prefab.InstanceInfo;
import com.xrbpowered.ruins.world.Direction;
import com.xrbpowered.ruins.world.World;
import com.xrbpowered.ruins.world.gen.WorldGenerator;

public abstract class TileObject extends MapObject {

	public int x, z, y;
	public Direction d;
	public long seed;
	
	public TileObject(World world, WorldGenerator.Token objToken) {
		super(world);
		this.x = objToken.x;
		this.z = objToken.z;
		this.y = objToken.y;
		this.d = objToken.d;
		this.seed = World.seedXZY(world.seed+58932, x, z, y);
		
		position = new Vector3f(x*2f, y, z*2f);
	}
	
	@Override
	public void place() {
		super.place();
		world.map[x][z][y].tileObject = this;
		instInfo = new InstanceInfo(world, this).setRotate(d);
	}
	
	@Override
	public void copyToActor(Actor actor) {
		actor.rotation.y = -d.rotation();
		super.copyToActor(actor);
	}
	
}
