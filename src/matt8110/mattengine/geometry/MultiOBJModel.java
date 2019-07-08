package matt8110.mattengine.geometry;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.TextureManager;
import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.geometry.MTLFile;
import matt8110.mattengine.geometry.VAO;
import matt8110.mattengine.scenegraph.Renderable;
import matt8110.mattengine.shaders.ShaderType;

public class MultiOBJModel extends Renderable{

	private float[] vertices, normals, texCoords, tangents;
	private List<Float> verticesList = new ArrayList<Float>();
	private List<Float> normalsList = new ArrayList<Float>();
	private List<Float> texCoordsList = new ArrayList<Float>();
	private List<Integer> facesList = new ArrayList<Integer>();
	private MTLFile mtlFile;
	private String filename;
	private String line;
	private String[] lineSplit;
	private int texCoordCounter, verticesCounter;
	public Map<String, ObjectChunk> chunks = new HashMap<String, ObjectChunk>();
	private String chunkName = null;
	
	public MultiOBJModel(String path, String model) {
		
		//material.mainTexture = Utils.loadTexture(texture);
		
		filename = path + model;
		String[] filenameSplit = filename.split("\\.");
		
		mtlFile = new MTLFile(filenameSplit[0] + ".mtl", path);
		
		loadData();
		//sortData();
		
		vertices = null;
		normals = null;
		texCoords = null;
		tangents = null;
		
	}
	
	public ObjectChunk getChunk(String name) {
		return chunks.get(name);
	}
	
