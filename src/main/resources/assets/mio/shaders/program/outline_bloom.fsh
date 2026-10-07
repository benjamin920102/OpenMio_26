#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform BloomConfig {
    vec4 Params0;
    vec4 Params1;
    vec4 GradientColor;
    vec4 TimeRes;
};

#define u_Width          Params0.x
#define u_GlowInside     int(Params0.y)
#define u_GlowQuality    int(Params0.z)
#define u_GlowMultiplier Params0.w
#define u_FillAlpha      Params1.x
#define u_OutlineAlpha   Params1.y
#define u_FillMode       int(Params1.z)
#define u_GradientFactor Params1.w
#define u_GradientColor  GradientColor
#define u_ShaderTime     TimeRes.x
#define u_GlowAlpha      TimeRes.y
#define u_Resolution     OutSize

layout(std140) uniform EspSettings {
    vec4 Rgb0;
    vec4 Rgb1;
    vec4 Rgb2;
    vec4 Rgb3;
    vec4 EspP0;
    vec4 EspP1;
    vec4 Params2;
    vec4 ScreenData;
    vec4 Extra;
    vec4 Opacities;
};

#define Time          EspP0.x
#define Step          EspP0.y
#define ShaderOpacity EspP0.z
#define Dist          EspP0.w
#define Speed         EspP1.x
#define EspWidth      EspP1.y
#define EspBlur       EspP1.z
#define Dots          int(EspP1.w + 0.5)
#define DotsRadius    int(Params2.x + 0.5)
#define DotsAlpha     Params2.y
#define Size          Extra.y
#define GlowMultiplier Extra.x
#define Resolution    ScreenData.xy
#define Offset        ScreenData.zw
#define EspEffect     int(Params2.w + 0.5)

in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453123);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);
}

const mat2 ROT = mat2(0.8775826, 0.4794255, -0.4794255, 0.8775826);

// 1: Gradient
vec3 esp_gradient() {
    vec2 coord = gl_FragCoord.xy + Offset;
    float d  = sqrt((coord.x * Dist) * (coord.x * Dist) + (coord.y * Dist) * (coord.y * Dist)) + Time;
    float d2 = sqrt(((coord.x * Dist) - 1920.0) * ((coord.x * Dist) - 1920.0) + (coord.y * Dist) * (coord.y * Dist)) + Time;
    float d3 = sqrt(((coord.x * Dist) - 1080.0) * ((coord.x * Dist) - 1080.0) + ((coord.y * Dist) - 1080.0) * ((coord.y * Dist) - 1080.0)) + Time;

    d  = sin(d  / max(Step, 0.001) + sin((coord.y * Dist) / max(Step, 0.001))) * 0.5 + 0.5;
    d2 = sin(d2 / max(Step, 0.001) + sin((coord.x * Dist) / max(Step, 0.001))) * 0.5 + 0.5;
    d3 = sin(d3 / max(Step, 0.001) + sin((coord.y * Dist) / max(Step, 0.001) + (coord.x * Dist) / max(Step, 0.001))) * 0.5 + 0.5;

    float ripple = sin(d  * 10.0 + Step * 10000.0) * 0.05;
    float swirl  = cos(((coord.x * Dist) - 960.0) * 0.05 + ((coord.y * Dist) - 540.0) * 0.05 + Step) * 0.05;

    d  += ripple + swirl;
    d2 += ripple - swirl;
    d3 += ripple + swirl;

    float inv = 1.0 - d;
    return Rgb0.rgb * d + Rgb1.rgb * inv + Rgb2.rgb * d2 + Rgb3.rgb * d3;
}

// 2: Smoke
float smoke_fbm(vec2 pos) {
    float v = 0.0, a = 0.5;
    float spd = Time * max(Speed, 0.01);
    for (int i = 0; i < 6; i++) {
        float dir = float(i & 1) * 2.0 - 1.0;
        v += a * noise(pos - 0.05 * dir * spd);
        pos = ROT * pos * 2.0 + 100.0;
        a *= 0.5;
    }
    return v;
}

vec4 esp_smoke() {
    vec2 coord = gl_FragCoord.xy + Offset;
    vec2 p = (coord * 2.0 - Resolution.xy) / min(Resolution.x, Resolution.y) - vec2(12.0, 0.0);
    vec2 q = vec2(smoke_fbm(p), smoke_fbm(p + 1.0));
    float f = smoke_fbm(p + q);
    vec3 color = mix(vec3(0.3), vec3(0.7), clamp(f * f * 4.0, 0.0, 1.0));
    color = mix(color, vec3(0.7), clamp(length(q), 0.0, 1.0));
    color = (f * f * f + 0.9 * f * f + 0.8 * f) * color;
    return Rgb0 + (Rgb1 - Rgb0) * color.r;
}

