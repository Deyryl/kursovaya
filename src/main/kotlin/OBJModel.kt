package org.example

import org.joml.Matrix4f
import org.joml.Vector3f

class OBJModel(
    private val meshes: List<Mesh>,
    private var position: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
    private var rotation: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
    private var scale: Vector3f = Vector3f(1.0f, 1.0f, 1.0f)
) {
    
    fun draw(shader: Shader) {

        val modelMatrix = getModelMatrix()
        shader.setMat4("model", modelMatrix)
        

        meshes.forEach { mesh ->
            mesh.draw(shader)
        }
    }
    
    fun getModelMatrix(): Matrix4f {
        val model = Matrix4f()
        model.translate(position)
        model.rotateX(Math.toRadians(rotation.x.toDouble()).toFloat())
        model.rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
        model.rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())
        model.scale(scale)
        return model
    }
    
    fun cleanup() {
        meshes.forEach { mesh ->
            mesh.cleanup()
        }
    }
    

    fun getPosition(): Vector3f = Vector3f(position)
    fun setPosition(pos: Vector3f) { position = Vector3f(pos) }
    
    fun getRotation(): Vector3f = Vector3f(rotation)
    fun setRotation(rot: Vector3f) { rotation = Vector3f(rot) }
    
    fun getScale(): Vector3f = Vector3f(scale)
    fun setScale(scl: Vector3f) { scale = Vector3f(scl) }
    
    fun translate(translation: Vector3f) {
        position.add(translation)
    }
    
    fun rotate(rotation: Vector3f) {
        this.rotation.add(rotation)
    }
    
    fun scale(scale: Vector3f) {
        this.scale.mul(scale)
    }
    
    fun getMeshCount(): Int = meshes.size
    
    fun getMeshes(): List<Mesh> = meshes
}
