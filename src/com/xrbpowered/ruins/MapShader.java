package com.xrbpowered.ruins;

import java.awt.Color;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shader.CameraShader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class MapShader extends CameraShader {
	
	public static final VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 3)
			.addAttrib("in_Normal", 3)
			.addAttrib("in_TexCoord", 2)
			.addAttrib("in_Light", 1);
	
	public static final String[] SAMLER_NAMES = {"texDiffuse"};
	
	private MapShader() {
		super(vertexInfo, "map_v.glsl", "map_f.glsl");
	}
	
	protected MapShader(String pathVS, String pathFS) {
		super(vertexInfo, pathVS, pathFS);
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		initSamplers(SAMLER_NAMES);
	}
	
	public void setFog(float near, float far, Vector4f color) {
		GL20.glUseProgram(pId);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogNear"), near);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogFar"), far);
		uniform(GL20.glGetUniformLocation(pId, "fogColor"), color);
		GL20.glUseProgram(0);
	}

	public void setFog(float near, float far, Color color) {
		GL20.glUseProgram(pId);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogNear"), near);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogFar"), far);
		GL20.glUniform4f(GL20.glGetUniformLocation(pId, "fogColor"),
				color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 1f);
		GL20.glUseProgram(0);
	}

	public void setLightScale(float light) {
		//GL20.glUseProgram(pId);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "lightScale"), light);
		//GL20.glUseProgram(0);
	}

	private static MapShader instance = null;
	
	public static MapShader getInstance() {
		if(instance==null) {
			instance = new MapShader();
		}
		return instance;
	}
	
	public static void destroyInstance() {
		if(instance!=null) {
			instance.release();
			instance = null;
		}
	}
	
}