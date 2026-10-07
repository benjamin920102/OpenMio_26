#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform OutlineGlowConfig {
    vec4 Params;
};
#define Radius Params.y
#define Direction Params.zw

#define MAX_WIDTH 24

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec4 centerTexel = texture(InSampler, texCoord);

    float rad = max(Radius, 1.0);
    float invR2 = -3.0 / (rad * rad);

    vec4 sum = vec4(centerTexel.rgb * centerTexel.a, centerTexel.a);
    float weight = 1.0;
    vec2 stepDir = Direction * oneTexel;

    int maxI = min(int(rad), MAX_WIDTH);
    for (int i = 1; i <= maxI; i++) {
        float fi = float(i);
        float w = exp(fi * fi * invR2);

        vec4 t1 = texture(InSampler, texCoord + stepDir * fi);
        vec4 t2 = texture(InSampler, texCoord - stepDir * fi);

        sum += vec4(t1.rgb * t1.a + t2.rgb * t2.a, t1.a + t2.a) * w;
        weight += 2.0 * w;
    }

    fragColor = sum / weight;
}
