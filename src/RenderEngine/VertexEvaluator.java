package RenderEngine;

import org.lwjgl.util.vector.Vector3f;

public class VertexEvaluator {
	
	private float NOT_SET = 0;
	private float biggestX = NOT_SET;
	private float smallestX = NOT_SET;
	
	private float biggestY = NOT_SET;
	private float smallestY = NOT_SET;
	
	private float biggestZ = NOT_SET;
	private float smallestZ = NOT_SET;
	
	private Vector3f dimensions;
	
	public void evaluateVertex(Vertex vertex){
		Vector3f position = vertex.getPosition();
		
		if(position.x < smallestX){
			smallestX = position.x;
		} else if(position.x > biggestX){
			biggestX = position.x;
		}
		
		if(position.y < smallestY){
			smallestY = position.y;
		} else if(position.y > biggestY){
			biggestY = position.y;
		}
		
		if(position.z < smallestZ){
			smallestZ = position.z;
		} else if(position.z > biggestZ){
			biggestZ = position.z;
		}		
		
	}

	public float getNotSet() {
		return NOT_SET;
	}

	public float getBiggestX() {
		return biggestX;
	}

	public float getSmallestX() {
		return smallestX;
	}

	public float getBiggestY() {
		return biggestY;
	}

	public float getSmallestY() {
		return smallestY;
	}

	public float getBiggestZ() {
		return biggestZ;
	}

	public float getSmallestZ() {
		return smallestZ;
	}

	public Vector3f getDimensions() {
		float width = Math.abs(biggestX - smallestX);
		float depth = Math.abs(biggestZ - smallestZ);
		float height = Math.abs(biggestY - smallestY);
		dimensions = new Vector3f(width, height, depth);
		return dimensions;
	}

	private void resetValues() {
		biggestX = NOT_SET;
		smallestX = NOT_SET;
		
		biggestY = NOT_SET;
		smallestY = NOT_SET;
		
		biggestZ = NOT_SET;
		smallestZ = NOT_SET;
		
	}
	
	
	
	
	
	
	
	
	
	

}
