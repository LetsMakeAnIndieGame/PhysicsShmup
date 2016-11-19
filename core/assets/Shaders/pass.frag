#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
//    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    gl_FragColor = vec4(v_color.rgb * 0.1, v_color.a) * texture2D(u_texture, v_texCoords);
}