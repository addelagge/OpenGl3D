package RenderEngine;

import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import Models.RawModel;

public class Loader {
	
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	public RawModel loadToVao(float[] vertices, float[] textureCoords, float[] normals, int[] indices){
		int vaoID = createVao();
		bindIndices(indices);
		storeInAttribList(0, 3, vertices);
		storeInAttribList(1, 2, textureCoords);		
		storeInAttribList(2, 3, normals);		
		unbindVao();
		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadToVao(float[] vertices){
		int vaoID = createVao();
		storeInAttribList(0, 3, vertices);
		unbindVao();
		return new RawModel(vaoID, vertices.length/3);
	}
	
	public int loadTexture(String fileName){
		Texture texture = null;
		
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream(new File("res\\" + fileName +".png")));
			textures.add(texture.getTextureID());
			//MipMapping
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return texture.getTextureID();
	}
	

	private void storeInAttribList(int index, int dimensions, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, dimensions, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
	}

	private FloatBuffer storeInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private void bindIndices(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
	}

	private IntBuffer storeInIntBuffer(int[] indices) {
		IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
		buffer.put(indices);
		buffer.flip();
		return buffer;
	}

	private int createVao() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private void unbindVao(){
		GL30.glBindVertexArray(0);
	}
	
	public void cleanUp(){
		for(int vbo : vbos){
			GL15.glDeleteBuffers(vbo);
		}
		for(int vao : vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for(int texture : textures){
			GL11.glDeleteTextures(texture);
		}
	}

}
