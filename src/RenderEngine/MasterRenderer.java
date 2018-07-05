package RenderEngine;

import java.util.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Entities.LightEntity;
import Entities.Player;
import HelperClasses.CollisionBox;
import Models.TexturedModel;
import Shaders.CollisionShader;
import Shaders.StaticShader;
import Shaders.TerrainShader;
import Terrain.Terrain;

public class MasterRenderer {
	
	private final float FOV = 70;
	private final float NEAR_PLANE = 0.1f;
	private final float FAR_PLANE = 1000;	
	private Matrix4f projectionMatrix;

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Entity> collisionList = new ArrayList<Entity>();
//	private List<Terrain> terrains = new/ ArrayList<Terrain>();
	private Terrain[][] terrains;
	private List<Light> lights = new ArrayList<Light>();
	
	private StaticShader entityshader = new StaticShader();
	private EntityRenderer entityrenderer;
	
	private TerrainShader terrainshader = new TerrainShader();
	private TerrainRenderer terrainrenderer;
	
	private CollisionShader collisionshader = new CollisionShader();
	private CollisionboxRenderer collisionrenderer;
	
	private static final int numOfTerrains = 1;
	
	public MasterRenderer(){
		createProjectionMatrix();
		entityrenderer = new EntityRenderer(entityshader, projectionMatrix);
		terrainrenderer = new TerrainRenderer(terrainshader, projectionMatrix);
		collisionrenderer = new CollisionboxRenderer(collisionshader, projectionMatrix); 
	}
	
	public void prepare(){
		enableCulling();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.2f, 0.5f, 1, 1);
	}
	
	
	public void render(Camera camera){
		prepare();
		entityshader.start();
		entityshader.loadViewMatrix(camera);
		entityshader.loadFacingDirection(camera);
		entityshader.loadLight(lights);
		entityrenderer.render(entities);
		entityshader.stop();		
		
		terrainshader.start();
		terrainshader.loadViewMatrix(camera);
		terrainshader.loadLight(lights);
		terrainrenderer.render(terrains);
		terrainshader.stop();
		
//		collisionshader.start();
//		collisionshader.loadViewMatrix(camera);
//		collisionrenderer.render(collisionList);
//		collisionshader.stop();		
		
		entities.clear();
		collisionList.clear();
		terrains = new Terrain[2][2];
		lights.clear();
	}
		

	public void processEntity(Entity entity){
		
		boolean isLight= entity.getClass() == LightEntity.class;
		
		if(isLight){
			LightEntity lightEntity = (LightEntity)entity;
			processLight(lightEntity.getLight());
		}
		
		TexturedModel texmodel = entity.getTexmodel();
		List<Entity> batch = entities.get(texmodel);
		collisionList.add(entity);
		
		if(batch != null){
			batch.add(entity);

		} else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(texmodel, newBatch);
		}
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void processTerrain(Terrain[][] terrains){
		this.terrains = terrains;
	}
	
	public void processLight(Light light){
		lights.add(light);
	}
	
	public void cleanUp(){
		entityshader.cleanUp();
		terrainshader.cleanUp();
		collisionshader.cleanUp();
	}
	
	
	
	public static int getNumofterrains() {
		return numOfTerrains;
	}

	private void createProjectionMatrix() {
		
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians((FOV / 2f)))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		
	}
	
	public List<Entity> getCollisionList(){
		return this.collisionList;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	
}
