package RenderEngine;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class DisplayManager {
	
	private static long lastFrameTime;
	private static float deltaTime;
	
	public static void createDisplay(){
		try {

			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, 800, 600);
		lastFrameTime = getCurrentTime();
	}

	public static void closeDisplay(){
		Display.destroy();
		
	}
	
	public static void updateDisplay(){
		Display.sync(120);
		Display.update();
		long currentFrameTime = getCurrentTime();
		deltaTime = (currentFrameTime - lastFrameTime)/1000.f;
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTime(){
		return deltaTime;
	}

	private static long getCurrentTime() {
		return Sys.getTime() *1000 / Sys.getTimerResolution();
	}
}
