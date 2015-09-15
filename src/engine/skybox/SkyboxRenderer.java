package engine.skybox;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import engine.entities.Camera;
import engine.math.Matrix4f;
import engine.model.RawModel;
import engine.renderEngine.DisplayManager;
import engine.renderEngine.Loader;

public class SkyboxRenderer {
	private static final float SIZE = 512f;
	
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	private static String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
	private static String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};
	
	private RawModel cube;
	private int texture;
	private int nightTexture;
	private SkyboxShader shader;
	private float time = 0;
	
	public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix){
		cube = loader.loadToVAO(VERTICES, 3);
		texture = loader.loadCubeMap(TEXTURE_FILES);
		nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	public void render(Camera camera, float r, float g, float b){
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadFogColor(r, g, b);
		glBindVertexArray(cube.getVaoID());
		glEnableVertexAttribArray(0);
		glActiveTexture(GL_TEXTURE0);
		bindTextures();
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shader.stop();
	}
	private void bindTextures(){
		time += DisplayManager.getFrameTimeSeconds() * 1000;
		time %= 24000;
		int texture1;
		int texture2;
		float blendFactor;		
		if(time >= 0 && time < 5000){
			texture1 = nightTexture;
			texture2 = nightTexture;
			blendFactor = (time - 0)/(5000 - 0);
		}else if(time >= 5000 && time < 8000){
			texture1 = nightTexture;
			texture2 = texture;
			blendFactor = (time - 5000)/(8000 - 5000);
		}else if(time >= 8000 && time < 21000){
			texture1 = texture;
			texture2 = texture;
			blendFactor = (time - 8000)/(21000 - 8000);
		}else{
			texture1 = texture;
			texture2 = nightTexture;
			blendFactor = (time - 21000)/(24000 - 21000);
		}

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, texture1);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlendFactor(blendFactor);
	}
}
