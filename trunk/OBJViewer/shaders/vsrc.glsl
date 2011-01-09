uniform vec3 fvLightPosition;  // wo befindet sich die Lichtquelle?
uniform vec3 fvEyePosition;    // wo befindet sich der Betrachter?

varying vec3 ViewDirection;    // in welcher Richtung steht der Betrachter
varying vec3 LightDirection;   // in welcher Richtung befindet sich die Lichtquelle
varying vec3 Normal;  // wo ist die Normale?
varying float intensity;
   
void main( void )
{
   intensity = dot(LightDirection,gl_Normal);
   // �bliche Berechnung der Position  
   gl_Position = ftransform();
   
   // �bliche Transformation der Normalen 
   Normal         = gl_NormalMatrix * gl_Normal;
   
   
   // Berechnung von ViewDirection und LightDirection
   // Diese Parameter braucht man dann f�r jedes Fragment, um dort die Phong
   // Beleuchtungsrechnung durchf�hren zu lassen
   vec4 fvObjectPosition = gl_ModelViewMatrix * gl_Vertex;
   
   ViewDirection  = fvEyePosition - fvObjectPosition.xyz;
   LightDirection = fvLightPosition - fvObjectPosition.xyz; 
}