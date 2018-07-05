package Models;

import Textures.ModelTexture;

public class TexturedModel {
	private RawModel rawmodel;
	private ModelTexture texture;
	
	public TexturedModel(RawModel rawmodel, ModelTexture texture) {
		this.rawmodel = rawmodel;
		this.texture = texture;
	}

	public RawModel getRawmodel() {
		return rawmodel;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
	

}
