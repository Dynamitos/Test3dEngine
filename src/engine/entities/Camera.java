package engine.entities;

import java.net.MalformedURLException;

import engine.data.Data;
import engine.math.Vector3f;
import engine.renderEngine.DisplayManager;
import engine.terrains.Terrain;
import engine.toolbox.MouseInput;

public class Camera {

	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;

	private Vector3f position;
	private float pitch;
	private float yaw = 180 - angleAroundPlayer;
	private float roll;

	private Player player;
	private float terrainHeight = 0;

	public Camera(Player player) {
		position = new Vector3f(0, 1, 0);
		this.player = player;
	}

	public void move() {
		calculateAngleAroundPlayer();
		calculatePitch();
		calculateZoom();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = angleAroundPlayer;
		float xoffset = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float zoffset = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - xoffset;
		position.z = player.getPosition().z - zoffset;
		position.y = player.getPosition().y + verticalDistance;
		Terrain terrain = Data.chunks.get((int)( getPosition().x/Terrain.SIZE), (int) (getPosition().z/Terrain.SIZE));
		terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		if (getPosition().y < terrainHeight+1) {
			getPosition().y = terrainHeight+1;
		}
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = (float) MouseInput.rotation * 2f;
		MouseInput.rotation = 0;
		distanceFromPlayer -= zoomLevel;
	}

	private void calculatePitch() {
		float pitchChange = DisplayManager.deltaY * 0.3f;
		pitch += pitchChange;
		if(pitch > 90){
			pitch = 90;
		}
		if(pitch < -90){
			pitch = -90;
		}
	}

	private void calculateAngleAroundPlayer() {
		float angleChange = DisplayManager.deltaX * 0.3f;
		angleAroundPlayer -= angleChange;
		yaw+=angleChange;
		player.setRotY(player.getRotY()-angleChange);
	}
}
