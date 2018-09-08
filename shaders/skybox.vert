#version 330 core

layout (location = 0) in vec3 vertex;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out vec3 texCoordPass;

void main()
{
	texCoordPass = vertex;
	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(vertex, 1.0);
}