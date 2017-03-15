#version 400 core
#extension GL_ARB_explicit_uniform_location : enable

#include "SpotLight.glsl"

in vec3 position;
in vec3 normal;
in vec2 textureCoordinates;
in vec3 tangent;

out vec2 pass_textureCoordinates;
out vec3 vectorToCamera;
out vec3 toLightVector[SUPPORTED_LIGHTS_COUNT];
out vec4 shadowCoordinates;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 plane;
uniform mat4 toShadowMapSpace;

const float shadowDistance = 100.0;
const float transitionDistance = 10.0;

layout (location = 0) uniform SpotLight lights[SUPPORTED_LIGHTS_COUNT];

void main(void) {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    shadowCoordinates = toShadowMapSpace * worldPosition;

    gl_ClipDistance[0] = dot(worldPosition, plane);

    mat4 modelViewMatrix = viewMatrix * transformationMatrix;
    vec4 positionRelativeToCam = modelViewMatrix * vec4(position, 1.0);

    gl_Position = projectionMatrix * positionRelativeToCam;

    pass_textureCoordinates = textureCoordinates;

    vec3 surfaceNormal = (modelViewMatrix * vec4(normal, 0.0)).xyz;

    vec3 nrm = normalize(surfaceNormal);
    vec3 tg = normalize((modelViewMatrix * vec4(tangent, 0.0)).xyz);
    vec3 btg = normalize(cross(nrm, tg));

    mat3 toTangentSpace = mat3(
        tg.x, btg.x, nrm.x,
        tg.y, btg.y, nrm.y,
        tg.z, btg.z, nrm.z
    );

    for (int i = 0; i < SUPPORTED_LIGHTS_COUNT; i++) {
        toLightVector[i] = toTangentSpace * (viewMatrix * vec4(lights[i].position, 1.0) - positionRelativeToCam).xyz;
    }

    vectorToCamera = toTangentSpace * (-positionRelativeToCam.xyz);

    float distance = length(positionRelativeToCam.xyz) - (shadowDistance - transitionDistance);
    distance = distance / transitionDistance;
    shadowCoordinates.w = clamp(1.0 - distance, 0.0, 1.0);

}
