package RenderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import Entities.Entity;
import HelperClasses.CollisionBox;
import Models.RawModel;
import Shaders.CollisionShader;
import ToolBox.Maths;

public class CollisionboxRenderer {
	private CollisionShader shader;
	
	public CollisionboxRenderer(CollisionShader collisionShader, Matrix4f projectionMatrix){
		this.shader = collisionShader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();		
		
	}
	
	
	public void render(List<Entity> entities){
		MasterRenderer.disableCulling();
		for(Entity entity : entities){
			RawModel model = entity.getCollisionBox().getRawmodel();
			
			GL30.glBindVertexArray(model.getVaoID());			
			GL20.glEnableVertexAttribArray(0);
			
			Matrix4f matrix = Maths.createTransformationMatrix(entity.getPosition(), 0,0,0,1);
			shader.loadTransformationMatrix(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getNumberOfVertices());
			
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
		}		
		
		MasterRenderer.enableCulling();
		

		
	}

}
