package Entities;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Models.TexturedModel;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import Terrain.Terrain;

public class Player extends Entity{
	
	private final float RUN_SPEED = 60;
	private final float TURN_SPEED = 160;
	private final float JUMP_POWER = 20;
	
	
	private float currentRunSpeed = 0;
	private float currentTurnSpeed = 0;
	private float currentStrafeSpeed = 0;
	private float upwardSpeed = 0;
	private float currentLookSpeed = 0;
	private float lastRotY = 0;
	
	private Vector3f facingDirection = new Vector3f();
	
	private boolean firstMouseMovement = true;
	
	private boolean inAir = false;
	private boolean hasTurned = false;
	
	private Vector2f screenSize = new Vector2f(800,600);
	
	private Vector3f toPlayerVector = new Vector3f();
	
	public Player(TexturedModel texmodel, Vector3f position, float rotX, float rotY, float rotZ, float scale, Loader loader) {
		super(texmodel, position, rotX, rotY, rotZ, scale, loader);
	}
	
	
	protected void move(Terrain[][] terrains){

		if(Camera.isFirstPersonMode()){
			checkfirstPersonInput();
		} else{
			check3rdPersonInput();
		}
		
		float currentFrameTime = DisplayManager.getFrameTime();
		
		super.increaseRotation(currentLookSpeed * currentFrameTime , currentTurnSpeed *  currentFrameTime, 0);
		
		float distanceToMove = currentRunSpeed * currentFrameTime;
		
		float xDirection;
		float zDirection;		
		float rotY = super.getRotY();
		
		double sinCalculation = Math.sin(Math.toRadians(rotY));
		double cosCalculation = Math.cos(Math.toRadians(rotY));
		
		if(inAir){
			xDirection = (float) (distanceToMove * Math.sin(Math.toRadians(lastRotY)));
			zDirection = (float) (distanceToMove * Math.cos(Math.toRadians(lastRotY)));	
		} else{
			xDirection = (float) (distanceToMove * sinCalculation);
			zDirection = (float) (distanceToMove * cosCalculation);
			
		}		
		
		if(hasTurned){	
			float xFacing = (float) sinCalculation;
			float zFacing = (float) cosCalculation;
			float yFacing = (float) Math.sin(Math.toRadians(-super.getRotX()));
			facingDirection.set(xFacing, yFacing, zFacing);
			facingDirection.normalise();
		}
		
		
		
		//Strafing
		float strafeAngle = rotY + 90;
		float strafeDistance = currentStrafeSpeed * currentFrameTime;
		float xStrafe = (float) (strafeDistance * Math.sin(Math.toRadians(strafeAngle)));
		float zStrafe = (float) (strafeDistance * Math.cos(Math.toRadians(strafeAngle)));	
		
		upwardSpeed += super.getGRAVITY() * currentFrameTime;
		float verticalDisplacement = upwardSpeed * currentFrameTime;
		
		super.increasePosition(xDirection + xStrafe, 
				   verticalDisplacement, 
				   zDirection + zStrafe);		
		
		Vector2f terrainIndex = Terrain.getTerrainIndex(super.getPosition().x, super.getPosition().z);

		float terrainHeight = terrains[(int) terrainIndex.x][(int) terrainIndex.y].getTerrainHeight(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y < terrainHeight){
			super.getPosition().y = terrainHeight;
			inAir = false;
			upwardSpeed = 0;
		}				
		
		
	}//End of move

	protected void checkBoundaries(Terrain[][] terrains){
		float largestXOnTerrain = terrains.length * Terrain.getSize();
		float largestZOnTerrain = terrains[0].length * Terrain.getSize();
		
		if(this.getPosition().x < 0){
			this.getPosition().x = 0;
		} else if(this.getPosition().x > largestXOnTerrain){
			this.getPosition().x = largestXOnTerrain;
		}
		
		if(this.getPosition().z < 0){
			this.getPosition().z = 0;
		} else if(this.getPosition().z > largestZOnTerrain){
			this.getPosition().z = largestZOnTerrain;
		}
	}

	private void checkfirstPersonInput() {
		
		
		if(!inAir){
			//Fwd Bkwd
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){
				currentRunSpeed = RUN_SPEED;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
				currentRunSpeed = -RUN_SPEED;			
			} else{
				currentRunSpeed = 0;
			}
			
			//Strafing
			if(Keyboard.isKeyDown(Keyboard.KEY_A)){
				currentStrafeSpeed = RUN_SPEED;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
				currentStrafeSpeed = -RUN_SPEED;			
			} else{
				currentStrafeSpeed = 0;
			}
			
			//Jumping
			if(Mouse.isButtonDown(1)){
				jump();
			}
			
			
		}
				
		if(firstMouseMovement){			
			currentLookSpeed = 0;
			currentTurnSpeed = 0;
			firstMouseMovement = false;
			Mouse.setCursorPosition((int)screenSize.x/2, (int)screenSize.y/2);
			
		} else{			
			//Turning
			int mouseX = Mouse.getX();
			int mouseY  = Mouse.getY();
			int deltaX = mouseX - (int)screenSize.x/2;
			int deltaY = mouseY - (int)screenSize.y/2;
			
			hasTurned = true;
			if(deltaX < 0){
				currentTurnSpeed = TURN_SPEED;
			} else if(deltaX > 0){
				currentTurnSpeed = -TURN_SPEED;			
			} else{
				currentTurnSpeed = 0;
				hasTurned = false;
			}
			
			hasTurned = true;
			if(deltaY < 0){
				currentLookSpeed = TURN_SPEED;
			} else if(deltaY > 0){
				currentLookSpeed = -TURN_SPEED;
			} else{
				currentLookSpeed = 0;
				hasTurned = false;
			}
			Mouse.setCursorPosition((int)screenSize.x/2, (int)screenSize.y/2);
		}
		
		
	}//End of checkfirstPersonInput
	

	private void check3rdPersonInput() {
		hasTurned = true;
		
		if(!inAir){
			
			//Fwd Bkwd
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){
				currentRunSpeed = RUN_SPEED;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
				currentRunSpeed = -RUN_SPEED;			
			} else{
				currentRunSpeed = 0;
			}
			
			//Strafing
			if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
				currentStrafeSpeed = RUN_SPEED;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_E)){
				currentStrafeSpeed = -RUN_SPEED;			
			} else{
				currentStrafeSpeed = 0;
			}
			
			//Jumping
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
				jump();
			}
		}		
		
		//Turning
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			currentTurnSpeed = TURN_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			currentTurnSpeed = -TURN_SPEED;			
		} else{
			currentTurnSpeed = 0;
			hasTurned = false;
		}	
		
		
		
		
	}//End of check3rdPersonInput
	
	

	private void jump() {
		upwardSpeed = JUMP_POWER;
		inAir = true;
		lastRotY = super.getRotY();

	}
	
	public Vector3f getFacingDirection(){
		return this.facingDirection;
	}
	
	

}
