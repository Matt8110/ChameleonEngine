#version 330 core

layout (location = 0) in vec3 vertex;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform bool renderingCubeMap;

out vec3 texCoordPass;

void main()
{
	texCoordPass = vertex;
	
	vec3 worldPos = vertex;
	
	if (!renderingCubeMap)
		//texCoordPass = -texCoordPass;
		//worldPos.y = -worldPos.y;
		
	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(worldPos, 1.0);
}