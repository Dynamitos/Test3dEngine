package engine.skybox;

import engine.entities.Camera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.renderEngine.DisplayManager;
import engine.shaders.ShaderProgram;
import engine.toolbox.Maths;

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/engine/skybox/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/engine/skybox/skyboxFragmentShader.txt";

	private static final float ROTATE_SPEED = 1f;
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_blendFactor;
	private int location_cubeMap;
	private int location_cubeMap2;
	
	private float rotation = 0;
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	public void loadFogColor(float r, float g, float b){
		super.loadVector(location_fogColor, new Vector3f(r, g, b));
	}
	public void connectTextureUnits(){
		super.loadInt(location_cubeMap, 0);
		super.loadInt(location_cubeMap2, 1);
	}
	public void loadBlendFactor(float blend){
		super.loadFloat(location_blendFactor, blend);
	}
	public void loadViewMatrix(Camera camera) {
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		rotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		location_blendFactor = super.getUniformLocation("blendFactor");
		location_cubeMap = super.getUniformLocation("cubeMap");
		location_cubeMap2 = super.getUniformLocation("cubeMap2");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}