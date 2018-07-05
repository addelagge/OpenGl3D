package RenderEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;
import Entities.Entity;
import Entities.Player;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.StaticShader;
import Textures.ModelTexture;
import ToolBox.Maths;

public class EntityRenderer {
	
	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix){
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	
	public void render(Map<TexturedModel, List<Entity>> entities){
		for(TexturedModel texmodel : entities.keySet()){
			bindModel(texmodel);
			List<Entity> batch = entities.get(texmodel);
	
			for(Entity entity : batch){		
				if(entity.getClass() == Player.class && Camera.isFirstPersonMode())
					continue;
				
				prepareEntity(entity);				
				GL11.glDrawElements(GL11.GL_TRIANGLES, texmodel.getRawmodel().getNumberOfVertices(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindModel();
		}
		
	}


	
	private Vector3f normalize(Vector3f vec){
		float x = vec.x;
		float y = vec.y;
		float z = vec.z;
		float hyp = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
		
		return new Vector3f(vec.x/hyp, vec.y/hyp,vec.z/hyp);		
	}


	private void unbindModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);		
		
		MasterRenderer.enableCulling();
	}

	private void prepareEntity(Entity entity) {
		Matrix4f transMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transMatrix);	
		shader.loadTextureOffsets(new Vector2f(entity.getTextureXOffset(), entity.getTextureYOffset()));
		shader.loadIsHighLighted(entity.isHighlighted());
		
	}

	private void bindModel(TexturedModel texmodel) {
		RawModel model = texmodel.getRawmodel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = texmodel.getTexture();
		shader.loadUseFakeLighting(texture.isUseFakeLighting());
		shader.loadShine(texture.getShineDamper(), texture.getReflectivity());		
		shader.loadNumberOfRowsInTextureAtlas((float)texture.getNumberOfRows());
		shader.loadUseSpecular(texture.isSkipSpecular());
		
		if(texture.isHasTransparency()){
			MasterRenderer.disableCulling();
		}
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texmodel.getTexture().getTextureID());
		
	}
	




}
