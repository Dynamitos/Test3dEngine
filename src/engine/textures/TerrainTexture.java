package engine.textures;

import java.io.Serializable;

public class TerrainTexture implements Serializable{
	private int textureID;

	public int getTextureID() {
		return textureID;
	}

	public TerrainTexture(int textureID) {
		this.textureID = textureID;
	}

	
}
