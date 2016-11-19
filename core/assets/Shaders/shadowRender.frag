#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define PI 3.14
varying vec2 vTexCoord0;
varying LOWP vec4 vColor;

uniform sampler2D u_texture;
uniform vec2 resolution;

uniform float softShadows;

//sample from the distance map
float sample(vec2 coord, float r) {
  // If the nearest occluder distance from origin is >= this fragment's distance from origin return 1 else 0
  return step(r, texture2D(u_texture, coord).r); // step returns 0 if arg2 < arg1 else 1
}

void main(void) {
    //rectangular to polar
	vec2 norm = vTexCoord0.st * 2.0 - 1.0; // change origin to center of texture instead of corner
	float theta = atan(norm.y, norm.x); // arctan of y/x is the angle pointing towards (x,y)
	float r = length(norm); // the length of radius/hypotenuse from origin to (x,y)
	float coord = (theta + PI) / (2.0*PI); // add 180 deg, divide by 360 deg to convert rads to a ratio (0.0-1.0) of the angle's progress around a circle

	//the tex coord to sample our 1D lookup texture
	//always 0.0 on y axis
	vec2 tc = vec2(coord, 0.0);

	//the center tex coord, which gives us hard shadows
	float center = sample(vec2(tc.x, tc.y), r);

	//we multiply the blur amount by our distance from center
	//this leads to more blurriness as the shadow "fades away"
	float blur = (1./resolution.x) * smoothstep(0., 1., r); // (smooth from 0.0 to 1.0) / resolution.x

	//now we use a simple gaussian blur
	float sum = 0.0;

	sum += sample(vec2(tc.x - 4.0*blur, tc.y), r) * 0.05;
	sum += sample(vec2(tc.x - 3.0*blur, tc.y), r) * 0.09;
	sum += sample(vec2(tc.x - 2.0*blur, tc.y), r) * 0.12;
	sum += sample(vec2(tc.x - 1.0*blur, tc.y), r) * 0.15;

	sum += center * 0.16;

	sum += sample(vec2(tc.x + 1.0*blur, tc.y), r) * 0.15;
	sum += sample(vec2(tc.x + 2.0*blur, tc.y), r) * 0.12;
	sum += sample(vec2(tc.x + 3.0*blur, tc.y), r) * 0.09;
	sum += sample(vec2(tc.x + 4.0*blur, tc.y), r) * 0.05;

	//1.0 -> in light, 0.0 -> in shadow
 	float lit = mix(center, sum, softShadows); // center * (1.0-softShadows) + sum * softShadows

 	//multiply the summed amount by our distance, which gives us a radial falloff
 	//then multiply by vertex (light) color
 	// Color is always pure white * color, alpha is determined by distance from origin
 	// obstructions automatically zerofy the alpha
 	gl_FragColor = vColor * vec4(vec3(1.0), lit * smoothstep(1.0, 0.0, r));
}