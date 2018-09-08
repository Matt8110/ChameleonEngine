#version 330 core

layout (location = 0) in vec2 vertex;
layout (location = 1) in vec2 texCoord;
out vec2 texCoordsPass;

void main(void)
{

	texCoordsPass = texCoord;

	gl_Position = vec4(vertex.xy, -0.1, 1.0);
	
}