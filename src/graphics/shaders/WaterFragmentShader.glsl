#version 400
#extension GL_ARB_explicit_uniform_location : enable

#include "SpotLight.glsl"

out vec4 out_Color;

in vec4 clipSpace;
in vec2 textureCoordinates;
in vec3 toCameraVector;
in vec3 fromLightVectors[SUPPORTED_LIGHTS_COUNT];

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform vec2 moveFactor;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 diffuseColor;
uniform vec3 specularColor;
uniform float transparency;

layout (location = 0) uniform SpotLight lights[SUPPORTED_LIGHTS_COUNT];

const float waveStrength = 0.04;

void main(void) {

    vec2 ndc = (clipSpace.xy / clipSpace.w) / 2.0 + 0.5;
    vec2 refractTexCoords = vec2(ndc.x, ndc.y);
    vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);

    float near = 0.1;
    float far = 10000.0;

    float depth = texture(depthMap, refractTexCoords).r;
    float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));

    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
    float waterDepth = floorDistance - waterDistance;

    vec2 distortedTexCoords = texture(dudvMap, vec2(textureCoordinates.x + moveFactor.x, textureCoordinates.y)).rg * 0.1;
    distortedTexCoords = textureCoordinates + vec2(distortedTexCoords.x, distortedTexCoords.y + moveFactor.y);
    vec2 totalDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength
        * clamp(waterDepth / 20.0, 0.0, 1.0);

    refractTexCoords += totalDistortion;
    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    reflectTexCoords += totalDistortion;
    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);

    vec4 reflectColor = texture(reflectionTexture, reflectTexCoords);
    vec4 refractColor = texture(refractionTexture, refractTexCoords);

    vec4 normalMapColor = texture(normalMap, distortedTexCoords);
    vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3.0, normalMapColor.g * 2.0 - 1.0);
    normal = normalize(normal);

    vec3 viewVector = normalize(toCameraVector);
    float refractiveFactor = dot(viewVector, normal);
    refractiveFactor = pow(refractiveFactor, 0.5);
    refractiveFactor = clamp(refractiveFactor, 0.0, 1.0);

    vec3 totalSpecular = vec3(0.0);
    for (int i = 0; i < SUPPORTED_LIGHTS_COUNT; i++) {
        float distance = length(fromLightVectors[i]);
        float attenuationFactor = lights[i].attenuation.x + (lights[i].attenuation.y * distance)
            + (lights[i].attenuation.z * distance * distance);
        vec3 normalizedFromLightVector = normalize(fromLightVectors[i]);
        float specularFactor = dot(reflect(normalizedFromLightVector, normal), viewVector);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        totalSpecular += (dampedFactor * reflectivity * lights[i].color) / attenuationFactor;
    }
    totalSpecular *= clamp(waterDepth / 5.0, 0.0, 1.0);

    float transparencyFactor = clamp(waterDepth / transparency / 50.0, 0.0, 1.0);
    float depthAttenuation = max(waterDepth / transparency / 2.0, 1.0);
    refractColor = mix(refractColor, vec4(diffuseColor / depthAttenuation, 1.0), transparencyFactor);
    out_Color = mix(reflectColor, refractColor, refractiveFactor);
    out_Color = out_Color + vec4(totalSpecular, 0.0);
    out_Color.a = clamp(waterDepth / transparency, 0.0, 1.0);

}
