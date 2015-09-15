package engine.entities;

import engine.math.Vector3f;
import engine.model.TexturedModel;

public class EntityLamp extends EntityEnvironment{
	public EntityLamp(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, float width, float length, float height, Light light) {
		super(model, index, position, rotX, rotY, rotZ, scale, width, length, height);
		this.light = light;
	}
	public EntityLamp(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, float width, float length, float height, Light light) {
		super(model, position, rotX, rotY, rotZ, scale, width, length, height);
		this.light = light;
	}
	public EntityLamp(){}
	public Light light;
	public Light getLight(){
		return light;
	}
	
}
