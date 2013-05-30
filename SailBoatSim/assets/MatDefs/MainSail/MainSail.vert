attribute vec3 inPosition;
attribute vec2 inTexCoord;

uniform mat4 g_WorldViewProjectionMatrix;

varying vec2 texCoord;

uniform vec3 m_Flag;
uniform vec2 m_Sail;

/*
* vertex shader to animate a flag
*/
void main() { 
    // Vertex transformation
    texCoord = inTexCoord;
    vec4 position = vec4(inPosition, 1.0);

    float awa = m_Sail.x;
    float minAwa = m_Sail.y;

    if (abs(awa) < minAwa) {
        float waveLength = m_Flag.x;
        float amplitude = m_Flag.y;
        float tm = m_Flag.z;

        float sinOff = (texCoord.x + texCoord.y) * waveLength;

        float fx = texCoord.x * texCoord.y;
        float fy = texCoord.x * texCoord.y;

        position.z += (sin(tm * 1.45 + sinOff) * fx * 0.5) * amplitude;
        position.x  = (sin(tm * 3.12 + sinOff) * fx * 0.5 - fy * 0.9) * amplitude;
        position.y -= (sin(tm * 2.20 + sinOff) * fx * 0.2) * amplitude;
    } else {
        float curv = (abs(awa/180.0*3.14159265)*8.0+6.0) / 80.0;
        position.x = -sign(awa) * curv * sin(texCoord.x*texCoord.y*3.14159265);
    }
    

    gl_Position = g_WorldViewProjectionMatrix * position; 
}
