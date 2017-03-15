#version 400 core
#extension GL_ARB_explicit_uniform_location : enable

#include "SpotLight.glsl"

#define MATERIALS_COUNT 4

const vec3 defaultColor = vec3(0.5);

in vec2 pass_textureCoordinates;
in vec3 vectorToCamera;
in vec3 toLightVector[SUPPORTED_LIGHTS_COUNT];
in vec4 shadowCoordinates;

out vec4 out_Color;

uniform sampler2D blendMap;
uniform sampler2D shadowMap;
uniform sampler2D diffuseMap[MATERIALS_COUNT];
uniform sampler2D normalMap[MATERIALS_COUNT];
uniform sampler2D specularMap[MATERIALS_COUNT];
uniform sampler2D displacementMap[MATERIALS_COUNT];

struct MaterialConfiguration {
    vec3 diffuseColor;
    vec3 specularColor;
    float mapScale;
    float shineDamper;
    float reflectivity;
    float useNormalMap;
    float useSpecularMap;
    float useDisplacementMap;
};

layout (location = 0) uniform SpotLight lights[SUPPORTED_LIGHTS_COUNT];
layout (location = 24) uniform MaterialConfiguration configs[MATERIALS_COUNT];

const int pcfCount = 1;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

void processMaterial(int index, float amount, inout float finalShineDamper, inout vec3 totalColor,
        inout float finalReflectivity, inout vec3 totalNormal, inout vec3 totalSpecularColor) {
    vec2 tiledCoordinates = pass_textureCoordinates * configs[index].mapScale;
    vec3 diffuseColorEffect = configs[index].diffuseColor - defaultColor;
    totalColor += ((texture(diffuseMap[index], tiledCoordinates)).rgb + diffuseColorEffect) * amount;

    if (configs[index].useNormalMap > 0.5) {
        vec4 normalMapValue = 2.0 * texture(normalMap[index], tiledCoordinates) - 1.0;
        totalNormal += normalMapValue.rgb * amount;
    } else {
        totalNormal += vec3(0.0, 0.0, 1.0) * amount;
    }

    float currentReflectivity = configs[index].reflectivity;
    if (configs[index].useSpecularMap > 0.5) {
        currentReflectivity *= texture(specularMap[index], tiledCoordinates).r;
    }
    if (currentReflectivity > 0.0) {
        finalShineDamper += configs[index].shineDamper * amount;
        finalReflectivity += currentReflectivity * amount;
        totalSpecularColor += configs[index].specularColor * amount;
    }
}

void main(void) {

    float mapSize = 4096.0;
    float texelSize = 1.0 / mapSize;
    float total = 0.0;

    for (int x = -pcfCount; x <= pcfCount; x++) {
        for (int y = -pcfCount; y <= pcfCount; y++) {
            float objectNearestLight = texture(shadowMap, shadowCoordinates.xy + vec2(x, y) * texelSize).r;
            if (shadowCoordinates.z > objectNearestLight + 0.0002) {
                total += 1.0;
            }
        }
    }

    total /= totalTexels;

    float lightFactor = 1.0 - (total * shadowCoordinates.w);

    vec4 blendMapColor = texture(blendMap, pass_textureCoordinates);

    float finalShineDamper = 0.0;
    float finalReflectivity = 0.0;
    vec3 totalColor = vec3(0.0);
    vec3 totalNormal = vec3(0.0);
    vec3 totalSpecularColor = vec3(0.0);

    float backTextureAmount = 1 - blendMapColor.r - blendMapColor.g - blendMapColor.b;

    processMaterial(0, backTextureAmount, finalShineDamper, totalColor, finalReflectivity, totalNormal,
            totalSpecularColor);

    for (int i = 1; i < MATERIALS_COUNT; i++) {
        processMaterial(i, blendMapColor[i - 1], finalShineDamper, totalColor, finalReflectivity, totalNormal,
                totalSpecularColor);
    }

    totalColor = clamp(totalColor, 0.0, 1.0);
    totalNormal = normalize(totalNormal);

    vec3 normalizedVectorToCamera = normalize(vectorToCamera);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < SUPPORTED_LIGHTS_COUNT; i++) {
        float distance = length(toLightVector[i]);
        float attenuationFactor = lights[i].attenuation.x + (lights[i].attenuation.y * distance)
            + (lights[i].attenuation.z * distance * distance);
        vec3 normalizedToLightVector = normalize(toLightVector[i]);
        float brightness = dot(totalNormal, normalizedToLightVector);
        brightness = max(brightness, 0.0);

        vec3 reflectedLight = reflect(-normalizedToLightVector, totalNormal);
        float specularFactor = dot(reflectedLight, normalizedVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = 1.0;
        if (specularFactor > 0.0 || finalShineDamper > 0.0) {
            dampedFactor = pow(specularFactor, finalShineDamper);
        }

        totalDiffuse += (brightness * lights[i].color) / attenuationFactor;
        totalSpecular += (dampedFactor * finalReflectivity * lights[i].color) / attenuationFactor;
    }

    totalDiffuse = max(totalDiffuse * lightFactor, 0.4);
    totalSpecular *= totalSpecularColor;

    out_Color = vec4(totalDiffuse * totalColor + totalSpecular, 1.0);

}
