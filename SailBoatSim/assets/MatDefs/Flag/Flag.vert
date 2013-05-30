attribute vec3 inPosition;
attribute vec2 inTexCoord;

uniform mat4 g_WorldViewProjectionMatrix;

varying vec2 texCoord;

uniform vec3 m_Flag;

/*
* vertex shader to animate a flag
*/
void main() { 
    // Vertex transformation
    texCoord = inTexCoord;
    vec4 position = vec4(inPosition, 1.0);

    float waveLength = m_Flag.x;
    float amplitude = m_Flag.y;
    float time = m_Flag.z;

    float sinOff = (texCoord.x + texCoord.y) * waveLength;
    float tm = -time;

    float fx = texCoord.x;
    float fy = texCoord.x * texCoord.y;

    position.z += (sin(tm * 1.45 + sinOff) * fx * 0.5) * amplitude;
    position.x  = (sin(tm * 3.12 + sinOff) * fx * 0.5 - fy * 0.9) * amplitude;
    position.y -= (sin(tm * 2.20 + sinOff) * fx * 0.2) * amplitude; 

    gl_Position = g_WorldViewProjectionMatrix * position; 
}
