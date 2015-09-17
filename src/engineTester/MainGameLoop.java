package engineTester;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import engine.data.Data;
import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.Light;
import engine.entities.Player;
import engine.guis.GuiRenderer;
import engine.guis.GuiTexture;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.renderEngine.DisplayManager;
import engine.renderEngine.Loader;
import engine.renderEngine.MasterRenderer;
import engine.renderEngine.OBJFileLoader;
import engine.renderEngine.OBJLoader;
import engine.terrains.Terrain;
import engine.textures.ModelTexture;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import engine.toolbox.Input;
import engine.toolbox.MousePicker;

public class MainGameLoop {

	public static volatile boolean running;

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		Data.loadResources(loader);
		RawModel model = Data.models.get("dragon");
		ModelTexture tex = Data.textures.get("white");
		Player player = new Player(new TexturedModel(model, tex), new Vector3f(100, 100, 100), 0, 0, 0, 1);
		TerrainTexture backgroundTexture = new TerrainTexture(Data.textures.get("grassy").getTextureID());
		TerrainTexture rTexture = new TerrainTexture(Data.textures.get("mud").getTextureID());
		TerrainTexture gTexture = new TerrainTexture(Data.textures.get("grassFlowers").getTextureID());
		TerrainTexture bTexture = new TerrainTexture(Data.textures.get("path").getTextureID());
		TerrainTexturePack tp = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(Data.textures.get("blendMap").getTextureID());
		TerrainTexture displacementMap = new TerrainTexture(loader.loadTexture("displacementMap.png"));
		Terrain terrain = new Terrain(0, 0, loader, tp, blendMap, displacementMap, "heightMap");
		Data.chunks.addTerrain(0, 0, terrain);
		Data.lights.add(new Light(new Vector3f(400, 1000, 400), new Vector3f(1, 1, 1)));
		Data.chunks.get(0, 0).addEntity(player);
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader);
		System.out.println(GL11.glGetString(GL11.GL_VERSION));
		running = true;
		while (running) {
			camera.move();
			player.move();
			renderer.render(Data.lights, camera);
			DisplayManager.updateDisplay();
			if (glfwWindowShouldClose(DisplayManager.window) == GL_TRUE || Input.keys[GLFW_KEY_ESCAPE])
				running = false;
		}
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		Data.saveResources();
	}

}
