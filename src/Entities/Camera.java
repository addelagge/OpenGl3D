package Entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Terrain.Terrain;

public class Camera {
	
	private final float SPEED = 0.2f;
	
	private Vector3f position = new Vector3f(0,4,0);
	
	private Vector3f FacingDirection = new Vector3f();
	
	private float pitch = 20;
	private float yaw;
	private float roll;	
	private float distanceToPlayer = 30;
	private float angleAroundPlayer;
	private final float fpsCameraOffset = 4;
	
	private static boolean firstPersonMode = true;
	
	private Player player;
	private Terrain[][] terrains;
		
	public Camera(Player player, Terrain[][] terrains) {
		this.player = player;
		this.terrains = terrains;
	}

	public void update(){
		checkInputs();
		
		if(firstPersonMode){
			this.position.x = player.getPosition().x;
			this.position.y = player.getPosition().y + fpsCameraOffset;
			this.position.z = player.getPosition().z;
			this.yaw = 180 - (player.getRotY());
			this.pitch = player.getRotX();
			
			
		} else{
			calculateZoom();
			calculateCameraPitch();
			calculateAngleAroundPlayer();
			calculateCameraPosition();
			calculateFacingDirection();
		}
		
		
		
	}	




	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_1)){
			firstPersonMode = true;
			this.pitch = 0;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_2)){
			firstPersonMode = false;
			this.pitch = 20;
		}		
	
	}

	private void calculateCameraPosition() {
		//Horizontal and vertical distances
		float horizontalDistanceFromPlayer = (float) (distanceToPlayer * Math.cos(Math.toRadians(pitch)));
		float verticalDistanceFromPlayer = (float) (distanceToPlayer * Math.sin(Math.toRadians(pitch)));
		this.position.y = verticalDistanceFromPlayer +  player.getPosition().y;	
		
		Vector2f terrainIndex = Terrain.getTerrainIndex(this.position.x, this.position.z);
		float terrainHeight = terrains[(int) terrainIndex.x][(int) terrainIndex.y].getTerrainHeight(this.position.x, this.position.z);
		if(this.position.y < terrainHeight){
			this.position.y = terrainHeight + 1f;
		}
		
		//x and x offsets (as viewed from a 90 degree angle above the player)
		float theta = player.getRotY() + angleAroundPlayer;
		float xOffsetFromPlayer = (float) (horizontalDistanceFromPlayer * Math.sin(Math.toRadians(theta)));
		float zOffsetFromPlayer = (float) (horizontalDistanceFromPlayer * Math.cos(Math.toRadians(theta)));
		this.position.x = player.getPosition().x - xOffsetFromPlayer;
		this.position.z = player.getPosition().z - zOffsetFromPlayer;
		
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		
		
		
		
	}

	private void calculateAngleAroundPlayer() {
		if(Mouse.isButtonDown(0)){
			angleAroundPlayer -= Mouse.getDX() * 0.3f;
		}
		
	}

	private void calculateCameraPitch() {
		if(Mouse.isButtonDown(1)){
			pitch += Mouse.getDY() * 0.1f;
			if(pitch < 2){
				pitch = 2;
			} else if(pitch > 90){
				pitch = 90;
			}
		} 
		
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() *0.01f;
		distanceToPlayer -= zoomLevel;
		if(distanceToPlayer < 2){
			distanceToPlayer = 2;
		} else if(distanceToPlayer > 50){
			distanceToPlayer = 50;
		}		
	}
	
	private void calculateFacingDirection() {
		float xFacing = (float) Math.sin(Math.toRadians(yaw));
		float zFacing = (float) Math.cos(Math.toRadians(yaw));
		float yFacing = (float) Math.sin(Math.toRadians(pitch));
		this.FacingDirection.set(xFacing, yFacing,zFacing);

		
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	public static boolean isFirstPersonMode() {
		return firstPersonMode;
	}

	public Vector3f getFacingDirection() {
		return FacingDirection;
	}

	
	
	
}
