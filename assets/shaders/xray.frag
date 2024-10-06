#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_texture2;
uniform sampler2D u_mask;
uniform sampler2D u_noise;
uniform vec2 u_size;

varying vec4 v_color;
varying vec2 v_texCoord;



void main() {
    vec2 invertedY = vec2(v_texCoord.x, 1. - v_texCoord.y);

    vec4 covered = texture2D(u_texture, v_texCoord);
    vec4 xray = texture2D(u_texture2, v_texCoord);
    vec4 noise = texture2D(u_noise, v_texCoord);
    vec4 mask = texture2D(u_mask, invertedY);


    float mixAmount = mask.r;
    gl_FragColor = mix(covered, xray, mixAmount) * v_color;
}
