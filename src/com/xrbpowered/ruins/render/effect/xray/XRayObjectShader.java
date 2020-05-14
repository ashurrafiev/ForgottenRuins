package com.xrbpowered.ruins.render.effect.xray;

import java.awt.Color;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.ruins.render.prefab.InstanceShader;
import com.xrbpowered.ruins.render.shader.WallShader;

public class XRayObjectShader extends WallShader {

	public XRayObjectShader(CameraActor camera) {
		super("shaders/tileobj_v.glsl", "shaders/blank_f.glsl");
		setEnvironment(InstanceShader.getInstance().environment);
		setCamera(camera);
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		GL20.glUseProgram(pId);
		uniform(GL20.glGetUniformLocation(pId, "color"), Color.WHITE);
		GL20.glUseProgram(0);
	}
	
	@Override
	protected void initSamplers(String[] names) {
	}

	@Override
	protected int bindAttribLocations() {
		return InstanceShader.instInfo.bindAttribLocations(this.pId, super.bindAttribLocations());
	}

}
