#version 400 core

in vec3 position;
in vec3 normal;
in vec2 textureCoordinates;

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 plane;

void main() {

    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);

    pass_textureCoordinates = textureCoordinates;
    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;

}
