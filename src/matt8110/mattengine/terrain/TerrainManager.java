package matt8110.mattengine.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.Application;
import matt8110.mattengine.Player;
import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.scenegraph.Renderer;

public class TerrainManager {

	public List<Terrain> terrainList = new CopyOnWriteArrayList<Terrain>();
	public List<Terrain> needsAdded = new CopyOnWriteArrayList<Terrain>();
	public int viewDistance = 7; //Must be odd
	private int startOffset = (int)Math.floor(viewDistance/2);
	float maxHeight, maxHeight2, maxHeight3;
	float divider, divider2, divider3;
	private int texture;
	private Thread terrainThread;
	private int seed;
	
	//Terrain selection
	private Vector3f rayStepPosition = new Vector3f(Window.currentCamera.position.x, Window.currentCamera.position.y, Window.currentCamera.position.z);
	private Vector3f rayDirection = new Vector3f();
	private Vector3f rayIntersection = new Vector3f();
	
	public TerrainManager(float maxHeight, float maxHeight2, float maxHeight3, float divider, float divider2, float divider3, String textureFile, int seed) {
		
		this.maxHeight = maxHeight;
		this.maxHeight2 = maxHeight2;
		this.maxHeight3 = maxHeight3;
		this.divider = divider;
		this.divider2 = divider2;
		this.divider3 = divider3;
		this.seed = seed;
		
		this.texture = Utils.loadTexture(textureFile);
		
		terrainThread = new Thread(new TerrainThread());
		terrainThread.start();
		
		//Generate terrain
		for (float z = -startOffset; z < viewDistance; z++)
			for (float x = -startOffset; x < viewDistance; x++) {
				
				Terrain terrain = new Terrain(x  * 64 * 4, z * 64 * 4, 4, 64, maxHeight, maxHeight2, maxHeight3, texture, divider, divider2, divider3, startOffset, seed);
				terrain.setTextureTiling(7, 7);
				
				terrain.generateTerrain();
				terrain.sendToOpenGL();
				terrainList.add(terrain);
				Renderer.rootNode.add(terrain);
			}
		
	}
	
	public void setHeight(float x, float z, float height) {
		float terrainSize = 64 * 4;
		int tileX = (int)Math.floor(x / terrainSize);
		int tileZ = (int)Math.floor(z / terrainSize);
		
		for (int i = 0; i < terrainList.size(); i++) {
			if (terrainList.get(i).getPosition().x == tileX && terrainList.get(i).getPosition().z == tileZ) {
				terrainList.get(i).addHeight(tileX, tileZ, height);
			}
				
		}
		
		
	}
	
	public void addHeight(float x, float z, float height) {
		float terrainSize = 64 * 4;
		int tileX = (int)Math.floor(x / terrainSize);
		int tileZ = (int)Math.floor(z / terrainSize);
		
		
		for (int i = 0; i < terrainList.size(); i++) {
			
			int fixedX = (int)Math.floor(terrainList.get(i).getPosition().x / terrainSize);
			int fixedZ = (int)Math.floor(terrainList.get(i).getPosition().z / terrainSize);
			
			if (fixedX == tileX && fixedZ == tileZ) {
				
			int positionX = (int)(x/4 - fixedX*66);
			int positionZ = (int)(z/4 - fixedZ*66);
				terrainList.get(i).addHeight(positionX, positionZ, height);
			}
				
		}
		
		
	}
	
	public float getHeightAt(float x, float z) {
		
		for (Terrain terrain : terrainList) {
			
			float terrainSize = 64 * 4;
			float terrainTileX = (float)Math.floor(terrain.getPosition().x / terrainSize);
			float terrainTileZ = (float)Math.floor(terrain.getPosition().z / terrainSize);
			float tileX = (float)Math.floor(x / terrainSize);
			float tileZ = (float)Math.floor(z / terrainSize);
			
			
			
			if (terrainTileX == tileX && terrainTileZ == tileZ) {
				
				return terrain.getHeight(x, z);
				
			}
			
		}
		
		return 0;
		
	}
	
	public void update() {
		
		//System.out.println(terrainList.size());
		
		for (int i = 0; i < terrainList.size(); i++) {
				
				
				terrainList.get(i).update();
				
				if (terrainList.get(i).needsDeleted) {
					Renderer.rootNode.remove(terrainList.get(i));
					terrainList.remove(i);
				}
			}
		
			//Add terrain that has been generated from other thread
			for (Terrain terrain : needsAdded) {
				
					terrain.sendToOpenGL();
					terrainList.add(terrain);
					Renderer.rootNode.add(terrain);
					needsAdded.remove(terrain);
			}
	}
	
