package matt8110.mattengine.terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Camera;
import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.geometry.VAO;
import matt8110.mattengine.scenegraph.Renderable;
import net.jlibnoise.generator.Perlin;

public class Terrain extends Renderable{

	private float[][] terrainData;
	public int size;
	public float tileSize;
	public float maxHeight, maxHeight2, maxHeight3;
	private int texture1;
	private Perlin p = new Perlin();
	private float divider = 100, divider2, divider3;
	private float tilePositionX, tilePositionZ;
	private float drawDistance = 1;
	public boolean needsDeleted = false;
	private float[] vertices, normals, texCoords;
	private float div1 = 0.3f, div2 = 0.10f;
	private boolean needsRebuilt = false;
	
	public Terrain(float x, float z, float tileSize, int size, float maxHeight, float maxHeight2, float maxHeight3, int texture1, float divider, float divider2, float divider3, int drawDistance, int seed) {
		
		p.setSeed(seed);
		
		tilePositionX = x / (tileSize * size);
		tilePositionZ = z / (tileSize * size);
		
		position.x = x;
		position.z = z;
		
		this.divider = divider;
		this.divider2 = divider2;
		this.divider3 = divider3;
		this.drawDistance = drawDistance;
		
		this.texture1 = texture1;
		material.setMainTexture(this.texture1);
		this.size = size;
		this.tileSize = tileSize;
		this.maxHeight = maxHeight;
		this.maxHeight2 = maxHeight2;
		this.maxHeight3 = maxHeight3;
		
		material.setTerrain(true);
		
		material.setTexCoordTiling(10, 10);
		
		terrainData = new float[this.size+3][this.size+3];
		//loadImageData(heightmap);
		
	}
	
	public void setHeight(int x, int y, float height) {
		terrainData[x][y] = height;
		needsRebuilt = true;
	}
	
	public void addHeight(int x, int y, float height) {
		cleanup();
		terrainData[x][y] += height;
		needsRebuilt = true;
	}
	
	public void generateTerrain() {
		
		generateRandomData();
		generateTerrainAndStore();
		
	}
	
	public void setTerrainBlendSettings(float div1, float div2, float maxHeight2, float maxHeight3) {
		
		this.div1 = div1;
		this.div2 = div2;
		this.maxHeight2 = maxHeight2;
		this.maxHeight3 = maxHeight3;
		
	}
	
	public void update() {
		
		Camera camera = Window.getCurrentCamera();
		
		int cameraGridX = (int)Math.floor(camera.getX() / (size * tileSize));
		int cameraGridZ = (int)Math.floor(camera.getZ() / (size * tileSize));
		
		if (cameraGridX < tilePositionX - drawDistance ||
			cameraGridX > tilePositionX + drawDistance ||
			cameraGridZ < tilePositionZ - drawDistance ||
			cameraGridZ > tilePositionZ + drawDistance) {
			
			cleanup();
			needsDeleted = true;
			
		}
		
		if (needsRebuilt) {
			//cleanup();
			generateTerrainAndStore();
			sendToOpenGL();
			System.out.println("Done");
			needsRebuilt = false;
		}
		
	}
	
	public void setTextureTiling(int x, int y) {
		
		material.setTexCoordTiling(x, y);
		
	}
	
	public void cleanup() {
		vao.cleanup();
	}
	
	public void generateRandomData() {
		
		
		
		for (int z = 0; z < size+3; z++)
			for (int x = 0; x < size+3; x++) {
				
				float xVal = (x + position.x/tileSize) / divider;
				float zVal = (z + position.z/tileSize) / divider;
				
				//Overall shape
				float val1 = (float) p.getValue(xVal, zVal, 0.1f)*maxHeight;
				
				xVal = (x + position.x/tileSize) / divider2;
				zVal = (z + position.z/tileSize) / divider2;
				float val2 = (float) p.getValue(xVal, zVal, 0.1f)*maxHeight2;
				
				xVal = (x + position.x/tileSize) / divider3;
				zVal = (z + position.z/tileSize) / divider3;
				float val3 = (float) p.getValue(xVal, zVal, 0.1f)*maxHeight3;
				
				terrainData[x][z] = Utils.lerp(val1, val2, div1);
				terrainData[x][z] = Utils.lerp(terrainData[x][z], val3, div2);
			}
		
	}
	
	public void setTextures(String terrainMap, String texture2, String texture3, String texture4) {
		
		material.terrainMap = Utils.loadTexture(terrainMap);
		if (texture2 != null)
		material.secondTexture = Utils.loadTexture(texture2);
		if (texture3 != null)
		material.thirdTexture = Utils.loadTexture(texture3);
		if (texture4 != null)
		material.fourthTexture = Utils.loadTexture(texture4);
		
	}
	
	private Vector3f getNormal(int x, int z){
		
		float hL;
		float hR;
		float hD;
		float hU;
		

			hL = terrainData[x-1][z];
			hD = terrainData[x][z-1];
			hR = terrainData[x+1][z];
			hU = terrainData[x][z+1];
		
		//hL = terrainData[x][z];
		//hD = terrainData[x][z];
		
		
		Vector3f n = new Vector3f(hL-hR, 2f, hD-hU);
		
		//if (x > size-1 || x < 1 || z > size-1 || z < 1){
		//	n.x = 0;
		//	n.z = 0;
		//}
		
		n.normalise();
		
		return n;
		
	}
	
