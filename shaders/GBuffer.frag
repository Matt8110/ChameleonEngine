#version 330 core

layout (location = 0) out vec4 gDiffuse;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gPosition;
layout (location = 3) out vec4 gSpecular;
layout (location = 4) out vec4 gBloomMap;

in vec2 texCoordPass;
in vec2 untouchedTexCoord;
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
uniform sampler2D thirdTexture;
uniform sampler2D fourthTexture;
uniform sampler2D terrainMap;
uniform sampler2D shadowMap;
uniform sampler2D normalMap;
uniform sampler2D specularMap;
uniform samplerCube cubeMap;

uniform float secondTextureStrength;
uniform vec3 diffColor, ambientColor;
uniform bool secondTextureEnable;
uniform vec3 directionalLightDirection;
uniform bool isTerrain;

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
vec3 parallaxCorrect( vec3 v, vec3 cubeSize2, vec3 cubePos );
float shadowBrightness;

void main()
{
	
	vec4 secondTextureColor = texture(secondTexture, texCoordPass);
	vec4 thirdTextureColor = texture(thirdTexture, texCoordPass);
	vec4 fourthTextureColor = texture(fourthTexture, texCoordPass);
	vec4 terrainMapColor = texture(terrainMap, untouchedTexCoord);
	
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
	
	if (isTerrain)
	{
		gDiffuse.xyz = mix(gDiffuse.xyz, secondTextureColor.rgb, terrainMapColor.r);
		gDiffuse.xyz = mix(gDiffuse.xyz, thirdTextureColor.rgb, terrainMapColor.g);
		gDiffuse.xyz = mix(gDiffuse.xyz, fourthTextureColor.rgb, terrainMapColor.b);
		gDiffuse.a = 1.0;
	}
	
	if (cubeMapEnable)
	{
		vec3 viewVector = normalize(-camPos);
		vec3 reflectedVector = reflect(viewVector, normalize(gNormal.xyz));
		
		if (parallaxCorrected)
		{
			gDiffuse.xyz = mix(gDiffuse.xyz, texture(cubeMap, parallaxCorrect(reflectedVector, cubeSize, cubePosition)).xyz, clamp(1.0 - dot(-viewVector, gNormal.xyz), cubeMapStrength/25, cubeMapStrength));
			//gDiffuse.xyz = texture(cubeMap, parallaxCorrect(reflectedVector, cubeSize, cubePosition)).xyz;
		}
		else
			gDiffuse.xyz = mix(gDiffuse.xyz, texture(cubeMap, reflectedVector).xyz, cubeMapStrength);
	}
	
	gDiffuse.rgb *= shadowBrightness;
	
	if (gDiffuse.a < 0.99)
		discard;
	
	gDiffuse.rgb *= diffuseColor;
	
	
	//Outputting brightness for bloom
	float brightness = dot(gDiffuse.rgb, vec3(0.2126, 0.7152, 0.0722));
    if(brightness > 1.0)
        gBloomMap = vec4(gDiffuse.rgb, 1.0);
    else
        gBloomMap = vec4(0.0, 0.0, 0.0, 1.0);
	
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


vec3 parallaxCorrect( vec3 v, vec3 cubeSize2, vec3 cubePos ) {

    vec3 nDir = normalize(v);
    //vec3 rbmax = ( ( cubePos + cubeSize2/2 ) - vertexPass ) / nDir;
    //vec3 rbmin = ( (cubePos - cubeSize2/2) - vertexPass ) / nDir;
	vec3 rbmax = ( .5 * ( cubeSize2 - cubePos ) - vertexPass ) / nDir;
    vec3 rbmin = ( - .5 * (cubeSize2 - cubePos) - vertexPass ) / nDir;

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
	float bias = 0.005;
	
	vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
	
	int pIndex = 0;
	
	/*for(int x = -1; x <= 1; ++x)
		for(int y = -1; y <= 1; ++y)
		{
		
			pIndex += 2;
			if (pIndex > 6)
				pIndex = 0;
		
			for (int i=0;i<2;i++){
				float pcfDepth = texture(shadowMap, shadowCoords.xy + poissonDisk[pIndex + i]/1000.0 + vec2(x, y) * texelSize).r; 
				shadowBrightness += shadowCoords.z - bias > pcfDepth ? 1.0 : 0.0;      
  
			}
		}*/
		
		if (shadowCoords.x > 1.0 || shadowCoords.y > 1.0 || shadowCoords.x < 0.0 || shadowCoords.y < 0.0)
		shadowBrightness = 1.0;
		else if (texture(shadowMap, shadowCoords.xy).z < shadowCoords.z-bias){
			shadowBrightness += 15;
		}
	
	
	
	shadowBrightness /= 40.0;
	shadowBrightness = 1.0 - shadowBrightness;

}