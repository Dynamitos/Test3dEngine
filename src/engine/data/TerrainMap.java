package engine.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import engine.entities.Entity;
import engine.terrains.Terrain;

public class TerrainMap{
	private List<List<Terrain>> terrains;
	public TerrainMap(){
		terrains = new ArrayList<List<Terrain>>();
	}
	public void addTerrain(int x, int z, Terrain terrain){
		if(terrains.size() <= x){
			addColumn(x);
		}
		terrains.get(x).add(terrain);
	}
	private void addColumn(int x){
		terrains.add(x, new ArrayList<>());
	}
	public Terrain get(int x, int z){
		if(x<0||z<0||x>=terrains.size()){
			return null;
		}
		if(z>=terrains.get(x).size()){
			return null;
		}
		return terrains.get(x).get(z);
	}
	
	public Set<Terrain> getCurrentTerrains(int count, int x, int z){
		return get(x, z).getNearTerrains(count, x, z);
	}
	public void update(){
		for (List<Terrain> list : terrains) {
			for (Terrain terrain : list) {
				for (List<Entity> entityList : terrain.getEntities().values()) {
					for (Entity entity : entityList) {
						if(!(entity.getPosition().x>=terrain.getX()&&entity.getPosition().x<=terrain.getX()+Terrain.SIZE)||!(entity.getPosition().z>=terrain.getZ()&&entity.getPosition().z<=terrain.getZ()+Terrain.SIZE)){
								entityList.remove(entity);
								System.out.println("removed");
								get((int)(entity.getPosition().x/Terrain.SIZE), (int)(entity.getPosition().z/Terrain.SIZE)).addEntity(entity);
							
						}
					}
				}
			}
		}
	}
	public void onEntityTerrainChange(int currentX, int currentZ, int newX, int newZ, Entity entity) {
		Terrain terrain = get(currentX, currentZ);
		terrain.remove(entity);
		Terrain newTerrain = get(newX, newZ);
		newTerrain.addEntity(entity);
	}
	public List<Terrain> getAll() {
		List<Terrain> terrains = new ArrayList<>();
		for (List<Terrain> terrain : this.terrains) {
			terrains.addAll(terrain);
		}
		return terrains;
	}
}
