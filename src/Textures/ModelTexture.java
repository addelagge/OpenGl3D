package Textures;

public class ModelTexture {
	
	private int textureID;
	private float shineDamper = 1.0f;
	private float reflectivity = 1.0f;
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	private boolean skipSpecular = false;
	private int numberOfRows = 1;
	
	public ModelTexture(int textureID) {
		this.textureID = textureID;
	}
	
	public ModelTexture(int textureID, int numberOfRows) {
		this.textureID = textureID;
		this.numberOfRows = numberOfRows;
	}
	

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public int getTextureID() {
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public boolean isSkipSpecular() {
		return skipSpecular;
	}

	public void setSkipSpecular(boolean skipSpecular) {
		this.skipSpecular = skipSpecular;
	}
	

}