	private void loadImageData(String heightmap) {
		
		try {
			BufferedImage img = ImageIO.read(new File(heightmap));
			
			float incX = img.getWidth() / size;
			float incY = img.getHeight() / size;
			
			for (int x = 0; x < size; x++) 
				for (int y = 0; y < size; y++) {
					terrainData[x][y] = (img.getRGB((int)incX*x, (int)incY*y) & 0x000000FF) / (255 / maxHeight);
				}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void generateTerrainAndStore() {
		
		vertices = new float[size*size*18];
		texCoords = new float[size*size*18];
		normals = new float[size*size*6*3];
		
		int vertexCounter = 0;
		int textureCounter = 0;
		
		for (int x = 1; x < size+1; x++)
			for (int y = 1; y < size+1; y++) {
				
				float tileX = tileSize * x;
				float tileY = tileSize * y; 
				
				float texIncX = 1.0f / (size+1);
				float texIncY = 1.0f / (size+1);
				
				Vector3f normal = getNormal(x, y);
				
				vertices[vertexCounter] = tileX;
				vertices[vertexCounter+1] = terrainData[x][y];
				vertices[vertexCounter+2] = tileY;
				
				normals[vertexCounter] = normal.x;
				normals[vertexCounter+1] = normal.y;
				normals[vertexCounter+2] = normal.z;
				
				texCoords[textureCounter] = texIncX * x;
				texCoords[textureCounter+1] = texIncY * y;
				
				normal = getNormal(x, y+1);
				
				vertices[vertexCounter+3] = tileX;
				vertices[vertexCounter+4] = terrainData[x][y+1];
				vertices[vertexCounter+5] = tileY + tileSize;
				
				normals[vertexCounter+3] = normal.x;
				normals[vertexCounter+4] = normal.y;
				normals[vertexCounter+5] = normal.z;
				
				texCoords[textureCounter+2] = texIncX * x;
				texCoords[textureCounter+3] = texIncY * y + texIncY;
				
				normal = getNormal(x+1, y);
				
				vertices[vertexCounter+6] = tileX + tileSize;
				vertices[vertexCounter+7] = terrainData[x+1][y];
				vertices[vertexCounter+8] = tileY;
				
				normals[vertexCounter+6] = normal.x;
				normals[vertexCounter+7] = normal.y;
				normals[vertexCounter+8] = normal.z;
				
				texCoords[textureCounter+4] = texIncX * x + texIncX;
				texCoords[textureCounter+5] = texIncY * y;
				
				
				normal = getNormal(x+1, y);
				
				vertices[vertexCounter+9] = tileX + tileSize;
				vertices[vertexCounter+10] = terrainData[x+1][y];
				vertices[vertexCounter+11] = tileY;
				
				normals[vertexCounter+9] = normal.x;
				normals[vertexCounter+10] = normal.y;
				normals[vertexCounter+11] = normal.z;
				
				texCoords[textureCounter+6] = texIncX * x + texIncX;
				texCoords[textureCounter+7] = texIncY * y;
				
				normal = getNormal(x, y+1);
				
				vertices[vertexCounter+12] = tileX;
				vertices[vertexCounter+13] = terrainData[x][y+1];
				vertices[vertexCounter+14] = tileY + tileSize;
				
				normals[vertexCounter+12] = normal.x;
				normals[vertexCounter+13] = normal.y;
				normals[vertexCounter+14] = normal.z;
				
				texCoords[textureCounter+8] = texIncX * x;
				texCoords[textureCounter+9] = texIncY * y + texIncY;
				
				normal = getNormal(x+1, y+1);
				
				vertices[vertexCounter+15] = tileX + tileSize;
				vertices[vertexCounter+16] = terrainData[x+1][y+1];
				vertices[vertexCounter+17] = tileY + tileSize;
				
				normals[vertexCounter+15] = normal.x;
				normals[vertexCounter+16] = normal.y;
				normals[vertexCounter+17] = normal.z;
				
				texCoords[textureCounter+10] = texIncX * x + texIncX;
				texCoords[textureCounter+11] = texIncY * y + texIncY;
				
				vertexCounter += 18;
				textureCounter += 12;
				
			}
		
	}
	
	public void sendToOpenGL() {
		
		vao = new VAO(vertices, normals, texCoords, true);
	}
	
	public float getHeight(float x, float z){
		
		float newX = x-position.x;
		float newZ = z-position.z;
		
		float gridSquare = (size+3)/ (float) (terrainData.length-1);
		
		int gridX2 = (int) Math.floor(newX / tileSize);
		int gridZ2 = (int) Math.floor(newZ / tileSize);
		
		float xCoord = (newX % tileSize)/tileSize;
		float zCoord = (newZ % tileSize)/tileSize;
		if (gridX2 >= terrainData.length-1 || gridZ2 >= terrainData.length-1 || gridX2 < 0 || gridZ2 < 0){
			
			return 0;
		}
		
		float answer;
		
		if (xCoord <= (1-zCoord)) {
			answer = barryCentric(new Vector3f(0, terrainData[gridX2][gridZ2], 0), new Vector3f(1,
					terrainData[gridX2 + 1][gridZ2], 0), new Vector3f(0,
							terrainData[gridX2][gridZ2 + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = barryCentric(new Vector3f(1, terrainData[gridX2 + 1][gridZ2], 0), new Vector3f(1,
					terrainData[gridX2 + 1][gridZ2 + 1], 1), new Vector3f(0,
							terrainData[gridX2][gridZ2 + 1], 1), new Vector2f(xCoord, zCoord));
		}
		
		return answer;
		
	}
	
	public float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
}
