package engine.terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import engine.data.Data;
import engine.entities.Entity;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.model.RawModel;
import engine.model.TexturedModel;
import engine.renderEngine.Loader;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import engine.toolbox.Maths;

public class Terrain implements Serializable{
	public static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

	private float x, z;
	private int gridX, gridZ;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	private TerrainTexture displacementMap;
	private Map<TexturedModel, List<Entity>> entities;

	private float[][] heights;

	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, TerrainTexture displacementMap,
			String heightMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.displacementMap = displacementMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.gridX = gridX;
		this.gridZ = gridZ;
		this.model = generateTerrain(loader, heightMap);
		entities = new HashMap<>();
	}

	public Terrain() {
	}

	public void addEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}

	public Set<Terrain> getNearTerrains(int count, int x, int z) {
		if (count == 0)
			return new HashSet<>();
		Set<Terrain> result = new HashSet<>();
		result.add(this);
		int currX = (int) (this.x / SIZE);
		int currZ = (int) (this.z / SIZE);
		count -= 1;
		Terrain n = Data.chunks.get(currX + 1, currZ);
		Terrain s = Data.chunks.get(currX - 1, currZ);
		Terrain e = Data.chunks.get(currX, currZ + 1);
		Terrain w = Data.chunks.get(currX, currZ - 1);
		if (!(n == null)) {
			if (n.x / SIZE != x || n.z / SIZE != z)
				result.addAll(n.getNearTerrains(count, n.getGridX(), n.getGridZ()));
		}
		if (!(s == null)) {
			if (s.x / SIZE != x || s.z / SIZE != z)
				result.addAll(s.getNearTerrains(count, s.getGridX(), s.getGridZ()));
		}
		if (!(e == null)) {
			if (e.x / SIZE != x || e.z / SIZE != z)
				result.addAll(e.getNearTerrains(count, e.getGridX(), e.getGridZ()));
		}
		if (!(w == null)) {
			if (w.x / SIZE != x || w.z / SIZE != z)
				result.addAll(w.getNearTerrains(count, w.getGridX(), w.getGridZ()));
		}
		return result;
	}

	public Map<TexturedModel, List<Entity>> getEntities() {
		return entities;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}
	public TerrainTexture getDisplacementMap(){
		return displacementMap;
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = SIZE / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;
		if (xCoord <= (1 - zCoord)) {
			answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		}
		return answer;
	}

	private RawModel generateTerrain(Loader loader, String heightMap) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int VERTEX_COUNT = image.getHeight();
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				float height = getHeight(j, i, image);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i, image);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x - 1, z, image);
		float heightR = getHeight(x + 1, z, image);
		float heightD = getHeight(x, z - 1, image);
		float heightU = getHeight(x, z + 1, image);

		Vector3f normal = new Vector3f(heightL - heightR, 2, heightD - heightU);
		normal = normal.normalize();
		return normal;
	}

	private float getHeight(int x, int z, BufferedImage image) {
		if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
			return 0;
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR / 2f;
		height /= MAX_PIXEL_COLOR / 2f;
		height *= MAX_HEIGHT;
		return height;
	}

	public int getGridX() {
		return gridX;
	}

	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	public int getGridZ() {
		return gridZ;
	}

	public void setGridZ(int gridZ) {
		this.gridZ = gridZ;
	}

	@Override
	public String toString() {
		return "Terrain [x=" + x + ", z=" + z + "]";
	}

	public void remove(Entity entity) {
		List<Entity> batch = entities.get(entity.getModel());
		batch.remove(entity);
	}
}
