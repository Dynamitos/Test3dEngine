package engine.entities;

import engine.math.Vector3f;
import engine.model.TexturedModel;
import engine.renderEngine.DisplayManager;

public class EntityMob extends Entity {

	private float width, length, height;
	private static final float RUN_SPEED = 50;

	public EntityMob(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale,
			float width, float length, float height) {
		super(model, index, position, rotX, rotY, rotZ, scale);
		this.width = width;
		this.length = length;
		this.height = height;
	}

	public boolean isColliding(Vector3f position) {
		if (super.position.x - width / 2 <= position.x && super.position.x + width / 2 >= position.x) {
			if (super.position.y - height / 2 <= position.y && super.position.y + height / 2 >= position.y) {
				if (super.position.z - length / 2 <= position.z && super.position.z + length / 2 >= position.z) {
					return true;
				}
			}
		}
		return false;
	}
}
