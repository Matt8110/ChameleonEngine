#version 330 core

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gDiffuse;
layout (location = 3) out vec4 gSpecular;

in vec2 texCoordPass;
in vec3 normalPass;
in vec3 vertexPass;
in vec3 tangentPass;
in vec2 poissonDisk[6];
in vec3 camPos;
//in vec3 reflectedVector;

in mat3 TSpace;
in vec4 depthMat;

uniform sampler2D mainTexture;
uniform sampler2D secondTexture;
uniform sampler2D shadowMap;
uniform sampler2D normalMap;
uniform sampler2D specularMap;
uniform samplerCube cubeMap;

uniform float secondTextureStrength;
uniform vec3 diffColor, ambientColor;
uniform bool secondTextureEnable;
uniform vec3 directionalLightDirection;

uniform bool normalMapEnable;
uniform bool cubeMapEnable;
uniform float cubeMapStrength;

uniform bool specularEnable;
uniform bool specularMapEnable;
uniform float specularStrength;
uniform float specularDampening;
uniform bool shadowEnable;
uniform bool parallaxCorrected;
uniform vec3 cubePosition;
uniform vec3 cubeSize;
uniform vec3 diffuseColor;

float secondTextureStrengthFinal;

//Function declarations
void directionalShadowCalculation();
vec3 parallaxCorrect( vec3 refleced, vec3 cubeSize2, vec3 cubePos );
float shadowBrightness;

void main()
{
	
	if (shadowEnable)
	{
		shadowBrightness = 0.0;
		directionalShadowCalculation();
	}
	else
		shadowBrightness = 1.0;

	gPosition = vec4(vertexPass, 1.0);
	
	vec4 specularMapTex = texture(specularMap, texCoordPass);
	
	if (normalMapEnable)
	{
		vec3 normalMapValue = normalize( TSpace * (2.0 * texture(normalMap, texCoordPass).xyz - 1.0));
		gNormal = vec4(normalMapValue, 1.0);
	}else
	{
		gNormal = vec4(normalize(normalPass), 1.0);
	}
	
	gDiffuse = texture(mainTexture, texCoordPass);
	
	if (cubeMapEnable)
	{
		vec3 viewVector = normalize(-camPos);
		vec3 reflectedVector = reflect(viewVector, normalize(gNormal.xyz));
		
		if (parallaxCorrected)
		{
			gDiffuse.xyz = mix(gDiffuse.xyz, texture(cubeMap, parallaxCorrect(reflectedVector, cubeSize, cubePosition)).xyz, clamp(1.0 - dot(-viewVector, gNormal.xyz), cubeMapStrength/25, cubeMapStrength));
		}
		else
			gDiffuse.xyz = mix(gDiffuse.xyz, texture(cubeMap, reflectedVector).xyz, cubeMapStrength);
	}
	
	gDiffuse *= shadowBrightness;
	
	if (gDiffuse.a < 0.99)
		discard;
	
	gDiffuse.rgb *= diffuseColor;
	
	//Calculating the specular strength and specular map into the alpha channel of the diffuse
	
	float finalSpecularPower = 0.0;
	
	if (specularEnable)
	{
		finalSpecularPower = specularStrength;
		
		if (specularMapEnable)
		{
			finalSpecularPower *= specularMapTex.r;
		}
		
	}
	
	gSpecular = vec4(finalSpecularPower, specularDampening, 1.0, 1.0);
}


vec3 parallaxCorrect( vec3 reflected, vec3 cubeSize2, vec3 cubePos ) {

    vec3 nDir = normalize(reflected);
    vec3 rbmax = (   .5 * ( cubeSize2 - cubePos ) - vertexPass ) / nDir;
    vec3 rbmin = ( - .5 * ( cubeSize2 - cubePos ) - vertexPass ) / nDir;

    vec3 rbminmax;
    rbminmax.x = ( nDir.x > 0. )?rbmax.x:rbmin.x;
    rbminmax.y = ( nDir.y > 0. )?rbmax.y:rbmin.y;
    rbminmax.z = ( nDir.z > 0. )?rbmax.z:rbmin.z;

    float correction = min(min(rbminmax.x, rbminmax.y), rbminmax.z);
    vec3 boxIntersection = vertexPass + nDir * correction;

    return boxIntersection - cubePos;
}

void directionalShadowCalculation()
{
	vec3 shadowCoords = depthMat.xyz;
	float cosTheta = dot(normalize(normalPass), normalize(-directionalLightDirection));
	float bias = 0.001*tan(acos(cosTheta));
	
	vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
	
	int pIndex = 0;
	
	for(int x = -1; x <= 1; ++x)
		for(int y = -1; y <= 1; ++y)
		{
		
			pIndex += 2;
			if (pIndex > 6)
				pIndex = 0;
		
			for (int i=0;i<2;i++){
				float pcfDepth = texture(shadowMap, shadowCoords.xy + poissonDisk[pIndex + i]/1000.0 + vec2(x, y) * texelSize).r; 
				shadowBrightness += shadowCoords.z - bias > pcfDepth ? 1.0 : 0.0;      
  
			}
		}
	
	if (shadowCoords.z > 1.0 || shadowCoords.z < 0.0)
		shadowBrightness = 0.0;
	
	shadowBrightness /= 40.0;
	shadowBrightness = 1.0 - shadowBrightness;

}