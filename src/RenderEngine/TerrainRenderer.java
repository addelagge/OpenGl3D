package RenderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Models.RawModel;
import Shaders.TerrainShader;
import Terrain.Terrain;
import Textures.ModelTexture;
import Textures.TexturePack;
import ToolBox.Maths;

public class TerrainRenderer {
	
	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix){
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadTextureIndices();
		shader.stop();		
	}

	
	public void render(Terrain[][] terrains){
		for(int i=0; i < terrains.length; i++){
			for(int j=0; j < terrains[i].length; j++){
				activateModel(terrains[i][j]);
				prepareTerrain(terrains[i][j]);
				GL11.glDrawElements(GL11.GL_TRIANGLES, terrains[i][j].getModel().getNumberOfVertices(), GL11.GL_UNSIGNED_INT, 0);
				unbindModel();
			}
		}
//		for(Terrain terrain : terrains){
//			activateModel(terrain);
//			prepareTerrain(terrain);
//			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getNumberOfVertices(), GL11.GL_UNSIGNED_INT, 0);
//			unbindModel();
//		}
	}
	
	private void unbindModel() {		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
	}

	private void prepareTerrain(Terrain terrain) {
		Matrix4f transMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transMatrix);
		
	}

	private void activateModel(Terrain terrain) {
		RawModel rawmodel = terrain.getModel();
		GL30.glBindVertexArray(rawmodel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);		
		GL20.glEnableVertexAttribArray(2);	
		bindTerrainTextures(terrain);
		shader.loadShine(10, 1);
		
	}


	private void bindTerrainTextures(Terrain terrain) {
		TexturePack texturepack = terrain.getTexturepack();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturepack.getTexture0().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturepack.getTexture1().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturepack.getTexture2().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturepack.getTexture3().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturepack.getTexture4().getTextureID());
		
	}

}
