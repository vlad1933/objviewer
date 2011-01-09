uniform vec3 fvLightPosition;  // wo befindet sich die Lichtquelle?
uniform vec3 fvEyePosition;    // wo befindet sich der Betrachter?

varying vec3 ViewDirection;    // in welcher Richtung steht der Betrachter
varying vec3 LightDirection;   // in welcher Richtung befindet sich die Lichtquelle
varying vec3 Normal;  // wo ist die Normale?
varying float intensity;
   
void main( void )
{
   intensity = dot(LightDirection,gl_Normal);
   // übliche Berechnung der Position  
   gl_Position = ftransform();
   
   // übliche Transformation der Normalen 
   Normal         = gl_NormalMatrix * gl_Normal;
   
   
   // Berechnung von ViewDirection und LightDirection
   // Diese Parameter braucht man dann für jedes Fragment, um dort die Phong
   // Beleuchtungsrechnung durchführen zu lassen
   vec4 fvObjectPosition = gl_ModelViewMatrix * gl_Vertex;
   
   ViewDirection  = fvEyePosition - fvObjectPosition.xyz;
   LightDirection = fvLightPosition - fvObjectPosition.xyz; 
}