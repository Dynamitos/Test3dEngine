package engine.entities;

import engine.math.Vector3f;
import engine.model.TexturedModel;

public class EntityEnvironment extends Entity {

	private float width, length, height;
	
	public EntityEnvironment(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, float width, float length, float height) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.width = width;
		this.length = length;
		this.height = height;
	}
	public EntityEnvironment(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, float width, float length, float height) {
		super(model, index, position, rotX, rotY, rotZ, scale);
		this.width = width;
		this.length = length;
		this.height = height;
	}
	public EntityEnvironment(){
		
	}
	public boolean isColliding(Vector3f position){
		if(super.position.x-width/2 <= position.x&&super.position.x+width/2 >= position.x){
			if(super.position.y-height/2 <= position.y&&super.position.y+height/2 >= position.y){
				if(super.position.z-length/2 <= position.z&&super.position.z+length/2 >= position.z){
					return true;
				}
			}
		}
		return false;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getLength() {
		return length;
	}
	public void setLength(float length) {
		this.length = length;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
}
