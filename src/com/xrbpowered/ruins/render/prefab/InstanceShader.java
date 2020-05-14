package com.xrbpowered.ruins.render.prefab;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.ruins.render.shader.ShaderEnvironment;
import com.xrbpowered.ruins.render.shader.WallShader;

public class InstanceShader extends WallShader {
	
	public static final VertexInfo instInfo = new VertexInfo(WallShader.vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_RotationY", 1)
			.addAttrib("ins_Scale", 1)
			.addAttrib("ins_Light", 1); 
	
	private int highlightInstanceLocation;
	
	public InstanceShader(ShaderEnvironment env, CameraActor camera) {
		super("shaders/tileobj_v.glsl", "shaders/tileobj_f.glsl");
		setEnvironment(env);
		setCamera(camera);
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		highlightInstanceLocation = GL20.glGetUniformLocation(pId, "highlightInstance");
	}
	
	@Override
	protected String[] listSamplerNames() {
		return new String[] {"texDiffuse", "texGlow"};
	}
	
	@Override
	protected int bindAttribLocations() {
		return instInfo.bindAttribLocations(this.pId, super.bindAttribLocations());
	}
	
	public void updateHighlight(int index) {
		GL20.glUniform1i(highlightInstanceLocation, index);
	}
	
	private static InstanceShader instance = null;
	
	public static void createInstance(ShaderEnvironment env, CameraActor camera) {
		instance = new InstanceShader(env, camera);
	}
	
	public static InstanceShader getInstance() {
		return instance;
	}
	
	public static void releaseInstance() {
		instance.release();
	}

}