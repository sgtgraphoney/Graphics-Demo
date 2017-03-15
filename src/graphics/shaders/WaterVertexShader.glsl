#version 400
#extension GL_ARB_explicit_uniform_location : enable

#include "SpotLight.glsl"

in vec3 position;

out vec4 clipSpace;
out vec2 textureCoordinates;
out vec3 toCameraVector;
out vec3 fromLightVectors[SUPPORTED_LIGHTS_COUNT];

uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;

layout (location = 0) uniform SpotLight lights[SUPPORTED_LIGHTS_COUNT];

const float tiling = 14.0;

void main(void) {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    clipSpace = projectionMatrix * viewMatrix * worldPosition;
    gl_Position = clipSpace;

    textureCoordinates = vec2(position.x / 2.0 + 0.5, position.z / 2.0 + 0.5) * tiling;
    toCameraVector = cameraPosition - worldPosition.xyz;

    for(int i = 0; i < SUPPORTED_LIGHTS_COUNT; i++) {
        fromLightVectors[i] = worldPosition.xyz - lights[i].position;
    }

}
