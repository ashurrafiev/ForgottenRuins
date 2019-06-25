package com.xrbpowered.ruins;

import java.awt.Color;
import java.awt.event.KeyEvent;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.ui.common.UIFpsOverlay;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.ruins.entity.PlayerActor;
import com.xrbpowered.ruins.render.TileObjectPicker;
import com.xrbpowered.ruins.render.WallBuilder;
import com.xrbpowered.ruins.render.WallChunk;
import com.xrbpowered.ruins.render.effects.FlashPane;
import com.xrbpowered.ruins.render.prefab.Prefabs;
import com.xrbpowered.ruins.render.shader.ShaderEnvironment;
import com.xrbpowered.ruins.render.shader.WallShader;
import com.xrbpowered.ruins.render.texture.TextureAtlas;
import com.xrbpowered.ruins.ui.Hud;
import com.xrbpowered.ruins.world.World;

public class Ruins extends UIClient {

	// settings
	public static float renderScale = 1f; 
	
	private WallChunk[] walls;
	private WallShader shader;
	private TextureAtlas atlas;
	
	private Controller observerController;
	private Controller activeController;
	
	private StaticMesh groundMesh;
	private Texture groundTexture;

	private TileObjectPicker pick;
	
	private World world;
	private PlayerActor player = new PlayerActor(input);

	public static ShaderEnvironment environment = new ShaderEnvironment();
	public static FlashPane flash;
	
	public Ruins() {
		super("Ruins Generator");
		
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
		
		new UIOffscreen(getContainer(), renderScale) {
			@Override
			public void setSize(float width, float height) {
				super.setSize(width, height);
				player.camera.setAspectRatio(getWidth(), getHeight());
			}
			
			@Override
			public void setupResources() {
				clearColor = new Color(0xe5efee);
				environment.setFog(10, 80, clearColor);
				environment.lightScale = 0.1f;
				
				player.camera = new CameraActor.Perspective().setRange(0.1f, 80f).setAspectRatio(getWidth(), getHeight());
				pick = new TileObjectPicker(player);
				
				shader = (WallShader) new WallShader().setEnvironment(environment).setCamera(player.camera);
				atlas = new TextureAtlas();
				
				observerController = new Controller(input).setActor(player.camera);
				observerController.moveSpeed = 10f;
				activeController = player.controller;
				activeController.setMouseLook(true);

				groundTexture = new Texture("ground.png", true, false);
				groundMesh = WallBuilder.createGround(80f);
				
				Prefabs.createResources(environment, player.camera);
				createWorldResources();
				
				super.setupResources();
			}
			
			@Override
			public void updateTime(float dt) {
				activeController.update(dt);
				player.updateTime(dt);
				super.updateTime(dt);
			}
			
			@Override
			protected void renderBuffer(RenderTarget target) {
				WallChunk.zsort(walls, player.camera);
				
				GL11.glEnable(GL11.GL_CULL_FACE);
				pick.update(target);

				super.renderBuffer(target);
				shader.use();
				
				atlas.getTexture().bind(0);
				for(WallChunk wall : walls)
					wall.drawVisible();

				groundTexture.bind(0);
				groundMesh.draw();
				shader.unuse();
				
				Prefabs.drawInstances();
			}
		};
		
		flash = new FlashPane(getContainer());
		new Hud(getContainer(), player);
		new UIFpsOverlay(this);
	}
	
	private void createWorldResources() {
		world = World.createWorld(System.currentTimeMillis());
		walls = WallBuilder.createChunks(world, atlas);

		Prefabs.createInstances(world);

		player.reset(world);
		pick.setWorld(world, walls);
	}
	
	private void releaseWorldResources() {
		Prefabs.releaseInstances();
		for(WallChunk wall : walls)
			wall.release();
	}
	
	@Override
	public void keyPressed(char c, int code) {
		if(code==KeyEvent.VK_ESCAPE)
			requestExit();
		else if(code==KeyEvent.VK_F1) {
			activeController.setMouseLook(false);
			activeController = (activeController==player.controller) ? observerController : player.controller;
			activeController.setMouseLook(true);
		}
		else if(code==KeyEvent.VK_ALT)
			activeController.setMouseLook(false);
		else if(code==KeyEvent.VK_BACK_SPACE) {
			releaseWorldResources();
			createWorldResources();
		}
		else
			super.keyPressed(c, code);
	}
	
	@Override
	public void mouseDown(float x, float y, int button) {
		activeController.setMouseLook(true);
		
		if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT && pick.pickObject!=null)
			pick.pickObject.interact();
		
		super.mouseDown(x, y, button);
	}
	
	public static void main(String[] args) {
		new Ruins().run();
	}

}
