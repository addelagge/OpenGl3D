package Shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class ShaderProgram {
	private int vertexShader, fragmentShader, programID;
	private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public ShaderProgram(String vertexFile, String fragmentFile){
		vertexShader = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShader = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShader);
		GL20.glAttachShader(programID, fragmentShader);
		bindAttributes();
		GL20.glLinkProgram(programID);
		getUniformLocations();
	}
	
	protected abstract void bindAttributes();
	protected abstract void getUniformLocations();
	
	protected void bindAttribute(int attribIndex, String attribName){
		GL20.glBindAttribLocation(programID, attribIndex, attribName);
	}
	
	protected int getUniformLocation(String name){
		return GL20.glGetUniformLocation(programID, name);
	}
	
	
	public void start(){
		GL20.glUseProgram(programID);
	}
	
	public void stop(){
		GL20.glUseProgram(0);
	}
	protected void loadVector(Vector3f vector, int location){
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	protected void load2DVector(Vector2f vector, int location){
		GL20.glUniform2f(location, vector.x, vector.y);
	}
	
	protected void loadFloat(float value, int location){
		GL20.glUniform1f(location, value);
	}
	
	protected void loadInt(int value, int location){
		GL20.glUniform1i(location, value);
	}
	
	protected void loadBoolean(boolean value, int location){
		float toLoad = 0;
		if(value){
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}
	
	protected void loadMatrix(Matrix4f matrix, int location){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
		
	}	
	
	
	private int loadShader(String file, int type){
		StringBuilder shaderSource = new StringBuilder();
		try{
			InputStream in = Class.class.getResourceAsStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch(IOException e){
			e.printStackTrace();
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.out.println("Error compiling shader");
			System.exit(1);
		}
		
		return shaderID;
	}
	
	public void cleanUp(){
		GL20.glDetachShader(programID, fragmentShader);
		GL20.glDetachShader(programID, vertexShader);
		GL20.glDeleteShader(fragmentShader);
		GL20.glDeleteShader(vertexShader);
		GL20.glDeleteProgram(programID);
	}

}
