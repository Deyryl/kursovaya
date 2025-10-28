package org.example

import org.joml.Vector3f

class Material(
    var ambient: Vector3f = Vector3f(1.0f, 1.0f, 1.0f),
    var diffuse: Vector3f = Vector3f(1.0f, 1.0f, 1.0f),
    var specular: Vector3f = Vector3f(1.0f, 1.0f, 1.0f),
    var shininess: Float = 32.0f,
    var diffuseTexture: Int = 0,
    var specularTexture: Int = 0,
    var normalTexture: Int = 0,
    var hasDiffuseTexture: Boolean = false,
    var hasSpecularTexture: Boolean = false,
    var hasNormalTexture: Boolean = false
) {
    
    companion object {

        val EMERALD = Material(
            Vector3f(0.0215f, 0.1745f, 0.0215f),
            Vector3f(0.07568f, 0.61424f, 0.07568f),
            Vector3f(0.633f, 0.727811f, 0.633f),
            0.6f
        )
        
        val JADE = Material(
            Vector3f(0.135f, 0.2225f, 0.1575f),
            Vector3f(0.54f, 0.89f, 0.63f),
            Vector3f(0.316228f, 0.316228f, 0.316228f),
            0.1f
        )
        
        val OBSIDIAN = Material(
            Vector3f(0.05375f, 0.05f, 0.06625f),
            Vector3f(0.18275f, 0.17f, 0.22525f),
            Vector3f(0.332741f, 0.328634f, 0.346435f),
            0.3f
        )
        
        val PEARL = Material(
            Vector3f(0.25f, 0.20725f, 0.20725f),
            Vector3f(1.0f, 0.829f, 0.829f),
            Vector3f(0.296648f, 0.296648f, 0.296648f),
            0.088f
        )
        
        val RUBY = Material(
            Vector3f(0.1745f, 0.01175f, 0.01175f),
            Vector3f(0.61424f, 0.04136f, 0.04136f),
            Vector3f(0.727811f, 0.626959f, 0.626959f),
            0.6f
        )
        
        val TURQUOISE = Material(
            Vector3f(0.1f, 0.18725f, 0.1745f),
            Vector3f(0.396f, 0.74151f, 0.69102f),
            Vector3f(0.297254f, 0.30829f, 0.306678f),
            0.1f
        )
        
        val BRASS = Material(
            Vector3f(0.329412f, 0.223529f, 0.027451f),
            Vector3f(0.780392f, 0.568627f, 0.113725f),
            Vector3f(0.992157f, 0.941176f, 0.807843f),
            0.21794872f
        )
        
        val BRONZE = Material(
            Vector3f(0.2125f, 0.1275f, 0.054f),
            Vector3f(0.714f, 0.4284f, 0.18144f),
            Vector3f(0.393548f, 0.271906f, 0.166721f),
            0.2f
        )
        
        val CHROME = Material(
            Vector3f(0.25f, 0.25f, 0.25f),
            Vector3f(0.4f, 0.4f, 0.4f),
            Vector3f(0.774597f, 0.774597f, 0.774597f),
            0.6f
        )
        
        val COPPER = Material(
            Vector3f(0.19125f, 0.0735f, 0.0225f),
            Vector3f(0.7038f, 0.27048f, 0.0828f),
            Vector3f(0.256777f, 0.137622f, 0.086014f),
            0.1f
        )
        
        val GOLD = Material(
            Vector3f(0.24725f, 0.1995f, 0.0745f),
            Vector3f(0.75164f, 0.60648f, 0.22648f),
            Vector3f(0.628281f, 0.555802f, 0.366065f),
            0.4f
        )
        
        val SILVER = Material(
            Vector3f(0.19225f, 0.19225f, 0.19225f),
            Vector3f(0.50754f, 0.50754f, 0.50754f),
            Vector3f(0.508273f, 0.508273f, 0.508273f),
            0.4f
        )
        
        val BLACK_PLASTIC = Material(
            Vector3f(0.0f, 0.0f, 0.0f),
            Vector3f(0.01f, 0.01f, 0.01f),
            Vector3f(0.50f, 0.50f, 0.50f),
            0.25f
        )
        
        val CYAN_PLASTIC = Material(
            Vector3f(0.0f, 0.1f, 0.06f),
            Vector3f(0.0f, 0.50980392f, 0.50980392f),
            Vector3f(0.50196078f, 0.50196078f, 0.50196078f),
            0.25f
        )
        
        val GREEN_PLASTIC = Material(
            Vector3f(0.0f, 0.0f, 0.0f),
            Vector3f(0.1f, 0.35f, 0.1f),
            Vector3f(0.45f, 0.55f, 0.45f),
            0.25f
        )
        
        val RED_PLASTIC = Material(
            Vector3f(0.0f, 0.0f, 0.0f),
            Vector3f(0.5f, 0.0f, 0.0f),
            Vector3f(0.7f, 0.6f, 0.6f),
            0.25f
        )
        
        val WHITE_PLASTIC = Material(
            Vector3f(0.0f, 0.0f, 0.0f),
            Vector3f(0.55f, 0.55f, 0.55f),
            Vector3f(0.70f, 0.70f, 0.70f),
            0.25f
        )
        
        val GRAY_PLASTIC = Material(
            Vector3f(0.1f, 0.1f, 0.1f),
            Vector3f(0.3f, 0.3f, 0.3f),
            Vector3f(0.5f, 0.5f, 0.5f),
            0.25f
        )
        
        val YELLOW_PLASTIC = Material(
            Vector3f(0.0f, 0.0f, 0.0f),
            Vector3f(0.5f, 0.5f, 0.0f),
            Vector3f(0.60f, 0.60f, 0.50f),
            0.25f
        )
        
        val BLACK_RUBBER = Material(
            Vector3f(0.02f, 0.02f, 0.02f),
            Vector3f(0.01f, 0.01f, 0.01f),
            Vector3f(0.4f, 0.4f, 0.4f),
            0.078125f
        )
        
        val CYAN_RUBBER = Material(
            Vector3f(0.0f, 0.05f, 0.05f),
            Vector3f(0.4f, 0.5f, 0.5f),
            Vector3f(0.04f, 0.7f, 0.7f),
            0.078125f
        )
        
        val GREEN_RUBBER = Material(
            Vector3f(0.0f, 0.05f, 0.0f),
            Vector3f(0.4f, 0.5f, 0.4f),
            Vector3f(0.04f, 0.7f, 0.04f),
            0.078125f
        )
        
        val RED_RUBBER = Material(
            Vector3f(0.05f, 0.0f, 0.0f),
            Vector3f(0.5f, 0.4f, 0.4f),
            Vector3f(0.7f, 0.04f, 0.04f),
            0.078125f
        )
        
        val WHITE_RUBBER = Material(
            Vector3f(0.05f, 0.05f, 0.05f),
            Vector3f(0.5f, 0.5f, 0.5f),
            Vector3f(0.7f, 0.7f, 0.7f),
            0.078125f
        )
        
        val YELLOW_RUBBER = Material(
            Vector3f(0.05f, 0.05f, 0.0f),
            Vector3f(0.5f, 0.5f, 0.4f),
            Vector3f(0.7f, 0.7f, 0.04f),
            0.078125f
        )
    }
    
    fun applyToShader(shader: Shader, prefix: String = "material.") {
        shader.setVec3("${prefix}ambient", ambient)
        shader.setVec3("${prefix}diffuse", diffuse)
        shader.setVec3("${prefix}specular", specular)
        shader.setFloat("${prefix}shininess", shininess)
        

        shader.setBool("${prefix}hasDiffuseTexture", hasDiffuseTexture)
        shader.setBool("${prefix}hasSpecularTexture", hasSpecularTexture)
        shader.setBool("${prefix}hasNormalTexture", hasNormalTexture)
        
        if (hasDiffuseTexture) {
            shader.setInt("${prefix}diffuseTexture", 0)
        }
        if (hasSpecularTexture) {
            shader.setInt("${prefix}specularTexture", 1)
        }
        if (hasNormalTexture) {
            shader.setInt("${prefix}normalTexture", 2)
        }
    }
}
