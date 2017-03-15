#version 150

in vec3 in_position;
in vec2 in_textureCoordinates;

out  vec2 textureCoordinates;

uniform mat4 mvpMatrix;

void main(void){

	gl_Position = mvpMatrix * vec4(in_position, 1.0);
	textureCoordinates = in_textureCoordinates;

}