#version 120

#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_lightPos;
varying vec3 v_lightColor;
varying vec2 v_position;

uniform sampler2D u_texture;

void main() {
    int dropoffDist = 100;
    float dist = 0;
    dist = pow(abs(v_lightPos.x - v_position.x), 2);
    dist += pow(abs(v_lightPos.y - v_position.y), 2);
    dist = sqrt(dist);

    gl_FragColor = v_color * texture2D(u_texture, v_texCoords) * min(.75, (dropoffDist / max(1, dist)));
    gl_FragColor = (gl_FragColor + vec4(v_lightColor, 0)) / 2;
}
