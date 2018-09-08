#version 330 core

layout (location = 0) in vec2 vertex;
layout (location = 1) in vec2 texCoord;

out vec2 texCoordPass;

uniform bool renderingCubeMap;

void main()
{
	texCoordPass = texCoord;
	
	if (renderingCubeMap)
	{
		texCoordPass = -texCoordPass;
	}
		
		
	gl_Position = vec4(vertex, 0.01, 1.0);
}