// 3: Astral
float astral_star(vec2 p, float s) {
    vec2 ip = floor(p * s);
    vec2 fp = fract(p * s) - 0.5;
    float h = hash(ip);
    if (h < 0.65) return 0.0;

    float twinkle = sin(Time * (2.0 + h * 4.0) + h * 6.28) * 0.5 + 0.5;
    float d = length(fp);
    float core = 0.02 / (d + 0.04);
    float crossGlint = max(0.0, 1.0 - abs(fp.x) * 12.0) * max(0.0, 1.0 - abs(fp.y) * 45.0) +
                       max(0.0, 1.0 - abs(fp.y) * 12.0) * max(0.0, 1.0 - abs(fp.x) * 45.0);
    return (core * 0.6 + crossGlint * 0.4) * twinkle;
}

vec4 esp_astral() {
    vec2 coord = gl_FragCoord.xy + Offset;
    vec2 uv = (coord * 2.0 - Resolution.xy) / min(Resolution.x, Resolution.y);
    float scale = max(Step, 5.0) / 25.0;

    float neb1 = sin(uv.x * 2.0 + Time * 0.4) * cos(uv.y * 2.5 - Time * 0.3) * 0.5 + 0.5;
    float neb2 = sin(uv.x * 3.5 - uv.y * 2.8 + Time * 0.6) * 0.5 + 0.5;
    float nebDust = pow(neb1 * 0.6 + neb2 * 0.4, 2.0);

    vec3 baseColor = mix(Rgb0.rgb, Rgb1.rgb, nebDust);
    float stars = astral_star(uv + vec2(Time * 0.02, 0.0), 4.0 * scale)
                + astral_star(uv - vec2(0.0, Time * 0.015), 7.0 * scale) * 0.8
                + astral_star(uv + vec2(Time * 0.03, Time * 0.02), 12.0 * scale) * 0.6;

    vec3 starCol = mix(vec3(1.0), Rgb1.rgb, 0.25) * stars;
    vec3 col = (baseColor * (0.4 + nebDust * 0.6) + starCol * 1.5) * max(GlowMultiplier, 1.0);
    return vec4(col, Rgb0.a);
}

// 4: Waves
vec4 esp_waves() {
    vec2 coord = gl_FragCoord.xy + Offset;
    vec2 uv = (coord / Resolution.xy) * 2.0 - 1.0;
    float phase = (-(uv.x + uv.y)) * max(Dist, 1.0) - Time * max(Speed, 0.01);
    float wave = sin(phase);
    float fillFactor = 1.0 - smoothstep(EspWidth, EspWidth + max(EspBlur, 0.01), abs(wave));
    return mix(Rgb0, Rgb1, fillFactor);
}

// 5: HoloGrid
vec4 esp_hologrid() {
    vec2 coord = gl_FragCoord.xy + Offset;
    float scale = max(Step, 5.0) / 15.0;
    vec2 p = coord * scale * 0.05;

    float scanline = sin(coord.y * 0.35 + Time * 3.0) * 0.15 + 0.85;
    vec2 gridFract = abs(fract(p) - 0.5);
    float gridLine = smoothstep(0.44, 0.49, max(gridFract.x, gridFract.y));

    float nodeDist = length(gridFract - 0.5);
    float node = smoothstep(0.2, 0.02, nodeDist);
    float nodePulse = (sin(Time * 4.0 + p.x * 2.0 + p.y * 2.0) * 0.5 + 0.5) * node;

    vec3 col = mix(Rgb0.rgb * 0.4, Rgb1.rgb, gridLine * 0.8 + nodePulse * 1.2);
    col += Rgb2.rgb * (node * 0.6);
    col *= scanline * max(GlowMultiplier, 1.0);

    float alpha = mix(1.0, DotsAlpha, gridLine * 0.5 + node * 0.5);
    return vec4(col, alpha);
}

// 6: Rainbow
vec3 getRainbowColor(float y, float timeVal) {
    vec3 o = -3.1416 * vec3(0.0, 0.5, 1.0);
    float g = y + timeVal;
    vec3 col = 0.5 + 0.5 * -sin(g) * cos(g + o);
    col.g += 0.25;
    col = 0.5 + (col * 2.0 - 1.0);
    col.gb *= vec2(0.75, 0.9);
    return 0.125 + 0.75 * col;
}

vec4 esp_rainbow() {
    vec2 coord = gl_FragCoord.xy + Offset;
    vec2 uv = coord / Resolution.xy;
    return vec4(getRainbowColor(uv.y * 6.28, Time), 1.0);
}

