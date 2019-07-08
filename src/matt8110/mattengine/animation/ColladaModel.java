package matt8110.mattengine.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.geometry.VAO;
import matt8110.mattengine.scenegraph.Renderable;
import matt8110.mattengine.shaders.Material;
import matt8110.mattengine.shaders.ShaderType;

public class ColladaModel extends Renderable{

	private List<Float> vertices = new ArrayList<Float>();
	private List<Float> normals = new ArrayList<Float>();
	private List<Float> texCoords = new ArrayList<Float>();
	private List<Integer> faces = new ArrayList<Integer>();
	
	private float[] verticesFinal, normalsFinal, texCoordsFinal, tangents;
	
	private int vertexCount = 0;
	
	public ColladaModel(String model, String texture) {
		
		material.setMainTexture(Utils.loadTexture(texture));
		
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(model);
			
			Element geometries = (Element)doc.getElementsByTagName("library_geometries").item(0);
			
			NodeList sources = geometries.getElementsByTagName("source");
			Element indices = (Element) geometries.getElementsByTagName("polylist").item(0);
			indices = (Element)indices.getElementsByTagName("p").item(0);
			
			
			Element data[] = new Element[sources.getLength()];
			
			for (int i = 0; i < sources.getLength(); i++) {
				data[i] = (Element)sources.item(i);
				Element dat = (Element) data[i].getElementsByTagName("float_array").item(0);
				
				String rawData = dat.getTextContent();
				
				Scanner scan = new Scanner(rawData);
				
				if (dat.getAttribute("id").contains("positions")) {
					
					while (scan.hasNext()) {
						vertices.add(scan.nextFloat());
					}
					
					scan.close();
					
				}
				
				if (dat.getAttribute("id").contains("normals")) {
					
					while (scan.hasNext()) {
						normals.add(scan.nextFloat());
					}
					
					scan.close();
					
				}
				
				if (dat.getAttribute("id").contains("map")) {
					
					while (scan.hasNext()) {
						texCoords.add(scan.nextFloat());
					}
					
					scan.close();
					
				}
				
			}
			
			
			Scanner scan = new Scanner(indices.getTextContent());
			
			while (scan.hasNext()) {
				
				faces.add(Integer.parseInt(scan.next()));

				
			}
			
			scan.close();
			
			
			
			vertexCount = (int) (faces.size()/4f);
			
			sortThings();
			
			vao = new VAO(verticesFinal, normalsFinal, texCoordsFinal, tangents, false);
			
			vertices.clear();
			normals.clear();
			texCoords.clear();
			tangents = null;
			verticesFinal = null;
			normalsFinal = null;
			texCoordsFinal = null;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void render(ShaderType type) {
		
		//Window.mainShader.setTransformation(position, rotation, scale);
		if (type == ShaderType.SHADOW) {
			Window.shadowShader.setTransformation(position, rotation, scale);
		}
			
		if (type == ShaderType.GBUFFER) {
			Window.gBufferShader.setTransformation(position, rotation, scale);
			material.setShaderData(Window.gBufferShader);
		}
		
		
		if (type != ShaderType.SHADOW || material.getCanCastShadows()) {
		
			if (!material._cullingEnabled)
				GL11.glDisable(GL11.GL_CULL_FACE);
			
			GL30.glBindVertexArray(vao.getVaoID());
			
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			
			//Enable normal mapping if there is a texture
			if (material.normalMap != -1) {
				GL20.glEnableVertexAttribArray(3);
			}
			
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
			
			GL30.glBindVertexArray(0);
			
			GL11.glEnable(GL11.GL_CULL_FACE);
		
		}
		
	}
	
	
	public int getVaoID() {
		
		return vao.getVaoID();
		
	}
	
	
	
	
	private void sortThings(){
		
		int counter = 0;
		
		
		float size = (float)faces.size()*(3f/4f);
		float twoThirds = 2f/3f;
		
		normalsFinal = new float[(int)size];
		texCoordsFinal = new float[(int)(Math.ceil(size*twoThirds))];
		verticesFinal = new float[(int)size];
		tangents = new float[(int)size];
		
		int vertCounter = 0;
		
		for (int i = 0; i < faces.size(); i+=4){
			
			int num = faces.get(i);
			int normalNum = faces.get(i+1);
			int texNum = faces.get(i+2);
			
			
			verticesFinal[vertCounter] = vertices.get(num*3);
			verticesFinal[vertCounter+1] = vertices.get(num*3+1);
			verticesFinal[vertCounter+2] = vertices.get(num*3+2);
			
			normalsFinal[vertCounter] = normals.get(normalNum*3);
			normalsFinal[vertCounter+1] = normals.get(normalNum*3+1);
			normalsFinal[vertCounter+2] = normals.get(normalNum*3+2);
			
			texCoordsFinal[counter] = texCoords.get(texNum*2);
			texCoordsFinal[counter+1] = -texCoords.get(texNum*2+1);
			
			
			counter += 2;
			vertCounter += 3;
			
		}
		
		counter = 0;
		
		for (int i = 0; i < verticesFinal.length; i += 9){
			
			Vector3f p0 = new Vector3f(verticesFinal[i], verticesFinal[i+1], verticesFinal[i+2]);
			Vector3f p1 = new Vector3f(verticesFinal[i+3], verticesFinal[i+4], verticesFinal[i+5]);
			Vector3f p2 = new Vector3f(verticesFinal[i+6], verticesFinal[i+7], verticesFinal[i+8]);
			
			Vector2f t0 = new Vector2f(texCoordsFinal[counter], texCoordsFinal[counter+1]);
			Vector2f t1 = new Vector2f(texCoordsFinal[counter+2], texCoordsFinal[counter+3]);
			Vector2f t2 = new Vector2f(texCoordsFinal[counter+4], texCoordsFinal[counter+5]);
			
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
			
			counter += 6;
		}
		
	}


	public Material getMaterial() {
		return material;
	}


	public void setMaterial(Material material) {
		this.material = material;
	}
	
	
	
	
}
