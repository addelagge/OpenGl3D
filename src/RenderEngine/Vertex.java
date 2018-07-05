package RenderEngine;

import org.lwjgl.util.vector.Vector3f;

public class Vertex {
	private final int NO_INDEX = -1;
	private Vector3f position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	
	
	public Vertex(Vector3f position) {
		this.position = position;
	}
	public int getTextureIndex() {
		return textureIndex;
	}
	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}
	public int getNormalIndex() {
		return normalIndex;
	}
	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}
	public Vector3f getPosition() {
		return position;
	}
	
	public boolean isVertexSet(){
		return (textureIndex != NO_INDEX && normalIndex != NO_INDEX);
	}
	
	

}
