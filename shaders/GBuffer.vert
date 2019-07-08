#version 330 core

layout (location = 0) in vec3 vertex_modelSpace;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in vec3 tangent;
layout (location = 4) in ivec3 joints;
layout (location = 5) in ivec3 weights;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 shadowProjectionMatrix;
uniform mat4 shadowViewMatrix;
uniform mat4 shadowBias;
uniform vec3 cameraPos;

uniform vec2 texCoordTile;

uniform bool normalMapEnable;

out vec2 texCoordPass;
out vec2 untouchedTexCoord;
out vec3 normalPass;
out vec3 vertexPass;
out vec3 tangentPass;
out vec4 depthMat;
out vec3 camPos;
//out vec3 reflectedVector;

out mat3 TSpace;

void calculateTangents();

out vec2 poissonDisk[6];

void main()
{
	vec4 worldPos = transformationMatrix * vec4(vertex_modelSpace, 1.0);
	
	//camPos = cameraPos;
	camPos = (inverse(viewMatrix) * vec4(0, 0, 0, 1)).xyz - worldPos.xyz;
	//vec3 viewVector = normalize(worldPos.xyz - camPos);
	
	
	
	//Shadow stuff
	depthMat = shadowBias * shadowProjectionMatrix * shadowViewMatrix * worldPos;
	
	poissonDisk[0] = vec2( -0.54201624, -0.39906216 );
	poissonDisk[1] = vec2( 0.54558609, -0.36890725 );
	poissonDisk[2] = vec2( -0.054184101, -0.52938870 );
	poissonDisk[3] =  vec2( 0.34495938, 0.09387760 );
	poissonDisk[4] = vec2( 0.54201624, -0.09906216 );
	poissonDisk[5] = vec2( 0.04558609, -0.26890725 );
	
	
	vertexPass = worldPos.xyz;
	texCoordPass = texCoord * texCoordTile;
	untouchedTexCoord = texCoord;
	normalPass = normal;
	tangentPass = normalize(transformationMatrix * vec4(tangent, 0.0)).xyz;
	
	calculateTangents();
	
	gl_Position = projectionMatrix * viewMatrix * worldPos;
	
	//gl_Position.xy = floor(gl_Position.xy*gl_Position.w*.25)*4/gl_Position.w;
	
}

void calculateTangents()
{
	if (normalMapEnable){
			vec3 norm = normalize((transformationMatrix * vec4(normalPass, 0.0)).xyz);
			vec3 tang = tangentPass;
			vec3 bitang = normalize(cross(tang, norm));
		
			TSpace = mat3(tang, bitang, norm);
	}
}