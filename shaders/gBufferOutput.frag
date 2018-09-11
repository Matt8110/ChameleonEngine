#version 330 core

in vec2 texCoordPass;
out vec4 colour;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gDiffuse;
uniform sampler2D gSpecular;
uniform sampler2D specularMap;

const int MAX_LIGHTS = 100;
const float lightCutoff = 20;
uniform vec3 pointLightPosition[MAX_LIGHTS];
uniform vec3 pointLightColor[MAX_LIGHTS];
uniform float pointLightRange[MAX_LIGHTS];
uniform float numberOfPointLights;
uniform float exposure;
uniform float gamma;

uniform vec3 directionalLightDirection;
uniform vec3 directionalLightColor;
uniform vec3 ambientColor;
uniform vec3 cameraPosition;

uniform bool cellShadingEnable;
uniform bool directionalLightEnable;

vec3 position, tangent, normal;
vec4 diffuse;
vec2 specular;

mat3 TSpace;

const float cellLevels = 3.0;

vec3 finalBrightness = vec3(0.0);

//Function declarations
vec3 calculateDirectionalLight();
float calculateSpecular(vec3 lightDirection);
vec3 calculatePointLights();

void main()
{
	position = texture(gPosition, texCoordPass).xyz;
	normal = texture(gNormal, texCoordPass).xyz;
	diffuse = texture(gDiffuse, texCoordPass);
	specular = texture(gSpecular, texCoordPass).xy;
	
	//Adjusting exposure for HDR
	diffuse.rgb = vec3(1.0) - exp(-diffuse.rgb * exposure);
	
	//Gamma correction for diffuse
	diffuse.rgb = pow(diffuse.rgb, vec3(gamma));
	
	int x = 0;
	
	
	finalBrightness += calculateDirectionalLight();
	finalBrightness += calculatePointLights();
	
	finalBrightness = max(finalBrightness, ambientColor);
	
	//May cause issues, but is so the skybox can render
	if (diffuse.r == 0 && diffuse.g == 0 && diffuse.b == 0)
		discard;
		
		if (cellShadingEnable)
	{
		//float cellShadeLevel = floor(finalBrightness.r * cellLevels);
		//finalBrightness = vec3(cellShadeLevel / cellLevels);
	}
	
	colour = vec4(diffuse.rgb, 1.0) * vec4(finalBrightness, 1.0);
	
	//Gamma correction
	colour.rgb = pow(colour.rgb, vec3(1.0/gamma));
}

vec3 calculateDirectionalLight()
{
	if (directionalLightEnable)
		return vec3(dot(normal, -directionalLightDirection) + calculateSpecular(-directionalLightDirection))*directionalLightColor;
	else
		return vec3(0.0);
}

vec3 calculatePointLights()
{
	vec3 finalLightColour = vec3(0.0);
	
	for (int i = 0; i < numberOfPointLights; i++)
	{
			float dist = length(pointLightPosition[i] - position);
			float range = pointLightRange[i];
			
			if (dist < range*lightCutoff)
			{
				vec3 dir = normalize(pointLightPosition[i] - position);
				float rangeCalc = 1.0 / (1.0 + (2.0 / range) * dist + (1.0 / (range * range)) * (dist * dist));
				finalLightColour += (rangeCalc * dot(dir, normal)  +  rangeCalc * calculateSpecular(dir)) * pointLightColor[i];
				
			}
	}
	
	return finalLightColour;
}

float calculateSpecular(vec3 lightDirection){
	
		float specMap = 1;
		float specularDampening = specular.y;
		float specularPower = specular.x;
	
		vec3 unitToCamera = normalize(-cameraPosition + position);
		vec3 unitToLight = normalize(lightDirection);
		vec3 lightReflect = reflect(unitToLight, normal);
		
		float specularFactor = dot(lightReflect, unitToCamera);
		specularFactor = max(specularFactor, 0.0);
		
		float dampFactor = pow(specularFactor, specularDampening);
		
		return (dampFactor * specularPower);

}