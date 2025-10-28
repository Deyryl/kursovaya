package org.example

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class Camera(
    private var position: Vector3f = Vector3f(0.0f, 0.0f, 3.0f),
    private var worldUp: Vector3f = Vector3f(0.0f, 1.0f, 0.0f),
    private var yaw: Float = -90.0f,
    private var pitch: Float = 0.0f
) {

    private var front: Vector3f = Vector3f(0.0f, 0.0f, -1.0f)
    private var right: Vector3f = Vector3f()
    private var up: Vector3f = Vector3f()
    

    private var movementSpeed: Float = 2.5f
    private var mouseSensitivity: Float = 0.1f
    private var zoom: Float = 45.0f
    

    private var firstMouse: Boolean = true
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    
    init {
        updateCameraVectors()
    }
    
    fun getViewMatrix(): Matrix4f {
        val view = Matrix4f()
        val center = Vector3f(position).add(front)
        view.lookAt(position, center, up)
        return view
    }
    
    fun getProjectionMatrix(width: Int, height: Int): Matrix4f {
        val projection = Matrix4f()
        projection.perspective(
            Math.toRadians(zoom.toDouble()).toFloat(),
            width.toFloat() / height.toFloat(),
            0.1f,
            100.0f
        )
        return projection
    }
    
    fun processKeyboard(direction: CameraMovement, deltaTime: Float) {
        val velocity = movementSpeed * deltaTime
        
        when (direction) {
            CameraMovement.FORWARD -> position.add(Vector3f(front).mul(velocity))
            CameraMovement.BACKWARD -> position.sub(Vector3f(front).mul(velocity))
            CameraMovement.LEFT -> position.sub(Vector3f(right).mul(velocity))
            CameraMovement.RIGHT -> position.add(Vector3f(right).mul(velocity))
            CameraMovement.UP -> position.add(Vector3f(worldUp).mul(velocity))
            CameraMovement.DOWN -> position.sub(Vector3f(worldUp).mul(velocity))
        }
    }
    
    fun processMouseMovement(xPos: Float, yPos: Float, constrainPitch: Boolean = true) {
        if (firstMouse) {
            lastX = xPos
            lastY = yPos
            firstMouse = false
        }
        
        val xOffset = xPos - lastX
        val yOffset = lastY - yPos
        
        lastX = xPos
        lastY = yPos
        
        val xOffsetScaled = xOffset * mouseSensitivity
        val yOffsetScaled = yOffset * mouseSensitivity
        
        yaw += xOffsetScaled
        pitch += yOffsetScaled
        

        if (constrainPitch) {
            pitch = pitch.coerceIn(-89.0f, 89.0f)
        }
        
        updateCameraVectors()
    }
    
    fun processMouseScroll(yOffset: Float) {
        zoom -= yOffset
        zoom = zoom.coerceIn(1.0f, 45.0f)
    }
    
    fun initMousePosition(xPos: Float, yPos: Float) {
        lastX = xPos
        lastY = yPos
        firstMouse = true
    }
    
    private fun updateCameraVectors() {

        val front = Vector3f()
        front.x = Math.cos(Math.toRadians(yaw.toDouble())).toFloat() * 
                  Math.cos(Math.toRadians(pitch.toDouble())).toFloat()
        front.y = Math.sin(Math.toRadians(pitch.toDouble())).toFloat()
        front.z = Math.sin(Math.toRadians(yaw.toDouble())).toFloat() * 
                  Math.cos(Math.toRadians(pitch.toDouble())).toFloat()
        front.normalize()
        this.front = front
        

        this.right = Vector3f(front).cross(worldUp).normalize()
        this.up = Vector3f(right).cross(front).normalize()
    }
    

    fun getPosition(): Vector3f = Vector3f(position)
    fun getFront(): Vector3f = Vector3f(front)
    fun getRight(): Vector3f = Vector3f(right)
    fun getUp(): Vector3f = Vector3f(up)
    fun getZoom(): Float = zoom
    

    fun setMovementSpeed(speed: Float) { movementSpeed = speed }
    fun setMouseSensitivity(sensitivity: Float) { mouseSensitivity = sensitivity }
    fun setPosition(pos: Vector3f) { 
        position = Vector3f(pos)
        updateCameraVectors()
    }
}

enum class CameraMovement {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UP,
    DOWN
}
