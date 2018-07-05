package Entities;

import org.lwjgl.util.vector.Vector3f;

import Models.TexturedModel;
import RenderEngine.Loader;

public class LightEntity extends Entity{
	
	private Light light;
	
	public LightEntity(TexturedModel texmodel, Vector3f position, Vector3f lightColor, Loader loader) {
		super(texmodel, position, 0, 0, 0, 1, loader);
		Vector3f lightPosition = new Vector3f(position.x, position.y + 9, position.z);
		light = new Light(lightPosition, lightColor, new Vector3f(1,0.01f, 0.002f));
	}
	
	public LightEntity(TexturedModel texmodel, Vector3f position, Vector3f lightColor, Vector3f attenuation, Loader loader) {
		super(texmodel, position, 0, 0, 0, 1, loader);
		Vector3f lightPosition = new Vector3f(position.x, position.y + 8, position.z);
		light = new Light(lightPosition, lightColor, attenuation);
	}

	public Light getLight() {
		return light;
	}
	
	


}
