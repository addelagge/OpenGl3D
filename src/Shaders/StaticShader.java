package Shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;
import Entities.Light;
import Entities.Player;
import ToolBox.Maths;

public class StaticShader extends ShaderProgram{
	private static final String VERTEX_SHADER = "/Shaders/vertexShader.txt";
	private static final String FRAGMENT_SHADER = "/Shaders/fragmentShader.txt";
	private final int MAX_LIGHTS = 4;
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int[] location_lightColor;
	private int[] location_attenuation;
	private int location_reflectivity;
	private int location_shineDamper;
	private int location_useFakeLighting;
	private int location_cameraFacingDirection;
	private int location_numberOfRows;
	private int location_textureOffset;
	private int location_highlighted;
	private int location_skipSpecular;
	
	public StaticShader(){
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
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_cameraFacingDirection = super.getUniformLocation("cameraFacingDirection");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_textureOffset = super.getUniformLocation("textureOffset");
		location_highlighted = super.getUniformLocation("highlighted");
		location_skipSpecular = super.getUniformLocation("skipSpecular");
		
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
	
	public void loadUseFakeLighting(boolean value){
		super.loadBoolean(value, location_useFakeLighting);
	}
	
	public void loadFacingDirection(Camera camera){
		super.loadVector(camera.getFacingDirection(), location_cameraFacingDirection);
	}
	
	public void loadTextureOffsets(Vector2f offsets){
		super.load2DVector(offsets, location_textureOffset);
	}
	
	public void loadNumberOfRowsInTextureAtlas(float numberOfRows){
		super.loadFloat(numberOfRows, location_numberOfRows);
	}
	
	public void loadIsHighLighted(boolean value){
		super.loadBoolean(value, location_highlighted);
	}
	public void loadUseSpecular(boolean value){
		super.loadBoolean(value, location_skipSpecular);
	}
}
