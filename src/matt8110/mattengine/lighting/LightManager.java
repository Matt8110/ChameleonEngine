package matt8110.mattengine.lighting;

import java.util.ArrayList;
import java.util.List;

public class LightManager {

	public final static int MAX_LIGHTS = 100;
	public static List<PointLight> pointLights = new ArrayList<PointLight>();
	public static float[] _lightPosition = new float[MAX_LIGHTS * 3];
	public static float[] _lightColor = new float[MAX_LIGHTS * 3];
	public static float[] _lightRange = new float[MAX_LIGHTS];
	
	public static void addLight(PointLight light) {
		light._InternalID = pointLights.size();
		pointLights.add(light);
	}
	
	public static void removeLight(PointLight light) {
		pointLights.remove(light._InternalID);
		setLightData(light._InternalID, 0, 0, 0, 0, 0, 0, 0);
	}
	
	public static void renderShadowMaps() {
		
		for (int i = 0; i < pointLights.size(); i++) {
			
			if (pointLights.get(i)._shadowsEnabled) {
				pointLights.get(i)._renderShadowMap();
			}
			
		}
		
	}
	
	public static void updateLights() {
		
		for (int i = 0; i < pointLights.size(); i++) {
			
			pointLights.get(i)._InternalID = i;
			setLightData(i, pointLights.get(i).position.x, pointLights.get(i).position.y, pointLights.get(i).position.z,
					pointLights.get(i).color.x, pointLights.get(i).color.y, pointLights.get(i).color.z, pointLights.get(i).range);
		}
		
	}
	
	private static void setLightData(int id, float x, float y, float z, float r, float g, float b, float range) {
		
		_lightPosition[id * 3] = x;
		_lightPosition[id * 3 + 1] = y;
		_lightPosition[id * 3 + 2] = z;
		_lightColor[id * 3] = r;
		_lightColor[id * 3 + 1] = g;
		_lightColor[id * 3 + 2] = b;
		_lightRange[id] = range;
		
	}
	
}