	private void loadData() {
		
		try {
			
			Scanner scan = new Scanner(new File(filename));
			
			while (scan.hasNextLine()) {
				line = scan.nextLine();
				line = line.replaceAll("/", " ");
				lineSplit = line.split(" ");
				
				switch(lineSplit[0]) {
					case "v":
						verticesList.add(Float.parseFloat(lineSplit[1]));
						verticesList.add(Float.parseFloat(lineSplit[2]));
						verticesList.add(Float.parseFloat(lineSplit[3]));
					break;
					case "vt":
						texCoordsList.add(Float.parseFloat(lineSplit[1]));
						texCoordsList.add(Float.parseFloat(lineSplit[2]));
					break;
					case "vn":
						normalsList.add(Float.parseFloat(lineSplit[1]));
						normalsList.add(Float.parseFloat(lineSplit[2]));
						normalsList.add(Float.parseFloat(lineSplit[3]));
					break;
					case "f":
						facesList.add(Integer.parseInt(lineSplit[1]));
						facesList.add(Integer.parseInt(lineSplit[2]));
						facesList.add(Integer.parseInt(lineSplit[3]));
						facesList.add(Integer.parseInt(lineSplit[4]));
						facesList.add(Integer.parseInt(lineSplit[5]));
						facesList.add(Integer.parseInt(lineSplit[6]));
						facesList.add(Integer.parseInt(lineSplit[7]));
						facesList.add(Integer.parseInt(lineSplit[8]));
						facesList.add(Integer.parseInt(lineSplit[9]));
					break;
					case "usemtl":
						if (chunkName != null) {
							
							//if ()
							ObjectChunk chunk = new ObjectChunk();
							sortData();
							
							chunk.material.setMainTexture(TextureManager.getTexture(chunkName));
							
							chunk.vao = new VAO(vertices, normals, texCoords, tangents, true);
							chunks.put(chunkName, chunk);
							
							facesList.clear();
						}
						
						chunkName = lineSplit[1];
				}
				
			}
			
			ObjectChunk chunk = new ObjectChunk();
			
			sortData();
			
			chunk.material.setMainTexture(TextureManager.getTexture(chunkName));
			
			chunk.vao = new VAO(vertices, normals, texCoords, tangents, true);
			chunks.put(chunkName, chunk);
			
			verticesList.clear();
			normalsList.clear();
			texCoordsList.clear();
			
			scan.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void render(ShaderType type) {
		
		for (ObjectChunk chunk : chunks.values()) {
		
			if (type == ShaderType.SHADOW) {
				Window.shadowShader.setTransformation(position, rotation, scale);
			}
				
			if (type == ShaderType.GBUFFER) {
				Window.gBufferShader.setTransformation(position, rotation, scale);
				chunk.material.setShaderData(Window.gBufferShader);
			}
			
			if (!chunk.material._cullingEnabled)
				GL11.glDisable(GL11.GL_CULL_FACE);
			
			GL30.glBindVertexArray(chunk.vao.getVaoID());
			
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			
			//Enable normal mapping if there is a texture
			if (chunk.material.normalMap != -1) {
				GL20.glEnableVertexAttribArray(3);
			}
			
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, chunk.vao.getVertexCount());
			
			GL11.glEnable(GL11.GL_CULL_FACE);
			
		GL30.glBindVertexArray(0);
		
		}
		
	}
	
	private void sortData() {
		
		vertices = new float[facesList.size()];
		normals = new float[facesList.size()];
		texCoords = new float[(facesList.size() * (3 / 2))];
		tangents = new float[facesList.size()];
		texCoordCounter = 0;
		verticesCounter = 0;
		
		for (int i = 0; i < facesList.size(); i += 3) {
			
			float vertX = verticesList.get((facesList.get(i) - 1) * 3);
			float vertY = verticesList.get((facesList.get(i) - 1) * 3 + 1);
			float vertZ = verticesList.get((facesList.get(i) - 1) * 3 + 2);
			
			vertices[verticesCounter] = vertX;
			vertices[verticesCounter + 1] = vertY;
			vertices[verticesCounter + 2] = vertZ;
			
			texCoords[texCoordCounter] = texCoordsList.get((facesList.get(i+1) - 1) * 2);
			texCoords[texCoordCounter + 1] = -texCoordsList.get((facesList.get(i+1) - 1) * 2 + 1);
			
			normals[verticesCounter] = normalsList.get((facesList.get(i+2) - 1) * 3);
			normals[verticesCounter + 1] = normalsList.get((facesList.get(i+2) - 1) * 3 + 1);
			normals[verticesCounter + 2] = normalsList.get((facesList.get(i+2) - 1) * 3 + 2);
			
			texCoordCounter += 2;
			verticesCounter += 3;
		}
		
		
		//Calculate tangents
		texCoordCounter = 0;
		
		for (int i = 0; i < vertices.length; i += 9){
			
			Vector3f p0 = new Vector3f(vertices[i], vertices[i+1], vertices[i+2]);
			Vector3f p1 = new Vector3f(vertices[i+3], vertices[i+4], vertices[i+5]);
			Vector3f p2 = new Vector3f(vertices[i+6], vertices[i+7], vertices[i+8]);
			
			Vector2f t0 = new Vector2f(texCoords[texCoordCounter], texCoords[texCoordCounter+1]);
			Vector2f t1 = new Vector2f(texCoords[texCoordCounter+2], texCoords[texCoordCounter+3]);
			Vector2f t2 = new Vector2f(texCoords[texCoordCounter+4], texCoords[texCoordCounter+5]);
			
			 Vector3f delatPos1 = Vector3f.sub(p1, p0, null);
		     Vector3f delatPos2 = Vector3f.sub(p2, p0, null);
		     Vector2f deltaUv1 = Vector2f.sub(t1, t0, null);
		     Vector2f deltaUv2 = Vector2f.sub(t2, t0, null);
			
		     float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
		        delatPos1.scale(deltaUv2.y);
		        delatPos2.scale(deltaUv1.y);
		        Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
		        tangent.scale(r);
			
			tangents[i] = tangent.x;
			tangents[i+1] = tangent.y;
			tangents[i+2] = tangent.z;
			
			tangents[i+3] = tangent.x;
			tangents[i+4] = tangent.y;
			tangents[i+5] = tangent.z;
			
			tangents[i+6] = tangent.x;
			tangents[i+7] = tangent.y;
			tangents[i+8] = tangent.z;
			
			texCoordCounter += 6;
		}
		
		//verticesList.clear();
		//normalsList.clear();
		//texCoordsList.clear();
		
	}
	
	
}
