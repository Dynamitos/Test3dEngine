package engineTester;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import java.util.ArrayList;
import java.util.List;

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
//		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy.png"));
//		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud.png"));
//		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers.png"));
//		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path.png"));
//		TerrainTexturePack tp = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
//		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap.png"));
//		TerrainTexture displacementMap = new TerrainTexture(loader.loadTexture("displacementMap.png"));
//		Terrain terrain = new Terrain(0, 0, loader, tp, blendMap, displacementMap, "heightMap");
//		Data.chunks.addTerrain(0, 0, terrain);
		Data.chunks.get(0, 0).addEntity(player);
		Data.guis.add(new GuiTexture(loader.loadTexture("aim.png"), new Vector2f(0, 0), new Vector2f(0.25f, 0.25f)));
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader);
		
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