// 7: Quantum
vec4 esp_quantum() {
    vec2 coord = gl_FragCoord.xy + Offset;
    vec2 p = (coord * 2.0 - Resolution.xy) / min(Resolution.x, Resolution.y);
    float scale = max(Step, 5.0) / 25.0;
    p *= scale;

    float r = length(p);
    float a = atan(p.y, p.x);
    float t = Time * 1.5;

    float spiral1 = sin(a * 3.0 - log(max(r, 0.001)) * 4.0 + t);
    float spiral2 = sin(a * 5.0 + log(max(r, 0.001)) * 3.0 - t * 1.2);
    float arm = smoothstep(-0.2, 0.9, spiral1 * 0.6 + spiral2 * 0.4);

    float core = 0.08 / (r + 0.05);
    float ring = exp(-abs(r - 0.7) * 4.0) * (sin(a * 8.0 + t * 2.0) * 0.3 + 0.7);

    vec3 col = mix(Rgb0.rgb, Rgb1.rgb, arm);
    col = mix(col, Rgb2.rgb, ring);
    col += Rgb3.rgb * (core * 0.6);
    col *= max(GlowMultiplier, 1.0);

    return vec4(col, 1.0);
}

// 8: Pulse
vec4 esp_pulse() {
    vec2 coord = gl_FragCoord.xy + Offset;
    float gradient = 0.5 + 0.5 * sin(Time + (coord.x / Resolution.x) * max(Size, 1.0));
    return vec4(mix(Rgb0.rgb, Rgb1.rgb, gradient), 1.0);
}

// 9: Prism
vec4 esp_prism() {
    vec2 coord = gl_FragCoord.xy + Offset;
    vec2 uv = (coord * 2.0 - Resolution.xy) / min(Resolution.x, Resolution.y);
    float scale = max(Step, 5.0) / 20.0;
    vec2 p = uv * scale;

    float t = Time * 0.8;
    float a = atan(p.y, p.x);
    float d = length(p);

    float facet1 = sin(dot(p, vec2(cos(t * 0.5), sin(t * 0.5))) * 6.0 + t);
    float facet2 = sin(dot(p, vec2(-sin(t * 0.4), cos(t * 0.4))) * 8.0 - t * 0.7);
    float facet3 = sin(a * 6.0 + d * 5.0 - t);
    float shimmer = pow(abs(facet1 * facet2 + facet3) * 0.5, 2.0);

    float redDisp = sin(a * 4.0 - d * 6.0 + t * 1.1) * 0.5 + 0.5;
    float greenDisp = sin(a * 4.0 - d * 6.0 + t * 1.1 + 2.094) * 0.5 + 0.5;
    float blueDisp = sin(a * 4.0 - d * 6.0 + t * 1.1 + 4.188) * 0.5 + 0.5;
    vec3 chromatic = vec3(redDisp, greenDisp, blueDisp);

    vec3 baseCol = mix(Rgb0.rgb, Rgb1.rgb, shimmer);
    vec3 col = mix(baseCol, chromatic * Rgb2.rgb, 0.45);
    col += (shimmer * 0.8 + 0.2) * Rgb0.rgb;
    col *= max(GlowMultiplier, 1.0);

    return vec4(col, Rgb0.a);
}

