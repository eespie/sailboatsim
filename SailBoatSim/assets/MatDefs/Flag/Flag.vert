attribute vec3 inPosition;
attribute vec2 inTexCoord;

uniform mat4 g_WorldViewProjectionMatrix;
uniform float g_Time;

varying vec2 texCoord;

#ifdef WAVELENGTH
    uniform float m_WaveLength;
#endif
#ifdef AMPLITUDE
    uniform float m_Amplitude;
#endif
#ifdef TIME
    uniform float m_Time;
#endif


/*
* vertex shader to animate a flag
*/
void main() { 
    // Vertex transformation
    texCoord = inTexCoord;
    vec4 position = vec4(inPosition, 1.0);

    float waveLength = 3.0;
    #ifdef WAVELENGTH
        waveLength = m_WaveLength;
    #endif
    float amplitude = 0.8;
    #ifdef AMPLITUDE
        amplitude = m_Amplitude;
    #endif

    float sinOff = (texCoord.x + texCoord.y) * waveLength;
    float tm = -g_Time * 5.0;

    #ifdef TIME
        tm = -m_Time;
    #endif

    float fx = texCoord.x;
    float fy = texCoord.x * texCoord.y;

    position.z += (sin(tm * 1.45 + sinOff) * fx * 0.5) * amplitude;
    position.x  = (sin(tm * 3.12 + sinOff) * fx * 0.5 - fy * 0.9) * amplitude;
    position.y -= (sin(tm * 2.20 + sinOff) * fx * 0.2) * amplitude; 

    gl_Position = g_WorldViewProjectionMatrix * position; 
}
