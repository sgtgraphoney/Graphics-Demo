#version 400 core
#extension GL_ARB_explicit_uniform_location : enable

#include "SpotLight.glsl"

in vec3 position;
in vec3 normal;
in vec3 tangent;

#ifdef FLAG_TEXTURE
in vec2 textureCoordinates;
out vec2 pass_textureCoordinates;
#endif

out vec3 vectorToCamera;
out vec3 toLightVector[SUPPORTED_LIGHTS_COUNT];
out vec4 shadowCoordinates;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform float useFakeLighting;
uniform vec4 plane;
uniform mat4 toShadowMapSpace;

layout (location = 0) uniform SpotLight lights[SUPPORTED_LIGHTS_COUNT];

const float shadowDistance = 100.0;
const float transitionDistance = 10.0;

void main(void) {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    shadowCoordinates = toShadowMapSpace * worldPosition;

    gl_ClipDistance[0] = dot(worldPosition, plane);

    mat4 modelViewMatrix = viewMatrix * transformationMatrix;
    vec4 positionRelativeToCam = modelViewMatrix * vec4(position, 1.0);

    gl_Position = projectionMatrix * positionRelativeToCam;

#ifdef FLAG_TEXTURE
    pass_textureCoordinates = textureCoordinates;
#endif

    vec3 actualNormal = normal;
    if (useFakeLighting > 0.5) {
        actualNormal = vec3(0.0, 1.0, 0.0);
    }

    vec3 surfaceNormal = (modelViewMatrix * vec4(actualNormal, 0.0)).xyz;

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
