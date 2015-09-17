package engine.data;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.EntityEnvironment;
import engine.entities.EntityMob;
import engine.entities.Light;
import engine.guis.GuiTexture;
import engine.model.ModelData;
import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.renderEngine.Loader;
import engine.renderEngine.MasterRenderer;
import engine.renderEngine.OBJFileLoader;
import engine.terrains.Terrain;
import engine.textures.ModelTexture;

public class Data {
	public static Map<String, RawModel> models;
	public static Map<String, ModelTexture> textures;
	public static Map<Integer, EntityEnvironment> environment;
	public static Map<Integer, EntityMob> mobs;
	public static TerrainMap chunks;
	public static List<Light> lights;
	public static List<GuiTexture> guis;

	public static void loadResources(Loader loader) {
		models = new HashMap<>();
		textures = new HashMap<>();
		environment = new HashMap<>();
		chunks = new TerrainMap();
		mobs = new HashMap<>();
		lights = new ArrayList<>();
		guis = new ArrayList<>();
		
		File res = new File("res");
		File[] resources = res.listFiles();
		for (File file : resources) {
			if (file.getName().endsWith(".obj")) {
				ModelData data = OBJFileLoader.loadOBJ(file.getName());
				models.put(file.getName().substring(0, file.getName().indexOf(".")), loader
						.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices()));
			} else if (file.getName().endsWith(".png")) {
				textures.put(file.getName().substring(0, file.getName().indexOf(".")),
						new ModelTexture(loader.loadTexture(file.getName())));
			} else if (file.getName().endsWith(".dat")) {
				FileInputStream stream = null;
				ObjectInputStream ois = null;
				try {
					stream = new FileInputStream(file);
					ois = new ObjectInputStream(stream);
					Object o = null;
					while((o = ois.readObject())!=null){
						if(o.getClass().toString().contains("Terrain")){
							Terrain terrain = (Terrain)o;
							chunks.addTerrain(terrain.getGridX(), terrain.getGridZ(), terrain);
							System.out.println("terrain "+terrain);
						}
						if(o.getClass().toString().contains("Entity")){
							System.out.println("entity "+o.getClass());
						}
					}
				} catch (Exception e) {
					try {
						stream.close();
						ois.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	public static void saveResources(){
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream("res/data.dat");
			oos = new ObjectOutputStream(fos);
		for (EntityEnvironment environment : environment.values()) {
			oos.writeObject(environment);
		}
		for (EntityMob mob : mobs.values()) {
			oos.writeObject(mob);
		}
		for (Terrain terrain : chunks.getAll()) {
			oos.writeObject(terrain);
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	private static void processTerrain(String[] tokens, Loader loader) {
//		int gridX = Integer.parseInt(tokens[1]);
//		int gridZ = Integer.parseInt(tokens[2]);
//		String heightMap = tokens[3];
//		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture(tokens[4] + ".png"));
//		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture(tokens[5] + ".png"));
//		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture(tokens[6] + ".png"));
//		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture(tokens[7] + ".png"));
//		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
//		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(tokens[8] + ".png"));
//		TerrainTexture displacementMap = new TerrainTexture(loader.loadTexture(tokens[9]+".png"));
//		Terrain terrain = new Terrain(gridX, gridZ, loader, texturePack, blendMap, displacementMap, heightMap);
//		chunks.addTerrain(gridX, gridZ, terrain);
//	}
//
//	private static void processEntity(String[] tokens, Loader loader) {
//		if (tokens[1].equals("env")) {
//			int x = (int) (Float.parseFloat(tokens[2]) / Terrain.SIZE);
//			int z = (int) (Float.parseFloat(tokens[3]) / Terrain.SIZE);
//			String modelName = tokens[4];
//			String texName = tokens[5];
//			int index = Integer.parseInt(tokens[6]);
//			float rotX = Float.parseFloat(tokens[7]);
//			float rotY = Float.parseFloat(tokens[8]);
//			float rotZ = Float.parseFloat(tokens[9]);
//			float scale = Float.parseFloat(tokens[10]);
//			float width = Float.parseFloat(tokens[11]);
//			float length = Float.parseFloat(tokens[12]);
//			float height = Float.parseFloat(tokens[13]);
//			ModelData data = OBJFileLoader.loadOBJ(modelName + ".obj");
//			ModelTexture tex = new ModelTexture(loader.loadTexture(texName + ".png"));
//			TexturedModel model = new TexturedModel(
//					loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices()),
//					tex);
//			Terrain terrain = chunks.get(x, z);
//			EntityEnvironment env = new EntityEnvironment(model, index,
//					new Vector3f(x, terrain.getHeightOfTerrain(x, z), z), rotX, rotY, rotZ, scale, width, length,
//					height);
//			terrain.addEntity(env);
//		} else if (tokens[1].equals("lamp")) {
//			float x = Float.parseFloat(tokens[2]);
//			float z = Float.parseFloat(tokens[3]);
//			String modelName = tokens[4];
//			String texName = tokens[5];
//			int index = Integer.parseInt(tokens[6]);
//			float rotX = Float.parseFloat(tokens[7]);
//			float rotY = Float.parseFloat(tokens[8]);
//			float rotZ = Float.parseFloat(tokens[9]);
//			float scale = Float.parseFloat(tokens[10]);
//			float width = Float.parseFloat(tokens[11]);
//			float length = Float.parseFloat(tokens[12]);
//			float height = Float.parseFloat(tokens[13]);
//			float r = Float.parseFloat(tokens[14]);
//			float g = Float.parseFloat(tokens[15]);
//			float b = Float.parseFloat(tokens[16]);
//			Vector3f att;
//			if (tokens.length == 17) {
//				att = new Vector3f(1, 0, 0);
//			} else {
//				att = new Vector3f(Float.parseFloat(tokens[17]), Float.parseFloat(tokens[18]),
//						Float.parseFloat(tokens[19]));
//			}
//			ModelData data = OBJFileLoader.loadOBJ(modelName + ".obj");
//			ModelTexture tex = new ModelTexture(loader.loadTexture(texName + ".png"));
//			TexturedModel model = new TexturedModel(
//					loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices()),
//					tex);
//			Terrain terrain = chunks.get((int) (x / Terrain.SIZE), (int) (z / Terrain.SIZE));
//			Vector3f position = new Vector3f(x, terrain.getHeightOfTerrain(x, z)+height, z);
//			Light light = new Light(position, new Vector3f(r, g, b), att);
//			EntityLamp lamp = new EntityLamp(model, index, position, rotX, rotY, rotZ, scale, width, length, height,
//					light);
//			terrain.addEntity(lamp);
//			lights.add(light);
//		}
//	}

	public static List<Map<TexturedModel, List<Entity>>> getEntities(Camera camera) {
		List<Map<TexturedModel, List<Entity>>> chunkEntities = new ArrayList<>();
		for (Terrain map : chunks.getCurrentTerrains(MasterRenderer.RENDER_DISTANCE,
				(int) (camera.getPosition().x / Terrain.SIZE), (int) (camera.getPosition().z / Terrain.SIZE))) {
			chunkEntities.add(map.getEntities());
		}
		return chunkEntities;

	}
}
