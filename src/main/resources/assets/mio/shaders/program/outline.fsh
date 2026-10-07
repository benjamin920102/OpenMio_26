#version 330

#define Width 3

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform OutlineConfig {
    vec4 Config;
};

#define RenderMode     int(Config.x + 0.5)
#define FillOpacity    Config.y
#define OutlineOpacity Config.z
#define GlowOpacity    Config.w

layout(std140) uniform EspSettings {
    vec4 Rgb0;
    vec4 Rgb1;
    vec4 Rgb2;
    vec4 Rgb3;
    vec4 Params0;
    vec4 Params1;
    vec4 Params2;
    vec4 ScreenData;
    vec4 Extra;
    vec4 Opacities;
};

#define Time Params0.x
#define Step Params0.y
#define Alpha Params0.z
#define Dist Params0.w

#define Speed Params1.x
#define Blur Params1.z
#define Dots int(Params1.w + 0.5)

#define DotsRadius int(Params2.x + 0.5)
#define DotsAlpha Params2.y
#define Quality Params2.z
#define Effect int(Params2.w + 0.5)

#define Resolution OutSize
#define Offset ScreenData.zw

#define GlowMultiplier Extra.x
#define Size Extra.y
#define ShaderOpacity Alpha

in vec2 texCoord;

out vec4 fragColor;

float quad(float x) {
    return x * x;
}

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
    float distance = sqrt((gl_FragCoord.x * Dist) * (gl_FragCoord.x * Dist) + (gl_FragCoord.y * Dist) * (gl_FragCoord.y * Dist)) + Time;
    float distance2 = sqrt(((gl_FragCoord.x * Dist) - 1920.0) * ((gl_FragCoord.x * Dist) - 1920.0) + (gl_FragCoord.y * Dist) * (gl_FragCoord.y * Dist)) + Time;
    float distance3 = sqrt(((gl_FragCoord.x * Dist) - 1080.0) * ((gl_FragCoord.x * Dist) - 1080.0) + ((gl_FragCoord.y * Dist) - 1080.0) * ((gl_FragCoord.y * Dist) - 1080.0)) + Time;

    distance = sin(distance / max(Step, 0.001) + sin((gl_FragCoord.y * Dist) / max(Step, 0.001))) * 0.5 + 0.5;
    distance2 = sin(distance2 / max(Step, 0.001) + sin((gl_FragCoord.x * Dist) / max(Step, 0.001))) * 0.5 + 0.5;
    distance3 = sin(distance3 / max(Step, 0.001) + sin((gl_FragCoord.y * Dist) / max(Step, 0.001) + (gl_FragCoord.x * Dist) / max(Step, 0.001))) * 0.5 + 0.5;

    float ripple = sin(distance * 10.0 + Step * 10000.0) * 0.05;
    float swirl = cos(((gl_FragCoord.x * Dist) - 960.0) * 0.05 + ((gl_FragCoord.y * Dist) - 540.0) * 0.05 + Step) * 0.05;

    distance += ripple + swirl;
    distance2 += ripple - swirl;
    distance3 += ripple + swirl;

    float inv = 1.0 - distance;
    return Rgb0.rgb * distance + Rgb1.rgb * inv + Rgb2.rgb * distance2 + Rgb3.rgb * distance3;
}

// 2: Smoke
float smoke_fbm(vec2 pos) {
    float v = 0.0;
    float a = 0.5;
    float speed = Time * max(Speed, 0.01);
    for (int i = 0; i < 6; i++) {
        float dir = float(i & 1) * 2.0 - 1.0;
        v += a * noise(pos - 0.05 * dir * speed);
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

    vec3 color = mix(
        vec3(0.3, 0.3, 0.3),
        vec3(0.7, 0.7, 0.7),
        clamp(f * f * 4.0, 0.0, 1.0)
    );

    color = mix(color, vec3(0.7, 0.7, 0.7), clamp(length(q), 0.0, 1.0));
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
    vec3 col = baseColor * (0.4 + nebDust * 0.6) + starCol * 1.5;
    return vec4(col, Rgb0.a);
}

// 4: Waves
vec4 esp_waves() {
    vec2 coord = gl_FragCoord.xy + Offset;
    vec2 uv = (coord / Resolution.xy) * 2.0 - 1.0;
    float distanceVal = max(Dist, 1.0);
    float phase = (-(uv.x + uv.y)) * distanceVal - Time * max(Speed, 0.01);
    float wave = sin(phase);
    float fillFactor = 1.0 - smoothstep(Params1.y, Params1.y + max(Blur, 0.01), abs(wave));
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
    col *= scanline;

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
    vec3 rainbowCol = getRainbowColor(uv.y * 6.28, Time);
    return vec4(rainbowCol, 1.0);
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
    vec3 animatedColor = mix(Rgb0.rgb, Rgb1.rgb, gradient);
    return vec4(animatedColor, 1.0);
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

vec4 esp_color(vec4 current) {
    if (Effect == 1) return vec4(esp_gradient(), current.a);
    if (Effect == 2) { vec4 s = esp_smoke(); return vec4(s.rgb, current.a * s.a); }
    if (Effect == 3) { vec4 sn = esp_astral(); return vec4(sn.rgb, current.a * sn.a); }
    if (Effect == 4) { vec4 w = esp_waves(); return vec4(w.rgb, current.a * w.a); }
    if (Effect == 5) { vec4 m = esp_hologrid(); return vec4(m.rgb, current.a * m.a); }
    if (Effect == 6) { vec4 r = esp_rainbow(); return vec4(r.rgb, current.a * r.a); }
    if (Effect == 7) { vec4 fl = esp_quantum(); return vec4(fl.rgb, current.a * fl.a); }
    if (Effect == 8) { vec4 p = esp_pulse(); return vec4(p.rgb, current.a * p.a); }
    if (Effect == 9) { vec4 bl = esp_prism(); return vec4(bl.rgb, current.a * bl.a); }
    return current;
}

vec4 processOutline() {
    vec4 center = texture(InSampler, texCoord);

    if (center.a > 0.0) {
        if (RenderMode == 0) return vec4(0.0);

        vec4 fill = esp_color(center);
        float alphaMult = (Effect != 0) ? (FillOpacity * ShaderOpacity) : FillOpacity;
        return vec4(fill.rgb, center.a * alphaMult);
    }

    if (RenderMode == 1) return vec4(0.0);

    float alpha = 0.0;
    vec4 outlineColor = vec4(0.0);
    float closestDist = 9999.0;
    vec2 oneTexel = 1.0 / OutSize;

    for (int x = -Width; x <= Width; x++) {
        for (int y = -Width; y <= Width; y++) {
            if (x == 0 && y == 0) continue;
            vec2 offset = vec2(float(x), float(y)) * oneTexel;
            vec4 neighbor = texture(InSampler, texCoord + offset);
            if (neighbor.a > 0.0) {
                float dist = quad(float(x)) + quad(float(y));
                if (dist < closestDist) {
                    closestDist = dist;
                    outlineColor = neighbor;
                }
                alpha += 1.0;
            }
        }
    }

    if (alpha <= 0.0) return vec4(0.0);

    vec4 outCol = esp_color(outlineColor);
    float outlineAlpha = min(alpha / (float(Width) * 4.0), 1.0);
    float alphaMult = (Effect != 0) ? (OutlineOpacity * ShaderOpacity) : OutlineOpacity;
    return vec4(outCol.rgb, outlineAlpha * alphaMult);
}

void main() {
    fragColor = processOutline();
}
