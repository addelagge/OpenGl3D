package Textures;

import RenderEngine.Loader;

public class TexturePack {
	
	private ModelTexture texture0;
	private ModelTexture texture1;
	private ModelTexture texture2;
	private ModelTexture texture3;
	private ModelTexture texture4;
	
	public TexturePack(Loader loader){
		texture0 = new ModelTexture(loader.loadTexture("blendMap"));
		texture1 = new ModelTexture(loader.loadTexture("grassy2"));
		texture2 = new ModelTexture(loader.loadTexture("grassFlowers"));
		texture3 = new ModelTexture(loader.loadTexture("mud"));
		texture4 = new ModelTexture(loader.loadTexture("path"));
	}

	public ModelTexture getTexture0() {
		return texture0;
	}

	public ModelTexture getTexture1() {
		return texture1;
	}

	public ModelTexture getTexture2() {
		return texture2;
	}

	public ModelTexture getTexture3() {
		return texture3;
	}

	public ModelTexture getTexture4() {
		return texture4;
	}	

}
