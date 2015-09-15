package engine.textures;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class TextureData implements Serializable{
	
	private int width;
	private int height;
	private ByteBuffer buffer;
	
	public TextureData(ByteBuffer buffer, int width, int height){
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public ByteBuffer getBuffer(){
		return buffer;
	}

}
