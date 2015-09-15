package engine.entities;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickAxes;

import java.nio.FloatBuffer;

import engine.data.Data;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.model.TexturedModel;
import engine.renderEngine.DisplayManager;
import engine.terrains.Terrain;
import engine.toolbox.Input;

public class Player extends Entity {

	private static final float RUN_SPEED = 50;
	public static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;

	private static float terrainHeight = 0;

	private float currentSpeed = 0;
	private float currentStriveSpeed = 0;
	private float upwardsSpeed = 0;

	private boolean isInAir = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		// TODO Auto-generated constructor stub
	}

	public void move() {
		checkInputs();
		//checkJoysticks();
		float striveDistance = currentStriveSpeed * DisplayManager.getFrameTimeSeconds();
		float dxStrive = (float) (striveDistance * Math.sin(Math.toRadians(super.getRotY()+90)));
		float dzStrive = (float) (striveDistance * Math.cos(Math.toRadians(super.getRotY()+90)));
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		Terrain terrain = Data.chunks.get((int)( super.getPosition().x/Terrain.SIZE), (int) (super.getPosition().z/Terrain.SIZE));
		if(super.getPosition().y-terrain.getHeightOfTerrain(super.getPosition().x+dx, super.getPosition().x+dz)>=100&&!isInAir){
			dx = 0;
			dz = 0;
		}
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.move(dx+dxStrive, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), dz+dzStrive);
		terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
			isInAir = false;
		}
	}

	private void jump() {
		this.upwardsSpeed = JUMP_POWER;
		isInAir = true;
	}

	private void checkInputs() {
		if (Input.keys[GLFW_KEY_W]) {
			this.currentSpeed = RUN_SPEED;
		} else if (Input.keys[GLFW_KEY_S]) {
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}

		if (Input.keys[GLFW_KEY_D]) {
			this.currentStriveSpeed = -RUN_SPEED;
		} else if (Input.keys[GLFW_KEY_A]) {
			this.currentStriveSpeed = RUN_SPEED;
		} else {
			this.currentStriveSpeed = 0;
		}
		if (( Input.keys[GLFW_KEY_SPACE]) && !isInAir) {
			jump();
		}
	}
	private void checkJoysticks(){
		FloatBuffer delta = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
		currentStriveSpeed = -RUN_SPEED * delta.get(0);
		currentSpeed = -RUN_SPEED * delta.get(1);
	}
}
