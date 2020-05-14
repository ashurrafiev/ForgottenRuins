package com.xrbpowered.ruins.render.prefab;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.InstanceBuffer;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.ruins.render.RenderComponent;

public abstract class InstanceComponent extends RenderComponent<InstanceInfo> {

	public final StaticMesh mesh; 
	public final Texture texture;
	public Texture glowTexture = null;
	
	protected InstanceBuffer instBuffer = null;
	
	public boolean culling = true;

	public InstanceComponent(StaticMesh mesh, Texture texture) {
		this.mesh = mesh;
		this.texture = texture;
	}
	
	public InstanceComponent setGlow(Texture glow) {
		this.glowTexture = glow;
		return this;
	}
	
	public boolean hasGlow() {
		return glowTexture!=null;
	}
	
	public InstanceComponent setCulling(boolean culling) {
		this.culling = culling;
		return this;
	}
	
	@Override
	public void drawInstances(Shader shader) {
		if(instCount==0)
			return;
		if(culling)
			GL11.glEnable(GL11.GL_CULL_FACE);
		else
			GL11.glDisable(GL11.GL_CULL_FACE);
		texture.bind(0);
		(hasGlow() ? glowTexture : getBlack()).bind(1);
		meshDrawCallInstanced();
	}
	
	public void meshDrawCallInstanced() {
		if(instCount==0)
			return;
		mesh.enableDraw(null);
		instBuffer.enable();
		mesh.drawCallInstanced(instCount);
		instBuffer.disable();
		mesh.disableDraw();
	}
	
	protected boolean createInstanceBuffer(int count) {
		if(instBuffer!=null)
			releaseInstances();
		if(count>0)
			instBuffer = new InstanceBuffer(count, InstanceShader.instInfo);
		else
			instBuffer = null;
		return instBuffer!=null;
	}
	
	protected float[] createInstanceData(int count) {
		return InstanceShader.instInfo.createData(count);
	}
	
	protected void setInstanceData(float[] instanceData, InstanceInfo info, int index) {
		int offs = index * InstanceShader.instInfo.getSkip();
		instanceData[offs+0] = info.x;
		instanceData[offs+1] = info.y;
		instanceData[offs+2] = info.z;
		instanceData[offs+3] = info.rotate;
		instanceData[offs+4] = info.scale;
		instanceData[offs+5] = info.light;
	}
	
	@Override
	public void releaseInstances() {
		if(instBuffer!=null)
			instBuffer.release();
		instBuffer = null;
		instCount = 0;
	}
	
	@Override
	public void release() {
		mesh.release();
		texture.release();
		if(black!=null) {
			black.release();
			black = null;
		}
		super.release();
	}

	private static Texture black = null;
	
	public static Texture getBlack() {
		if(black==null)
			black = new Texture(Color.BLACK);
		return black;
	}
}
