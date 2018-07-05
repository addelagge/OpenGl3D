package HelperClasses;

import org.lwjgl.util.vector.Vector3f;

import Models.RawModel;
import RenderEngine.Loader;

public class CollisionBox {
	private float width;
	private float depth;
	private RawModel rawmodel;
	
	public CollisionBox(float width, float depth, Loader loader){
		this.width = width;
		this.depth = depth;
		float[] verts = {

				width/2, 0, -depth/2, //up left
				-width/2, 0, -depth/2, //down left
				width/2, 0, depth/2, //up right
				width/2, 0, depth/2, //up right
				-width/2, 0, -depth/2, //down left
				-width/2, 0, depth/2 //  down right
		};
		
		rawmodel = loader.loadToVao(verts);
	}
	
	public float getWidth() {
		return width;
	}

	public float getDepth() {
		return depth;
	}


	public RawModel getRawmodel() {
		return rawmodel;
	}
	
	
	

}
