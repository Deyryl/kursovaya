package org.example

import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(
    vertices: FloatArray,
    indices: IntArray,
    private val material: Material = Material.WHITE_PLASTIC
) {
    private val vao: Int
    private val vbo: Int
    private val ebo: Int
    val indexCount: Int = indices.size
    val vertexCount: Int = vertices.size / 8
    
    init {

        vao = glGenVertexArrays()
        glBindVertexArray(vao)
        

        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(vertices.size)
            buffer.put(vertices)
            buffer.flip()
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)
        }
        

        ebo = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocInt(indices.size)
            buffer.put(indices)
            buffer.flip()
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)
        }
        


        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0L)
        glEnableVertexAttribArray(0)
        

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4L)
        glEnableVertexAttribArray(1)
        

        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4L)
        glEnableVertexAttribArray(2)
        

        glBindVertexArray(0)
    }
    
    fun draw(shader: Shader) {

        material.applyToShader(shader)
        

        if (material.hasDiffuseTexture) {
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, material.diffuseTexture)
        }
        
        if (material.hasSpecularTexture) {
            glActiveTexture(GL_TEXTURE1)
            glBindTexture(GL_TEXTURE_2D, material.specularTexture)
        }
        
        if (material.hasNormalTexture) {
            glActiveTexture(GL_TEXTURE2)
            glBindTexture(GL_TEXTURE_2D, material.normalTexture)
        }
        

        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
        

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, 0)
    }
    
    
    fun cleanup() {
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        glDeleteBuffers(ebo)
    }
    
    fun getMaterial(): Material = material
    
    fun getVertices(): FloatArray {
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        val vertices = FloatArray(vertexCount * 8)
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(vertices.size)
            glGetBufferSubData(GL_ARRAY_BUFFER, 0, buffer)
            buffer.get(vertices)
        }
        return vertices
    }
    
    fun updateVertices(vertices: FloatArray) {
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(vertices.size)
            buffer.put(vertices)
            buffer.flip()
            glBufferSubData(GL_ARRAY_BUFFER, 0, buffer)
        }
    }
}
