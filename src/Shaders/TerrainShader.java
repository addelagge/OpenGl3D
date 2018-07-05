package Shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;
import Entities.Light;
import Entities.Player;
import ToolBox.Maths;

public class TerrainShader extends ShaderProgram{
	private static final String VERTEX_SHADER = "/Shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_SHADER = "/Shaders/terrainFragmentShader.txt";
	private final int MAX_LIGHTS = 4;
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int[] location_lightColor;
	private int[] location_attenuation;
	private int location_reflectivity;
	private int location_shineDamper;
	private int location_blendmap;
	private int location_grass;
	private int location_grassflowers;
	private int location_mud;
	private int location_path;
	private int location_facingDirection;
	
	public TerrainShader(){
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		
	}

	@Override
	protected void getUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_blendmap = super.getUniformLocation("blendmap");
		location_grass = super.getUniformLocation("grass");
		location_grassflowers = super.getUniformLocation("grassflowers");
		location_mud = super.getUniformLocation("mud");
		location_path = super.getUniformLocation("path");
		location_facingDirection = super.getUniformLocation("facingDirection");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for(int i = 0; i < MAX_LIGHTS; i++){
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i +"]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i +"]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i +"]");
		}

	}
	
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(matrix, location_transformationMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(matrix, location_projectionMatrix);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix(matrix, location_viewMatrix);
	}
	
	public void loadLight(List<Light> lights){
		for(int i = 0; i < MAX_LIGHTS; i++){
			if(i < lights.size()){
				super.loadVector(lights.get(i).getPosition(), location_lightPosition[i]);
				super.loadVector(lights.get(i).getColor(), location_lightColor[i]);
				super.loadVector(lights.get(i).getAttenuation(), location_attenuation[i]);
			} else{
				Vector3f zeroVec = new Vector3f(0,0,0);
				super.loadVector(zeroVec, location_lightPosition[i]);
				super.loadVector(zeroVec, location_lightColor[i]);
				super.loadVector(new Vector3f(1,0,0), location_attenuation[i]);
			}
		}
	}
	
	public void loadShine(float shineDamper, float reflectivity){
		super.loadFloat(reflectivity, location_reflectivity);
		super.loadFloat(shineDamper, location_shineDamper);
	}
	
	public void loadTextureIndices(){
		super.loadInt(0, location_blendmap);
		super.loadInt(1, location_grass);
		super.loadInt(2, location_grassflowers);
		super.loadInt(3, location_mud);
		super.loadInt(4, location_path);
	}

	public void loadFacingDirection(Player player){
		super.loadVector(player.getFacingDirection(), location_facingDirection);
	}

}
