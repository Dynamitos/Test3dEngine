package engine.renderEngine;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.List;
import java.util.Map;

import engine.data.Data;
import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.Light;
import engine.entities.Player;
import engine.guis.GuiRenderer;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.model.TexturedModel;
import engine.shaders.StaticShader;
import engine.shaders.TerrainShader;
import engine.skybox.SkyboxRenderer;
import engine.terrains.Terrain;

public class MasterRenderer {
	
	private Matrix4f projectionMatrix;
	
	private StaticShader entityShader;
	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000f;
	public static final int RENDER_DISTANCE = 3;

	private static final Vector3f SKY_COLOR = new Vector3f(0.54f, 0.62f, 0.69f);
	private static final float density = 0, gradient = 0;// = 0.0035f, gradient = 1f;

	private SkyboxRenderer skyboxRenderer;
	private GuiRenderer guiRenderer;
	
	public MasterRenderer(Loader loader) {
		enableCulling();
		createProjectionMatrix();
		entityShader = new StaticShader();
		entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
		terrainShader = new TerrainShader();
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		guiRenderer = new GuiRenderer(loader);
	}

	public static void enableCulling(){
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}
	public static void disableCulling(){
		glDisable(GL_CULL_FACE);
	}
	
	public void render(List<Light> light, Camera camera){
		prepare();
		entityShader.start();
		entityShader.loadSkyColor(SKY_COLOR);
		entityShader.loadLights(light);
		entityShader.loadViewMatrix(camera);
		entityShader.loadFog(gradient, density);
		for (Map<TexturedModel, List<Entity>> chunk : Data.getEntities(camera)) {
			entityRenderer.render(chunk);
		}
		entityShader.stop();
		terrainShader.start();
		terrainShader.loadSkyColor(SKY_COLOR);
		terrainShader.loadLights(light);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadFog(gradient, density);
		terrainRenderer.render(Data.chunks.getCurrentTerrains(RENDER_DISTANCE, (int)(camera.getPosition().x/Terrain.SIZE), (int)(camera.getPosition().z/Terrain.SIZE)));
		terrainShader.stop();
		skyboxRenderer.render(camera, SKY_COLOR.x, SKY_COLOR.y, SKY_COLOR.z);
	}
	
	
	
	public void cleanUp(){
		entityShader.cleanUp();
		terrainShader.cleanUp();
		guiRenderer.cleanUp();
	}

	
	public void prepare() {
		glEnable(GL_DEPTH_TEST);
		glClearColor(SKY_COLOR.x, SKY_COLOR.y, SKY_COLOR.z, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}


	private void createProjectionMatrix() {
		float aspectRatio = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustrum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustrum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustrum_length);
		projectionMatrix.m33 = 0;

	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public StaticShader getEntityShader() {
		return entityShader;
	}

	public EntityRenderer getEntityRenderer() {
		return entityRenderer;
	}

	public TerrainRenderer getTerrainRenderer() {
		return terrainRenderer;
	}

	public TerrainShader getTerrainShader() {
		return terrainShader;
	}

	public static float getFov() {
		return FOV;
	}

	public static float getNearPlane() {
		return NEAR_PLANE;
	}

	public static float getFarPlane() {
		return FAR_PLANE;
	}

	public static Vector3f getSkyColor() {
		return SKY_COLOR;
	}

	public static float getDensity() {
		return density;
	}

	public static float getGradient() {
		return gradient;
	}

	public SkyboxRenderer getSkyboxRenderer() {
		return skyboxRenderer;
	}
}
