package Shaders;

import org.lwjgl.util.vector.Matrix4f;

import Entities.Camera;
import ToolBox.Maths;

public class CollisionShader extends ShaderProgram{


	private static final String VERTEX_SHADER = "/Shaders/collisionVertexShader.txt";
	private static final String FRAGMENT_SHADER = "/Shaders/collisionFragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	public CollisionShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}
	@Override
	protected void getUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");	
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
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
}
