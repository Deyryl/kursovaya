package org.example

import org.joml.Vector3f

sealed class Light {
    abstract fun applyToShader(shader: Shader, index: Int = 0)
}

class DirectionalLight(
    var direction: Vector3f = Vector3f(-0.2f, -1.0f, -0.3f),
    var ambient: Vector3f = Vector3f(0.05f, 0.05f, 0.05f),
    var diffuse: Vector3f = Vector3f(0.4f, 0.4f, 0.4f),
    var specular: Vector3f = Vector3f(0.5f, 0.5f, 0.5f)
) : Light() {
    
    override fun applyToShader(shader: Shader, index: Int) {
        shader.setVec3("dirLight.direction", direction)
        shader.setVec3("dirLight.ambient", ambient)
        shader.setVec3("dirLight.diffuse", diffuse)
        shader.setVec3("dirLight.specular", specular)
    }
}

class PointLight(
    var position: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
    var ambient: Vector3f = Vector3f(0.05f, 0.05f, 0.05f),
    var diffuse: Vector3f = Vector3f(0.8f, 0.8f, 0.8f),
    var specular: Vector3f = Vector3f(1.0f, 1.0f, 1.0f),
    var constant: Float = 1.0f,
    var linear: Float = 0.09f,
    var quadratic: Float = 0.032f
) : Light() {
    
    override fun applyToShader(shader: Shader, index: Int) {
        shader.setVec3("pointLights[$index].position", position)
        shader.setVec3("pointLights[$index].ambient", ambient)
        shader.setVec3("pointLights[$index].diffuse", diffuse)
        shader.setVec3("pointLights[$index].specular", specular)
        shader.setFloat("pointLights[$index].constant", constant)
        shader.setFloat("pointLights[$index].linear", linear)
        shader.setFloat("pointLights[$index].quadratic", quadratic)
    }
}

class SpotLight(
    var position: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
    var direction: Vector3f = Vector3f(0.0f, 0.0f, -1.0f),
    var ambient: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
    var diffuse: Vector3f = Vector3f(1.0f, 1.0f, 1.0f),
    var specular: Vector3f = Vector3f(1.0f, 1.0f, 1.0f),
    var constant: Float = 1.0f,
    var linear: Float = 0.09f,
    var quadratic: Float = 0.032f,
    var cutOff: Float = 12.5f,
    var outerCutOff: Float = 17.5f
) : Light() {
    
    override fun applyToShader(shader: Shader, index: Int) {
        shader.setVec3("spotLights[$index].position", position)
        shader.setVec3("spotLights[$index].direction", direction)
        shader.setVec3("spotLights[$index].ambient", ambient)
        shader.setVec3("spotLights[$index].diffuse", diffuse)
        shader.setVec3("spotLights[$index].specular", specular)
        shader.setFloat("spotLights[$index].constant", constant)
        shader.setFloat("spotLights[$index].linear", linear)
        shader.setFloat("spotLights[$index].quadratic", quadratic)
        shader.setFloat("spotLights[$index].cutOff", Math.cos(Math.toRadians(cutOff.toDouble())).toFloat())
        shader.setFloat("spotLights[$index].outerCutOff", Math.cos(Math.toRadians(outerCutOff.toDouble())).toFloat())
    }
}

class LightingManager {
    private val directionalLights = mutableListOf<DirectionalLight>()
    private val pointLights = mutableListOf<PointLight>()
    private val spotLights = mutableListOf<SpotLight>()
    
    fun addDirectionalLight(light: DirectionalLight) {
        directionalLights.add(light)
    }
    
    fun addPointLight(light: PointLight) {
        pointLights.add(light)
    }
    
    fun addSpotLight(light: SpotLight) {
        spotLights.add(light)
    }
    
    fun applyToShader(shader: Shader) {

        shader.setInt("numDirLights", directionalLights.size)
        directionalLights.forEachIndexed { index, light ->
            light.applyToShader(shader, index)
        }
        

        shader.setInt("numPointLights", pointLights.size)
        pointLights.forEachIndexed { index, light ->
            light.applyToShader(shader, index)
        }
        

        shader.setInt("numSpotLights", spotLights.size)
        spotLights.forEachIndexed { index, light ->
            light.applyToShader(shader, index)
        }
    }
    
    fun getPointLights(): List<PointLight> = pointLights.toList()
    
    fun getSpotLights(): List<SpotLight> = spotLights.toList()
    
    fun getDirectionalLights(): List<DirectionalLight> = directionalLights.toList()
    
    fun clear() {
        directionalLights.clear()
        pointLights.clear()
        spotLights.clear()
    }
}
