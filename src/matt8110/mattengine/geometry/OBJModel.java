package matt8110.mattengine.geometry;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Utils;
import matt8110.mattengine.scenegraph.Renderable;

public class OBJModel extends Renderable{

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
	public float x1 = 0, y1 = 0, z1 = 0, x2 = 0, y2 = 0, z2 = 0;
	
	public OBJModel(String file, String texture) {
		
		material.mainTexture = Utils.loadTexture(texture);
		
		filename = file;
		//mtlFile = new MTLFile();
		
		loadData();
		sortData();
		
		vao = new VAO(vertices, normals, texCoords, true);
		
	}
	
	public OBJModel(String file, String texture, String normalMap) {
		
		material.mainTexture = Utils.loadTexture(texture);
		material.normalMap = Utils.loadTexture(normalMap);
		material.enableNormalMap(true);
		
		filename = file;
		//mtlFile = new MTLFile();
		
		loadData();
		sortData();
		
		vao = new VAO(vertices, normals, texCoords, tangents, true);
		
		vertices = null;
		normals = null;
		texCoords = null;
		tangents = null;
		
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
				}
				
			}
			
			scan.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void sortData() {
		
		vertices = new float[facesList.size()];
		normals = new float[facesList.size()];
		texCoords = new float[(facesList.size() * (3 / 2))];
		tangents = new float[facesList.size()];
		texCoordCounter = 0;
		verticesCounter = 0;
		
		/*Formatter format = null;
		try {
			format = new Formatter(new File("switchmodel.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
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
			
			//Temp for switch
			/*format.format("{ {");
			format.format(vertX * 0.1f + "f, ");
			format.format(vertY * 0.1f + "f, ");
			format.format(vertZ * 0.1f + "f}, {");
			
			format.format(texCoordsList.get((facesList.get(i+1) - 1) * 2) + "f, " + -texCoordsList.get((facesList.get(i+1) - 1) * 2 + 1) + "f}, {");
			
			format.format(normalsList.get((facesList.get(i+2) - 1) * 3) + "f, ");
			format.format(normalsList.get((facesList.get(i+2) - 1) * 3 + 1) + "f, ");
			format.format(normalsList.get((facesList.get(i+2) - 1) * 3 + 2) + "f} },\n");*/
			
			texCoordCounter += 2;
			verticesCounter += 3;
			
			createBoundingBox(vertX, vertY, vertZ);
		}
		
		//format.close();
		
		
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
		
		verticesList.clear();
		normalsList.clear();
		texCoordsList.clear();
		
	}
	
	private void createBoundingBox(float x, float y, float z) {
		
		if (x < x1)
			x1 = x;
		if (x > x2)
			x2 = x;
		if (y < y1)
			y1 = y;
		if (y > y2)
			y2 = y;
		if (z < z1)
			z1 = z;
		if (z > z2)
			z2 = z;
		
	}
	
	public boolean checkAABB(Vector3f position) {
		
		if (position.x > x1+this.position.x && position.y > y1+this.position.y && position.z > z1+this.position.z &&
			position.x < x2+this.position.x && position.y < y2+this.position.y && position.z < z2+this.position.z)
			return true;
		
		return false;
		
	}
	
	
}
