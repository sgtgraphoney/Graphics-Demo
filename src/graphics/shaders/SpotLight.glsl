#define SUPPORTED_LIGHTS_COUNT 8

struct SpotLight {
    vec3 position;
    vec3 color;
    vec3 attenuation;
};