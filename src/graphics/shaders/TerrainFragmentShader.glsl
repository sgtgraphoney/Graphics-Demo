#version 400 core

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;

out vec4 out_Color;

uniform sampler2D diffuseMap;

#ifdef FLAG_NORMAL_MAP
uniform sampler2D normalMap;
#endif

#ifdef FLAG_SPECULAR_MAP
uniform sampler2D specularMap;
#endif

#ifdef FLAG_DISPLACEMENT_MAP
uniform sampler2D displacementMap;
#endif

uniform vec3 diffuseColor;
uniform vec3 specularColor;
uniform vec3 sunDirection;
uniform vec3 sunColor;
uniform float mapScale;
uniform float shineDamper;
uniform float reflectivity;

void main() {

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 toLightVector = -sunDirection;
    float brightness = max(dot(unitNormal, toLightVector), 0.2);

    vec3 diffuseColorEffect = diffuseColor - vec3(0.5);
    out_Color = min(texture(diffuseMap, pass_textureCoordinates * mapScale) + vec4(diffuseColorEffect, 0.0), vec4(1.0));
    out_Color = vec4(brightness * sunColor, 1.0) * out_Color;

}
