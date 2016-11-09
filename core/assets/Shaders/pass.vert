attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;

varying vec2 vTexCoord0;
varying vec4 vColor;

void main() {
  vColor = a_color;
  vTexCoord0 = a_texCoord0;
  gl_Position = u_projTrans * a_position;
}