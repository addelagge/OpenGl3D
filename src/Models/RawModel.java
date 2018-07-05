package Models;

import org.lwjgl.util.vector.Vector3f;

public class RawModel {
	private int vaoID;
	private int numberOfVertices;
	private Vector3f dimensions = new Vector3f();
	
	public RawModel(int vaoID, int numberOfVertices) {
		this.vaoID = vaoID;
		this.numberOfVertices = numberOfVertices;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getNumberOfVertices() {
		return numberOfVertices;
	}

	public Vector3f getDimensions() {
		return dimensions;
	}

	public void setDimensions(Vector3f dimensions) {
		this.dimensions = dimensions;
	}
	
	

}
