#version 400 core
#extension GL_ARB_explicit_uniform_location : enable

#include "SpotLight.glsl"

#ifdef FLAG_TEXTURE
in vec2 pass_textureCoordinates;
#endif

in vec3 vectorToCamera;
in vec3 toLightVector[SUPPORTED_LIGHTS_COUNT];
in vec4 shadowCoordinates;

out vec4 out_Color;

#ifdef FLAG_DIFFUSE_MAP
uniform sampler2D diffuseMap;
#endif

#ifdef FLAG_NORMAL_MAP
uniform sampler2D normalMap;
#endif

#ifdef FLAG_SPECULAR_MAP
uniform sampler2D specularMap;
#endif

#ifdef FLAG_DISPLACEMENT_MAP
uniform sampler2D displacementMap;
#endif

uniform sampler2D shadowMap;

uniform vec3 diffuseColor;
uniform vec3 specularColor;
uniform float hasTransparency;
uniform float shineDamper;
uniform float reflectivity;

layout (location = 0) uniform SpotLight lights[SUPPORTED_LIGHTS_COUNT];

const int pcfCount = 1;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

void main(void) {

    float mapSize = 4096.0;
        float texelSize = 1.0 / mapSize;
        float total = 0.0;

        // TODO: Закодить нормально!
        float cosTheta = clamp(dot(vec3(0, 0, 1), normalize(toLightVector[0])), 0.0, 1.0);
        float bias  = 0.005 * tan(acos(cosTheta));
        bias = clamp(bias, 0.0, 0.01);

        for (int x = -pcfCount; x <= pcfCount; x++) {
            for (int y = -pcfCount; y <= pcfCount; y++) {
                float objectNearestLight = texture(shadowMap, shadowCoordinates.xy + vec2(x, y) * texelSize).r;
                if (shadowCoordinates.z > objectNearestLight + bias) {
                    total += 1.0;
                }
            }
        }

        total /= totalTexels;

        float lightFactor = 1.0 - (total * shadowCoordinates.w);

    vec4 materialColor;

#ifdef FLAG_DIFFUSE_MAP
    materialColor = texture(diffuseMap, pass_textureCoordinates);

    if (hasTransparency > 0.5 && materialColor.a < 0.5) {
        discard;
    }

    vec3 diffuseColorEffect = diffuseColor - vec3(0.5);
    materialColor.xyz = min(materialColor.xyz + diffuseColorEffect, vec3(1.0));
#else
    materialColor = vec4(diffuseColor, 1.0);
#endif

#ifdef FLAG_NORMAL_MAP
    vec4 normalMapValue = 2.0 * texture(normalMap, pass_textureCoordinates) - 1.0;
    vec3 unitNormal = normalize(normalMapValue.rgb);
#else
    vec3 unitNormal = vec3(0, 0, 1);
#endif

    float finalReflectivity = reflectivity;
#ifdef FLAG_SPECULAR_MAP
    finalReflectivity *= texture(specularMap, pass_textureCoordinates).r;
#endif

    vec3 normalizedVectorToCamera = normalize(vectorToCamera);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < SUPPORTED_LIGHTS_COUNT; i++) {
        float distance = length(toLightVector[i]);
        float attenuationFactor = lights[i].attenuation.x + (lights[i].attenuation.y * distance)
            + (lights[i].attenuation.z * distance * distance);
        vec3 normalizedToLightVector = normalize(toLightVector[i]);
        float brightness = max(dot(unitNormal, normalizedToLightVector), 0.0);

        float specularFactor = dot(reflect(-normalizedToLightVector, unitNormal), normalizedVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);

        totalDiffuse += (brightness * lights[i].color) / attenuationFactor;
        totalSpecular += (dampedFactor * finalReflectivity * lights[i].color) / attenuationFactor;
    }

    totalDiffuse = max(totalDiffuse * lightFactor, 0.4);
    totalSpecular *= specularColor;

    out_Color = vec4(totalDiffuse, 1.0) * materialColor + vec4(totalSpecular, 1.0);

}
