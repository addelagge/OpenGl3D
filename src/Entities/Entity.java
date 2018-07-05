package Entities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import HelperClasses.CollisionBox;
import Models.TexturedModel;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import Terrain.Terrain;
import Textures.ModelTexture;

public class Entity {
	
	private TexturedModel texmodel;
	private Vector3f position;
	private float rotX, rotY, rotZ, scale;
	private int indexInTextureAtlas = 0;
	private float textureXOffset = 0;
	private float textureYOffset = 0;
	
	private float halfWidth;
	private float halfDepth;
	private float height;
	private int updateCounter = 0;
	
	private final float GRAVITY = -50;
	
	private CollisionBox collisionBox;
	
	private boolean highlighted = false;
	
	private Vector3f toEntityVector = new Vector3f();
	
	public Entity(TexturedModel texmodel, Vector3f position, float rotX, float rotY, float rotZ, float scale, Loader loader) {
		this.texmodel = texmodel;
		Vector3f dimensions = texmodel.getRawmodel().getDimensions();
		
		dimensions.x *= scale;
		dimensions.y *= scale;
		dimensions.z *= scale;
		
		this.collisionBox = new CollisionBox(dimensions.x, dimensions.z, loader);		
		
		this.halfWidth = dimensions.x / 2;
		this.halfDepth = dimensions.z / 2;
		this.height = dimensions.y;
		
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}
	
	public Entity(TexturedModel texmodel, int indexInTextureAtlas, Vector3f position, float rotX, float rotY, float rotZ, float scale, Loader loader) {
		this.texmodel = texmodel;
		Vector3f dimensions = texmodel.getRawmodel().getDimensions();
		
		dimensions.x *= scale;
		dimensions.y *= scale;
		dimensions.z *= scale;
		
		this.collisionBox = new CollisionBox(dimensions.x, dimensions.z, loader);		
		
		halfWidth = dimensions.x / 2;
		halfDepth = dimensions.z / 2;
		height = dimensions.y;
		
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.indexInTextureAtlas = indexInTextureAtlas;
	}
	
	public void update(Terrain[][] terrains){		
		move(terrains);
		checkBoundaries(terrains);				
	}
	
	
	
	protected void move(Terrain[][] terrains) {
		float speed = 0;
		float distanceToMove = DisplayManager.getFrameTime() * speed;
		
		float xMove = (float) (distanceToMove * Math.sin(Math.toRadians(rotY)));
		float zMove = (float) (distanceToMove * Math.cos(Math.toRadians(rotY)));
		float yMove = GRAVITY * DisplayManager.getFrameTime();
		this.increasePosition(xMove, yMove, zMove);
		
		Vector2f terrainIndex = Terrain.getTerrainIndex(position.x, position.z);
		float terrainHeight = terrains[(int) terrainIndex.x][(int) terrainIndex.y].getTerrainHeight(position.x, position.z);
		
		if(position.y < terrainHeight){			
			position.y = terrainHeight;
		}
		
		if(updateCounter > 30){
			float randomNr = (float) (Math.random() * 10);
			if((int) (Math.random() * 2) == 1)
				randomNr *= -1;

			this.increaseRotation(0, randomNr, 0);
			updateCounter = 0;
		}
		
		updateCounter++;
		
		
		
	}

	protected void checkBoundaries(Terrain[][] terrains) {

		//Outside of the terrain		
		float largestXOnTerrain = terrains.length * Terrain.getSize();
		float largestZOnTerrain = terrains[0].length * Terrain.getSize();
		
		if(this.getPosition().x < 0){
			this.getPosition().x = 0;
			this.rotY += 5;
		} else if(this.getPosition().x > largestXOnTerrain){
			this.getPosition().x = largestXOnTerrain;
			this.rotY += 5;
		}
		
		if(this.getPosition().z < 0){
			this.getPosition().z = 0;
			this.rotY += 45;
		} else if(this.getPosition().z > largestZOnTerrain){
			this.getPosition().z = largestZOnTerrain;
			this.rotY += 45;
		}
		
		
	}

	public void checkCollision(Entity entity) {		
				
		Vector3f.sub(this.getPosition(), entity.getPosition(), toEntityVector);	

		float minXDistance = entity.getHalfWidth() + this.getHalfWidth();
		float minZDistance = entity.getHalfDepth() + this.getHalfDepth();

		float collisionDepthX = Math.abs(toEntityVector.x) - minXDistance;
		float collisionDepthZ = Math.abs(toEntityVector.z)  - minZDistance;
		
		if(collisionDepthX < 0 && collisionDepthZ < 0){		
			float xCorrection = 0;
			float zCorrection = 0;				

			if(collisionDepthZ > collisionDepthX){						
				if(toEntityVector.z < 0 ){
					zCorrection = collisionDepthZ ;
				} else if(toEntityVector.z > 0 ){
					zCorrection = -collisionDepthZ;
				}					
			} 
			
			else{								
				if(toEntityVector.x < 0 ){
					xCorrection = collisionDepthX ;
				} else if(toEntityVector.x > 0 ){
					xCorrection = -collisionDepthX;
				}
			}
			
			this.increasePosition(xCorrection, 0, zCorrection);					
		}		
		
				
		
	}//End of checkCollision
	
		
	public float getTextureXOffset() {
		ModelTexture texture = texmodel.getTexture();
		int textureColumn = indexInTextureAtlas % texture.getNumberOfRows();		
		textureXOffset = textureColumn / (float)texture.getNumberOfRows();
		return textureXOffset;
	}

	public float getTextureYOffset() {
		ModelTexture texture = texmodel.getTexture();
		int textureRow = indexInTextureAtlas / texture.getNumberOfRows();
		textureYOffset = textureRow / (float)texture.getNumberOfRows();
		return textureYOffset;
	}

	public void increasePosition(float x, float y, float z){
		this.position.x += x;
		this.position.y += y;
		this.position.z += z;
	}
	
	public void increaseRotation(float x, float y, float z){
		this.rotX += x;
		this.rotY += y;
		this.rotZ += z;
		
		if(this.rotX > 80){
			this.rotX = 80;
		} else if(this.rotX < -80){
			this.rotX = -80;
		}
	}
	
	
	
	public float getGRAVITY() {
		return GRAVITY;
	}

	public int getIndexInTextureAtlas() {
		return indexInTextureAtlas;
	}

	public TexturedModel getTexmodel() {
		return texmodel;
	}
	public Vector3f getPosition() {
		return position;
	}
	public float getRotX() {
		return rotX;
	}
	public float getRotY() {
		return rotY;
	}
	public float getRotZ() {
		return rotZ;
	}
	public float getScale() {
		return scale;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public float getHalfWidth() {
		return halfWidth;
	}

	public float getHalfDepth() {
		return halfDepth;
	}

	public float getHeight() {
		return height;
	}

	public CollisionBox getCollisionBox() {
		return collisionBox;
	}
	
	
	
	public boolean isHitBy(Vector3f ray){
		
		if(!(ray.getX() < position.x + halfWidth && ray.getX() > position.x - halfWidth))
			return false;
		
		if(!(ray.getZ() < position.z + halfDepth && ray.getZ() > position.z - halfDepth))
			return false;
		
		if(!(ray.getY() >= position.y && ray.getY() <= position.y + height))
			return false;
				
		return true;
	}
	
	public void setPosition(Vector3f position){
		this.position = position;
	}
}
