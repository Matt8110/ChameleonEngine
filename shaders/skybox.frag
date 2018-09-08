#version 330 core

in vec3 texCoordPass;
out vec4 colour;
uniform samplerCube skybox;

void main()
{
	vec4 texColour = texture(skybox, texCoordPass);
	
	colour = texColour;
	
}