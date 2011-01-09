// Phong Shader für Lehrveranstaltung zur Shader Programmierung
// von Prof. Dr. Ralf Dörner, FH Wiesbaden
// Realisiert Phong Shading analog zur Fixed Functionality von Open Gl

uniform vec4 warmColor;

uniform vec4 fvAmbient;         // Farbe ambienter Lichtanteil
uniform vec4 fvSpecular;        // Farbe spekularer Lichtanteil
uniform vec4 fvDiffuse;         // Farbe diffuser Lichtanteil
uniform float fSpecularPower;   // wie stark glänzt das Material

varying vec3 ViewDirection;
varying vec3 LightDirection;
varying vec3 Normal;

void main( void )
{
   // Berechnung der verschiedenen Lichtanteile gemäß den Formeln des Phong
   // Beleuchtungsmodells
   float weight_a = 0.5;
   float weight_b = 0.3;
   float weight_w;
  
   vec4  fvBaseColor = vec4(1.0, 0.0, 0.0, 1.0);
  
   vec4 warmColor    = vec4(1.0, 0.5, 0.0, 1.0); //orange
   vec4 coldColor    = vec4(0.0, 0.5, 1.0, 1.0); //blau
  
   vec4 wWarmColor = (warmColor+(weight_b * fvBaseColor));
   vec4 wColdColor = (coldColor+(weight_a * fvBaseColor));
  
 
  
   vec3  fvLightDirection = normalize( LightDirection );
   vec3  fvNormal         = normalize( Normal );
   float fNDotL           = dot( fvNormal, fvLightDirection );
  
   vec3  fvReflection     = normalize( ( ( 2.0 * fvNormal ) * fNDotL ) - fvLightDirection );
   vec3  fvViewDirection  = normalize( ViewDirection );
   float fRDotV           = max( 0.0, dot( fvReflection, fvViewDirection ) );
 
   weight_w = (1+dot(fvNormal,fvReflection))/2;
  
   vec4 rBaseColorSummed = weight_w * wColdColor + (1 - weight_w) * wWarmColor;;
  
   gl_FragColor = rBaseColorSummed;
  
  // vec4  fvTotalAmbient   = fvAmbient * fvBaseColor;
  // vec4  fvTotalDiffuse   = fvDiffuse * fNDotL * fvBaseColor;
  // vec4  fvTotalSpecular  = fvSpecular * ( pow( fRDotV, fSpecularPower ) );
  
 
   // Lichanteile werden zusammengesetzt zur Gesamtfarbe des Fragments
  // gl_FragColor = ( fvTotalAmbient + fvTotalDiffuse + fvTotalSpecular );
}