#version 120

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_worldView;
uniform vec2 u_lightPos;
uniform vec3 u_lightColor;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_lightPos;
varying vec3 v_lightColor;
varying vec2 v_position;

void main() {
    v_color = a_color;
    v_lightColor = u_lightColor;
    v_lightPos = u_lightPos;
    v_position.xy = a_position.xy;
    v_texCoords = a_texCoord0;
    gl_Position =  u_worldView * a_position;
}