	private void updateRayCasting() {
		
		//Ray-terrain intersection stuff
		rayDirection.x = (float)Math.cos(Math.toRadians(Window.currentCamera.getRotY()));
		rayDirection.y = (float)-Math.tan(Math.toRadians(Window.currentCamera.getRotX()));
		rayDirection.z = (float)-Math.sin(Math.toRadians(Window.currentCamera.getRotY()));
		rayDirection.normalise();
		rayDirection.x *= 0.40f;
		rayDirection.y *= 0.40f;
		rayDirection.z *= 0.40f;
		
		rayStepPosition.x = Window.currentCamera.position.x;
		rayStepPosition.y = Window.currentCamera.position.y;
		rayStepPosition.z = Window.currentCamera.position.z;
		
		for (int i = 0; i < 400; i += 1) {
			
			rayStepPosition = Vector3f.add(rayStepPosition, rayDirection, null);
			
			//System.out.println(rayStepPosition);
			
			float terrainHeight = getHeightAt(rayStepPosition.x, rayStepPosition.z);
			
			if (rayStepPosition.y < terrainHeight) {
				rayStepPosition.y = terrainHeight;
				
				rayIntersection.x = rayStepPosition.x;
				rayIntersection.y = rayStepPosition.y;
				rayIntersection.z = rayStepPosition.z;
				
				break;
			}
		}
		
	}
	
	public Vector3f getRayIntersectionPosition() {
		return rayIntersection;
	}
	
	public class TerrainThread implements Runnable {

		private int lastPlayerGridX, lastPlayerGridY, startOffset2;
		
		@Override
		public void run() {
			
			lastPlayerGridX = Player.gridX;
			lastPlayerGridY = Player.gridY;
			startOffset2 = startOffset;
			
			while (Window.running.get()) {
				
				updateRayCasting();
				
				if (Player.gridX > lastPlayerGridX) {
					for (float z = Player.gridY-startOffset2; z < Player.gridY-startOffset2+viewDistance; z++) {
						Terrain terrain = new Terrain((Player.gridX + startOffset2) * 64 * 4, z * 64 * 4, 4, 64, maxHeight, maxHeight2, maxHeight3, texture, divider, divider2, divider3, startOffset2, seed);
						terrain.generateTerrain();
						needsAdded.add(terrain);
					}
					
					lastPlayerGridX = Player.gridX;
				}
				
				if (Player.gridX < lastPlayerGridX) {
					for (float z = Player.gridY-startOffset2; z < Player.gridY-startOffset2+viewDistance; z++) {
						Terrain terrain = new Terrain((Player.gridX - startOffset2) * 64 * 4, z * 64 * 4, 4, 64, maxHeight, maxHeight2, maxHeight3, texture, divider, divider2, divider3, startOffset2, seed);
						terrain.generateTerrain();
						needsAdded.add(terrain);
					}
					
					lastPlayerGridX = Player.gridX;
				}
				
				if (Player.gridY > lastPlayerGridY) {
					for (float x = Player.gridX-startOffset2; x < Player.gridX-startOffset2+viewDistance; x++) {
						Terrain terrain = new Terrain(x * 64 * 4, (Player.gridY + startOffset2) * 64 * 4, 4, 64, maxHeight, maxHeight2, maxHeight3, texture, divider, divider2, divider3, startOffset2, seed);
						terrain.generateTerrain();
						needsAdded.add(terrain);
					}
					lastPlayerGridY = Player.gridY;
				}
				
				if (Player.gridY < lastPlayerGridY) {
					for (float x = Player.gridX-startOffset2; x < Player.gridX-startOffset2+viewDistance; x++) {
						Terrain terrain = new Terrain(x * 64 * 4, (Player.gridY - startOffset2) * 64 * 4, 4, 64, maxHeight, maxHeight2, maxHeight3, texture, divider, divider2, divider3, startOffset2, seed);
						terrain.generateTerrain();
						needsAdded.add(terrain);
					}
					lastPlayerGridY = Player.gridY;
				}
				
			}
			
		}
		
		
		
	}
	
}
