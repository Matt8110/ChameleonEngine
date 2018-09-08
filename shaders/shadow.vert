#version 330 core

layout (location = 0) in vec3 vertex_modelSpace;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord;

uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;

void main()
{
	vec4 finalPostion = projectionMatrix * viewMatrix * transformationMatrix * vec4(vertex_modelSpace, 1.0);
	gl_Position = finalPostion;
	
}