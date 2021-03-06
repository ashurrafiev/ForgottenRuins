package com.xrbpowered.ruins.world.obj;

import com.xrbpowered.ruins.RandomUtils;
import com.xrbpowered.ruins.Ruins;
import com.xrbpowered.ruins.entity.WorldEntity;
import com.xrbpowered.ruins.render.effect.particle.Particle;
import com.xrbpowered.ruins.render.effect.particle.ParticleEffect;
import com.xrbpowered.ruins.render.effect.particle.ParticleGenerator;
import com.xrbpowered.ruins.render.effect.particle.ParticleRenderer;
import com.xrbpowered.ruins.render.prefab.Prefab;
import com.xrbpowered.ruins.render.prefab.PrefabRenderer;
import com.xrbpowered.ruins.world.ObeliskSystem;
import com.xrbpowered.ruins.world.World;
import com.xrbpowered.ruins.world.gen.WorldGenerator.Token;

public class Portal extends TileObject implements WorldEntity {

	public final ObeliskSystem system;
	public boolean active = false;
	private CollisionObject cobj;

	public Portal(ObeliskSystem system, Token objToken) {
		super(system.world, objToken);
		this.system = system;
		system.setPortal(this);
		effect.pivot.set(position);
	}

	private static CollisionObject collision = new CollisionObject(0.3f, 0.7f, -1f, 1f);

	@Override
	public void place() {
		super.place();
		cobj = collision.place(this);
		cobj.active = false;
		world.map[x][z][y].collisionObject = cobj;
		if(y+1<World.height)
			world.map[x][z][y+1].collisionObject = cobj;
		if(y+2<World.height)
			world.map[x][z][y+2].collisionObject = cobj;
	}

	@Override
	public Prefab getPrefab() {
		cobj.active = active;
		return active ? PrefabRenderer.portal : PrefabRenderer.portalOff;
	}

	@Override
	public String getPickName() {
		return "Exit Portal";
	}
	
	@Override
	public String getActionString() {
		return "[Right-click to interact]";
	}

	@Override
	public void interact() {
		if(active)
			Ruins.overlayPortal.show(world);
		else
			Ruins.hud.popup.popup("Activate all obelisks to open");
	}
	
	@Override
	public boolean updateTime(float dt) {
		if(active)
			generator.update(dt);
		return true;
	}
	
	public static ParticleEffect effect = new ParticleEffect(0.9f, 0.9f, 0f, 3f) {
		@Override
		public void generateParticle() {
			Particle p = new Particle(RandomUtils.random(0.75f, 1.5f));
			assign(p);
			ParticleRenderer.light.add(p);
		}
		@Override
		public void generate() {
			generateParticle();
		}
	};
	
	private static ParticleGenerator generator = new ParticleGenerator(effect, 0.01f, 0.02f); 
}
