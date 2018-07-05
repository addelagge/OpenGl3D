package EngineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Entities.LightEntity;
import Entities.Player;
import Models.TexturedModel;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import RenderEngine.MasterRenderer;
import RenderEngine.OBJLoader;
import Terrain.Terrain;
import Textures.ModelTexture;
import ToolBox.MousePicker;

public class Main {

	public static void main(String[] args) {
		
		//System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();		
		MasterRenderer renderer = new MasterRenderer();
			

		//Player
//		System.out.println("tree");
		TexturedModel texmodel = new TexturedModel(OBJLoader.loadObjFile("tree", loader),new ModelTexture(loader.loadTexture("white")));
		Player player = new Player(texmodel, new Vector3f(0,0,0),0,0,0,1, loader);
		
		Terrain[][] terrains = generateTerrains(loader);
		Camera camera = new Camera(player, terrains);	

		//Lights
		Light sun = new Light(new Vector3f(400,400,400), new Vector3f(0.4f,0.4f,0.4f));
		
		List<LightEntity> lightEntities = new ArrayList<LightEntity>();
//		System.out.println("lamp");
		TexturedModel texlamp = new TexturedModel(OBJLoader.loadObjFile("lamp", loader), new ModelTexture(loader.loadTexture("lamp")));
		LightEntity lamp1 = new LightEntity(texlamp, new Vector3f(50,terrains[0][0].getTerrainHeight(50, 50),50),new Vector3f(3,3,2), loader);
		lightEntities.add(lamp1);
	
		
		TexturedModel texdragon = new TexturedModel(OBJLoader.loadObjFile("dragon", loader), new ModelTexture(loader.loadTexture("white")));
		Entity dragon = new Entity(texdragon, new Vector3f(100, terrains[0][0].getTerrainHeight(100, 100), 100), 0,0,0,1, loader);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);
		
		//Main game loop
		while(!Display.isCloseRequested()){					
			
			for(LightEntity lightEntity : lightEntities){
				renderer.processEntity(lightEntity);
			}
			
			renderer.processEntity(dragon);
			renderer.processEntity(player);	
			
			if(!Camera.isFirstPersonMode())									
				picker.update(renderer.getCollisionList());
					
			updateEntities(renderer.getCollisionList(), terrains);	
			
			camera.update();
			
			renderer.processLight(sun);	

			renderer.processTerrain(terrains);
			
			renderer.render(camera);
			
			DisplayManager.updateDisplay();
		}
		
		renderer.cleanUp();
		
		DisplayManager.closeDisplay();

	}


	private static Terrain[][] generateTerrains(Loader loader) {
		int numOfTerrains = MasterRenderer.getNumofterrains();
		Terrain[][] terrains = new Terrain[numOfTerrains][numOfTerrains];
		
		for(int i=0; i < numOfTerrains; i++){
			for(int j=0; j < numOfTerrains; j++){
				terrains[i][j] = new Terrain(i, j, loader, "heightmap");	
			}
		}

		return terrains;
	}


	private static void updateEntities(List<Entity> collisionList, Terrain[][] terrains) {
		for(int i = 0; i < collisionList.size(); i++){
			collisionList.get(i).update(terrains);
		}
		
		for(int i = 0; i < collisionList.size(); i++){
			Entity entity = collisionList.get(i);
			
			for(int j = i+1; j < collisionList.size(); j++){
				Entity otherEntity = collisionList.get(j); 
				entity.checkCollision(otherEntity);
			}
		}
		
	}


	private static ArrayList<Entity> generateMultipleObjects(int number, TexturedModel textedModel, Terrain terrain) {

		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		for(int i = 0; i < number; i++){
			float randX = (float) (Math.random() * 200);
			float randZ = (float) (Math.random() * 200);
			float y = terrain.getTerrainHeight(randX,randZ);
			int randomNr = (int) Math.floor(Math.random() *4);

//			Entity entity = new Entity(textedModel, randomNr, new Vector3f(randX, y, randZ), 0,0,0,1);
//			entities.add(entity);			
		}
		
		return entities;
	}

}
