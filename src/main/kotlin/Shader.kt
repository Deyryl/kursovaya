package org.example

import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class Shader(vertexPath: String, fragmentPath: String) {
    private val programID: Int
    
    init {

        val vertexShader = loadShader(vertexPath, GL_VERTEX_SHADER)
        val fragmentShader = loadShader(fragmentPath, GL_FRAGMENT_SHADER)
        

        programID = glCreateProgram()
        glAttachShader(programID, vertexShader)
        glAttachShader(programID, fragmentShader)
        glLinkProgram(programID)
        

        val success = MemoryStack.stackPush().use { stack ->
            val pSuccess = stack.mallocInt(1)
            glGetProgramiv(programID, GL_LINK_STATUS, pSuccess)
            pSuccess[0] != 0
        }
        
        if (!success) {
            val infoLog = glGetProgramInfoLog(programID)
            glDeleteProgram(programID)
            throw RuntimeException("Ошибка линковки шейдеров: $infoLog")
        }
        

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }
    
    private fun loadShader(path: String, type: Int): Int {
        val shaderSource = try {
            String(Files.readAllBytes(Paths.get(path)))
        } catch (e: IOException) {
            throw RuntimeException("Не удалось загрузить шейдер: $path", e)
        }
        
        val shader = glCreateShader(type)
        glShaderSource(shader, shaderSource)
        glCompileShader(shader)
        

        val success = MemoryStack.stackPush().use { stack ->
            val pSuccess = stack.mallocInt(1)
            glGetShaderiv(shader, GL_COMPILE_STATUS, pSuccess)
            pSuccess[0] != 0
        }
        
        if (!success) {
            val infoLog = glGetShaderInfoLog(shader)
            glDeleteShader(shader)
            throw RuntimeException("Ошибка компиляции шейдера $path: $infoLog")
        }
        
        return shader
    }
    
    fun use() {
        glUseProgram(programID)
    }
    
    fun stop() {
        glUseProgram(0)
    }
    
    fun setBool(name: String, value: Boolean) {
        val location = glGetUniformLocation(programID, name)
        glUniform1i(location, if (value) 1 else 0)
    }
    
    fun setInt(name: String, value: Int) {
        val location = glGetUniformLocation(programID, name)
        glUniform1i(location, value)
    }
    
    fun setFloat(name: String, value: Float) {
        val location = glGetUniformLocation(programID, name)
        glUniform1f(location, value)
    }
    
    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        val location = glGetUniformLocation(programID, name)
        glUniform3f(location, x, y, z)
    }
    
    fun setVec3(name: String, value: org.joml.Vector3f) {
        setVec3(name, value.x, value.y, value.z)
    }
    
    fun setMat4(name: String, value: org.joml.Matrix4f) {
        val location = glGetUniformLocation(programID, name)
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(16)
            value.get(buffer)
            glUniformMatrix4fv(location, false, buffer)
        }
    }
    
    fun cleanup() {
        glDeleteProgram(programID)
    }
}
