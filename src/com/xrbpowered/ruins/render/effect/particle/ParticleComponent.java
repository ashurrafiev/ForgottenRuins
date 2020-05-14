package com.xrbpowered.ruins.render.effect.particle;

import java.util.ArrayList;
import java.util.Iterator;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.ruins.render.RenderComponent;

public class ParticleComponent extends RenderComponent<Particle> {

	private int maxCount = 0;

	private float[] pointData = null;
	private StaticMesh points = null;
	
	public final Texture texture;
	public final int frames;
	public final float particleSize;

	protected ArrayList<Particle> particles = new ArrayList<>();
	
	public ParticleComponent(int maxCount, Texture texture, int frames, float particleSize) {
		this.maxCount = maxCount;
		this.texture = texture;
		this.frames = frames;
		this.particleSize = particleSize;
		allocateInstanceData();
		instCount = 0;
	}

	public ParticleComponent(int maxCount, Texture texture) {
		this(maxCount, texture, texture.getWidth()/texture.getHeight(), texture.getHeight()*0.015f);
	}

	public int getMaxCount() {
		return maxCount;
	}
	
	protected void allocateInstanceData() {
		if(points!=null)
			releaseInstances();
		if(maxCount>0) {
			pointData = ParticleShader.vertexInfo.createData(maxCount);
			points = new StaticMesh(ParticleShader.vertexInfo, pointData, 1, maxCount, true);
		}
		else
			points = null;
	}
	
	@Override
	public void startCreateInstances() {
	}
	
	@Override
	public int addInstance(Particle p) {
		add(p);
		return particles.size()-1;
	}
	
	@Override
	public void finishCreateInstances() {
		updateData();
	}
	
	@Override
	public void drawInstances(Shader shader) {
		if(instCount==0)
			return;
		((ParticleShader) shader).updateComponent(this);
		texture.bind(0);
		points.draw();
	}
	
	@Override
	public void releaseInstances() {
		if(points!=null)
			points.release();
		points = null;
		instCount = 0;
		maxCount = 0;
	}

	@Override
	public void release() {
		texture.release();
		super.release();
	}
	protected void setPointData(float[] pointData, Particle p, int index) {
		int offs = index * ParticleShader.vertexInfo.getSkip();
		pointData[offs+0] = p.position.x;
		pointData[offs+1] = p.position.y;
		pointData[offs+2] = p.position.z;
		pointData[offs+3] = p.scale;
		pointData[offs+4] = p.phase;
	}
	
	protected void updateData() {
		instCount = particles.size();
		if(instCount>0) {
			if(instCount>maxCount)
				instCount = maxCount;
			for(int i=instCount-1; i>=0; i--)
				setPointData(pointData, particles.get(i), i);
			points.updateCountElements(instCount);
			points.updateVertexData(pointData);
		}
	}

	public void clear() {
		particles.clear();
	}
	
	public void add(Particle p) {
		particles.add(p);
	}

	public void update(float dt) {
		for(Iterator<Particle> i = particles.iterator(); i.hasNext();) {
			Particle p = i.next();
			if(!p.updateTime(dt))
				i.remove();
		}
		updateData();
	}
	
}
