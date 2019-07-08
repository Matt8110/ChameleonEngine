package matt8110.mattengine.planets;

import matt8110.mattengine.terrain.TerrainManager;

public abstract class Planet {

	public TerrainManager terrainManager;
	
	public Planet() {
		create();
	}
	protected abstract void create();
	public abstract void update();
	
}
