package matt8110.mattengine.planets;

import matt8110.mattengine.terrain.TerrainManager;

public class Rocky extends Planet{
	
	@Override
	public void create() {
		
		//MaxHeight1, MaxHeight2, MaxHeight3, Divider1, Divider2, Divider3
		terrainManager = new TerrainManager(35, 20, 0, 300, 70, 10, "dirt.png", 19681985);
		
	}

	@Override
	public void update() {
		
		terrainManager.update();
		
	}

	
	
}
