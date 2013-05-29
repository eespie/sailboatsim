#ifdef COLORMAP
    uniform sampler2D m_ColorMap;
#endif


#ifdef COLOR
    uniform vec4 m_Color;
#endif

varying vec2 texCoord;
/*
* fragment shader template
*/
void main() {
    // Set the fragment color for example to gray, alpha 1.0
    vec4 color = vec4(1.0);

    #ifdef COLOR
        color = m_Color;
    #endif

    #ifdef COLORMAP
        color *= texture2D(m_ColorMap, texCoord, 0.0);
    #endif

    gl_FragColor = color;
}

