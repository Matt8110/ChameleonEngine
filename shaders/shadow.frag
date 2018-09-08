#version 330 core

out float colour;

void main()
{
		
		colour = gl_FragCoord.z;
}