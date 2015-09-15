package engine.entities;

import java.io.Serializable;

import engine.data.Data;
import engine.math.Vector3f;
import engine.model.TexturedModel;
import engine.terrains.Terrain;

public abstract class Entity implements Serializable{
	private TexturedModel model;
	protected Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;

	private int textureIndex = 0;

	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}
	public Entity(){}
	public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.textureIndex = index;
	}

	public float getTextureXOffset() {
		int column = textureIndex % model.getTexture().getNumberOfRows();
		return (float) column / (float) model.getTexture().getNumberOfRows();
	}

	public float getTextureYOffset() {
		int row = textureIndex / model.getTexture().getNumberOfRows();
		return (float) row / (float) model.getTexture().getNumberOfRows();
	}

	public void move(float dx, float dy, float dz) {
		int currentX = (int) (this.position.x/Terrain.SIZE);
		int currentZ = (int) (this.position.z/Terrain.SIZE);
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
		int newX = (int) (this.position.x/Terrain.SIZE);
		int newZ = (int) (this.position.z/Terrain.SIZE);
		if(newX!=currentX||newZ!=currentZ){
			Data.chunks.onEntityTerrainChange(currentX, currentZ, newX, newZ, this);
			System.out.println("changed from "+currentX+"|"+currentZ+" to "+newX+"|"+newZ);
		}
	}

	public void rotate(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	public int getTextureIndex() {
		return textureIndex;
	}
	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}
}
