package engine.shaders;

import java.util.List;

import engine.entities.Camera;
import engine.entities.Light;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.toolbox.Maths;

public class TerrainShader extends ShaderProgram {

	private static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = "src/engine/shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/engine/shaders/terrainFragmentShader.txt";
	private static final String TESSELATIONCONTROL_FILE = "src/engine/shaders/tesselationControlShader.txt";
	private static final String TESSELATIONEVALUATION_FILE = "src/engine/shaders/tesselationEvaluationShader.txt";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_attenuation[];
	private int location_reflectivity;
	private int location_shineDamper;
	private int location_skyColor;
	private int location_density;
	private int location_gradient;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_cameraPos;
	private int location_displacementFactor;
	private int location_displacementMap;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, TESSELATIONCONTROL_FILE, TESSELATIONEVALUATION_FILE);
		start();
		getAllUniformLocations();
		stop();
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_skyColor = super.getUniformLocation("skyColor");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
		location_backgroundTexture = super.getUniformLocation("backgroundTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("bTexture");
		location_blendMap = super.getUniformLocation("blendMap");
		location_cameraPos = super.getUniformLocation("cameraPos");
		location_displacementFactor = super.getUniformLocation("displacementFactor");
		location_displacementMap = super.getUniformLocation("displacementMap");
		location_lightColor = new int[MAX_LIGHTS];
		location_lightPosition = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}
	public void loadCameraPos(Vector3f cameraPos){
		super.loadVector(location_cameraPos, cameraPos);
	}
	public void connectTextureUnits() {
		super.loadInt(location_backgroundTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
	}

	public void loadDisplacement(float displacementFactor){
		super.loadInt(location_displacementMap, 5);
		super.loadFloat(location_displacementFactor, displacementFactor);
	}

	public void loadFog(float gradient, float density) {
		super.loadFloat(location_density, density);
		super.loadFloat(location_gradient, gradient);
	}

	public void loadSkyColor(Vector3f skyColor) {
		super.loadVector(location_skyColor, skyColor);
	}

	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_reflectivity, reflectivity);
		super.loadFloat(location_shineDamper, damper);
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

	public void loadLights(List<Light> lights) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector(location_lightColor[i], lights.get(i).getColor());
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(location_lightColor[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}

}