vec4 esp_color(int effect) {
    if (effect == 1) return vec4(esp_gradient(), 1.0);
    if (effect == 2) return esp_smoke();
    if (effect == 3) return esp_astral();
    if (effect == 4) return esp_waves();
    if (effect == 5) return esp_hologrid();
    if (effect == 6) return esp_rainbow();
    if (effect == 7) return esp_quantum();
    if (effect == 8) return esp_pulse();
    if (effect == 9) return esp_prism();
    return vec4(1.0);
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec4 getFill(vec3 centerColor, float alpha) {
    if (u_FillMode == 1) {
        float time = u_ShaderTime / 5.0;
        float d = sqrt(gl_FragCoord.x * gl_FragCoord.x + gl_FragCoord.y * gl_FragCoord.y) + time;
        d = (sin(d / u_GradientFactor) + 1.0) / 2.0;
        float j = 1.0 - d;
        return vec4(
            centerColor.r * d + u_GradientColor.r * j,
            centerColor.g * d + u_GradientColor.g * j,
            centerColor.b * d + u_GradientColor.b * j,
            alpha * d + u_GradientColor.a * j
        );
    }

    if (u_FillMode == 4) {
        float zoom = 1.0;
        vec2 uv = (gl_FragCoord.xy / u_Resolution.xy - 0.5) * zoom + 0.5;
        float theta = uv.x * 3.14159;
        float phi   = uv.y * 3.14159 * 0.5;
        vec3 dir = vec3(cos(phi) * cos(theta), sin(phi), cos(phi) * sin(theta));
        float time = u_ShaderTime / 750.0;
        float rot = time * 0.2;
        mat2 rotMat = mat2(cos(rot), -sin(rot), sin(rot), cos(rot));
        dir.xz = rotMat * dir.xz;
        float dist  = length(dir.xy);
        float angle = atan(dir.y, dir.x);
        float spiral = sin(dist * 10.0 - angle * 3.0 - time * 2.0);
        float hue = fract(dist * 2.0 - time * 0.3 + angle / 6.28318);
        vec3 rainbowColor = hsv2rgb(vec3(hue, 0.8, 1.0));
        float rings = pow(max(0.0, sin(dist * 20.0 - time * 3.0)), 3.0);
        vec3 finalColor = rainbowColor * (spiral * 0.3 + 0.7) + vec3(1.0) * rings * 0.5;
        finalColor += vec3(1.0, 0.9, 1.0) * exp(-dist * 3.0) * 0.5;
        return vec4(finalColor, alpha);
    }

    return vec4(centerColor, alpha);
}

struct BloomSample {
    vec3 rgb;
    float coverage;
    float edgeDist;
};

BloomSample sample_bloom_composite(vec2 uv, vec2 oneTexel) {
    BloomSample result;
    result.rgb = vec3(0.0);
    result.coverage = 0.0;
    result.edgeDist = 999.0;

    float r = max(u_Width, 1.0) * float(max(u_GlowQuality, 1)) * 1.5;
    int tapCount = u_GlowQuality >= 3 ? 48 : (u_GlowQuality == 2 ? 32 : 24);

    vec4 colorAcc = vec4(0.0);
    float weightAcc = 0.0;
    float closestDist = 999.0;
    vec3 nearestColor = vec3(0.0);

    for (int i = 0; i < 48; i++) {
        if (i >= tapCount) break;

        float fi = float(i) + 0.5;
        float distFrac = sqrt(fi / float(tapCount));
        float theta = fi * 2.3999632;

        vec2 sampleOffset = vec2(cos(theta), sin(theta)) * (distFrac * r) * oneTexel;
        vec4 s = texture(InSampler, uv + sampleOffset);

        float w = exp(-distFrac * distFrac * 2.8);
        weightAcc += w;

        if (s.a > 0.0) {
            colorAcc += vec4(s.rgb * s.a, s.a) * w;
            float tapDist = distFrac * r;
            if (tapDist < closestDist) {
                closestDist = tapDist;
                nearestColor = s.rgb;
            }
        }
    }

    result.edgeDist = closestDist;
    if (weightAcc > 0.0001) {
        result.coverage = colorAcc.a / weightAcc;
        result.rgb = colorAcc.a > 0.0001 ? (colorAcc.rgb / colorAcc.a) : nearestColor;
    } else {
        result.rgb = nearestColor;
    }

    return result;
}

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec4 center = texture(InSampler, texCoord);
    int effect = EspEffect;

    BloomSample bloom = sample_bloom_composite(texCoord, oneTexel);

    if (center.a > 0.0) {
        vec3 rgb = (effect != 0) ? (esp_color(effect).rgb * center.rgb) : getFill(center.rgb, 1.0).rgb;
        float baseAlpha = (effect != 0) ? (u_FillAlpha * esp_color(effect).a) : u_FillAlpha;

        if (u_GlowInside == 1) {
            float innerProximity = clamp((1.0 - bloom.coverage) * 2.0, 0.0, 1.0);
            float innerAlpha = mix(baseAlpha, u_OutlineAlpha, innerProximity);
            fragColor = vec4(rgb, clamp(innerAlpha, 0.0, 1.0));
        } else {
            fragColor = vec4(rgb, clamp(baseAlpha, 0.0, 1.0));
        }
        return;
    }

    float outlineWidth = max(u_Width, 1.0);
    float outlineEdge = clamp(1.0 - (bloom.edgeDist / outlineWidth), 0.0, 1.0);
    float outlineAlpha = smoothstep(0.0, 1.0, outlineEdge) * u_OutlineAlpha;

    float glowCoverage = clamp(bloom.coverage * u_GlowMultiplier, 0.0, 1.0);
    float glowAlpha = pow(glowCoverage, 0.85) * u_GlowAlpha;

    float finalAlpha = max(outlineAlpha, glowAlpha);
    if (finalAlpha < 0.001) discard;

    vec3 outlineRGB = bloom.rgb;
    vec3 finalRGB = (effect != 0) ? (esp_color(effect).rgb * outlineRGB) : getFill(outlineRGB, 1.0).rgb;

    fragColor = vec4(finalRGB, clamp(finalAlpha, 0.0, 1.0));
}
