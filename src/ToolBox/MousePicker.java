package ToolBox;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Terrain.Terrain;
import Entities.Camera;
import Entities.Entity;

public class MousePicker {

	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 600;

	private Vector3f currentRay = new Vector3f();

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	
	private Terrain[][] terrains;
	private Vector3f currentTerrainPoint;
	
	private Entity grabbedEntity = null;

	public MousePicker(Camera cam, Matrix4f projection, Terrain[][] terrains) {
		camera = cam;
		projectionMatrix = projection;
		viewMatrix = Maths.createViewMatrix(camera);
		this.terrains = terrains;
	}
	
	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update(List<Entity> entities) {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		
		checkIfRayIntersectsWithTerrain();
		
		checkIfRayIntersectsWithEntity(entities);		
		
	}

	private void checkIfRayIntersectsWithEntity(List<Entity> entities) {
		if(grabbedEntity == null){
			projectMouseRay(currentRay, entities);
		} else{
			manipulateGrabbedEntity();
		}
		
	}

	private void checkIfRayIntersectsWithTerrain() {
		
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			currentTerrainPoint = searchForIntersectionPoint(currentRay);			
		} else {
			currentTerrainPoint = null;
		}
		
	}

	private Vector3f calculateMouseRay() {
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / Display.getWidth() - 1f;
		float y = (2.0f * mouseY) / Display.getHeight() - 1f;
		return new Vector2f(x, y);
	}
	
	//**********************************************************
	
	
	private void projectMouseRay(Vector3f ray, List<Entity> entities) {
		
		Vector3f rayPosition = camera.getPosition();			
		int i = 1;			
		boolean entityFound = false;		

		while(!entityFound){		
			
			Vector3f scaledRay = new Vector3f(ray.x * i, ray.y * i, ray.z * i);
			Vector3f resultRay = Vector3f.add(rayPosition, scaledRay, null);

			if(resultRay.x >= terrains.length*800 || resultRay.z >= terrains[0].length*800)
				break;
				
			if(i > RAY_RANGE)
				break;	
			
			Vector2f terrainIndex = Terrain.getTerrainIndex(resultRay.x, resultRay.z);
			if(resultRay.y <  terrains[(int) terrainIndex.x][(int) terrainIndex.y].getTerrainHeight(resultRay.x, resultRay.z)){				
				break;	
			}						
			
			for(Entity entity : entities){
				entity.setHighlighted(false);
				
				if(entity.isHitBy(resultRay)){
					entity.setHighlighted(true);	
					
					if(Mouse.isButtonDown(0)){
						grabbedEntity = entity;
					}
					
					entityFound = true;
					break;	
				}
			}			
			
			i++;
			
		}					
		
	}



	private Vector3f searchForIntersectionPoint(Vector3f ray){
		
		float start = 0;
		float finish = RAY_RANGE;
		int count = 0;
		float half = start + ((finish - start) / 2f);
		
		while(count < RECURSION_COUNT){			
			
			if(intersectionInRange(start, half, ray)){
				finish = half;
			} else{
				start = half;
			}
			
			half = start + ((finish - start) / 2f);
			count++;
		}
		
		Vector3f endPoint = getPointOnRay(ray, half);
		Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
		
		if (terrain != null) {
			return endPoint;
		} else {
			return null;
		}
	}
	
	
	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}
	
	
	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
			
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnderGround(Vector3f testPoint) {
		Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
		float height = 0;
		
		if (terrain != null) {
			height = terrain.getTerrainHeight(testPoint.getX(), testPoint.getZ());
		}
		
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}

	private Terrain getTerrain(float worldX, float worldZ) {
		Vector2f terrainIndex = Terrain.getTerrainIndex(worldX, worldZ);
		
		if(terrainIndex == null)
			return null;
		
		return terrains[(int) terrainIndex.x][(int) terrainIndex.y];

	}
	
	
	

	private void manipulateGrabbedEntity() {
		if(currentTerrainPoint != null)
			grabbedEntity.setPosition(currentTerrainPoint);
		
		if(!Mouse.isButtonDown(0)){				
			grabbedEntity = null;
		}
		
	}

}//End of MousePicker