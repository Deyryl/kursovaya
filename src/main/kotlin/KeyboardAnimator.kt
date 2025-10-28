package org.example

import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.sin
import kotlin.math.PI

class KeyboardAnimator(private val keyboardModel: OBJModel, private val meshes: List<Mesh>) {
    
    private val animationState = KeyboardAnimationState()
    private var animationTime = 0.0f
    

    private val keyAnimations = mutableMapOf<Int, Animation>()
    private var nextKeyAnimationId = 0
    

    private val keyIndices = mapOf(

        0 to 1, 1 to 2, 2 to 3, 3 to 4, 4 to 5, 5 to 6, 6 to 7, 7 to 8, 8 to 9, 9 to 10, 10 to 11, 11 to 12,

        12 to 13, 13 to 14, 14 to 15, 15 to 16, 16 to 17, 17 to 18, 18 to 19, 19 to 20, 20 to 21, 21 to 22, 22 to 23, 23 to 24, 24 to 25, 25 to 26
    )
    
    fun update(deltaTime: Float) {
        animationTime += deltaTime
        

        if (animationState.isFloating) {
            updateFloatingAnimation()
        }
        

        if (animationState.isRotating) {
            updateRotationAnimation()
        }
        

        if (animationState.isScaling) {
            updateScalingAnimation()
        }
        

        updateKeyAnimations(deltaTime)
    }
    
    private fun updateFloatingAnimation() {
        val floatAmount = sin(animationTime * 2.0f) * 0.1f
        val currentPos = keyboardModel.getPosition()
        currentPos.y = animationState.originalPosition.y + floatAmount
        keyboardModel.setPosition(currentPos)
    }
    
    private fun updateRotationAnimation() {
        val rotationAmount = animationTime * animationState.rotationSpeed
        val currentRotation = keyboardModel.getRotation()
        currentRotation.y = rotationAmount % 360.0f
        keyboardModel.setRotation(currentRotation)
    }
    
    private fun updateScalingAnimation() {
        val scaleAmount = 1.0f + sin(animationTime * 3.0f) * 0.05f
        val currentScale = keyboardModel.getScale()
        currentScale.set(animationState.originalScale).mul(scaleAmount)
        keyboardModel.setScale(currentScale)
    }
    
    fun setFloating(enabled: Boolean) {
        if (enabled && !animationState.isFloating) {
            animationState.originalPosition.set(keyboardModel.getPosition())
        }
        animationState.isFloating = enabled
    }
    
    fun setRotating(enabled: Boolean, speed: Float = 10.0f) {
        animationState.isRotating = enabled
        animationState.rotationSpeed = speed
    }
    
    fun setScaling(enabled: Boolean) {
        if (enabled && !animationState.isScaling) {
            animationState.originalScale.set(keyboardModel.getScale())
        }
        animationState.isScaling = enabled
    }
    
    fun getAnimationState(): KeyboardAnimationState = animationState
    
    fun resetAnimations() {
        animationState.isFloating = false
        animationState.isRotating = false
        animationState.isScaling = false
        

        keyboardModel.setPosition(animationState.originalPosition)
        keyboardModel.setRotation(Vector3f(0.0f, 0.0f, 0.0f))
        keyboardModel.setScale(animationState.originalScale)
    }
    
    fun animateKeyPress(keyIndex: Int) {
        val meshIndex = keyIndices[keyIndex] ?: return
        if (meshIndex >= meshes.size) return
        
        val animationId = nextKeyAnimationId++
        val animation = KeyPressAnimation(meshes[meshIndex], keyIndex, 0.3f, 0.0f)
        animation.start()
        keyAnimations[animationId] = animation
    }
    
    fun animateMultipleKeys(keyIndices: List<Int>, delay: Float = 0.05f) {
        keyIndices.forEachIndexed { index, keyIndex ->

            val delayedStartTime = index * delay
            val meshIndex = this.keyIndices[keyIndex] ?: return@forEachIndexed
            if (meshIndex >= meshes.size) return@forEachIndexed
            
            val animationId = nextKeyAnimationId++
            val animation = DelayedKeyPressAnimation(meshes[meshIndex], keyIndex, 0.3f, delayedStartTime)
            animation.start()
            keyAnimations[animationId] = animation
        }
    }
    
    private fun updateKeyAnimations(deltaTime: Float) {
        val iterator = keyAnimations.iterator()
        while (iterator.hasNext()) {
            val (id, animation) = iterator.next()
            animation.updateAnimation()
            if (animation.isFinished()) {
                iterator.remove()
            }
        }
    }
}

data class KeyboardAnimationState(
    var isFloating: Boolean = false,
    var isRotating: Boolean = false,
    var isScaling: Boolean = false,
    var rotationSpeed: Float = 10.0f,
    var originalPosition: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
    var originalScale: Vector3f = Vector3f(1.0f, 1.0f, 1.0f)
)

class LargeKeyPressAnimation(
    private val keyboardModel: OBJModel,
    private val keyIndex: Int,
    duration: Float = 0.2f,
    startTime: Float = 0.0f
) : Animation(duration, startTime) {
    
    private val pressDistance = 0.05f
    private val originalPosition = Vector3f()
    
    init {
        originalPosition.set(keyboardModel.getPosition())
    }
    
    override fun updateAnimation() {
        val progress = getProgress()
        

        val pressAmount = sin(progress * PI.toFloat()) * pressDistance
        

        val currentPos = keyboardModel.getPosition()
        currentPos.y = originalPosition.y - pressAmount
        keyboardModel.setPosition(currentPos)
    }
}

class LargeKeyHighlightAnimation(
    private val keyboardModel: OBJModel,
    private val keyIndex: Int,
    duration: Float = 1.0f,
    startTime: Float = 0.0f
) : Animation(duration, startTime) {
    
    private val originalScale = Vector3f()
    
    init {
        originalScale.set(keyboardModel.getScale())
    }
    
    override fun updateAnimation() {
        val progress = getProgress()
        

        val scaleAmount = 1.0f + sin(progress * PI.toFloat() * 2) * 0.1f
        

        val currentScale = keyboardModel.getScale()
        currentScale.set(originalScale)
        currentScale.mul(scaleAmount)
        keyboardModel.setScale(currentScale)
    }
}

class KeyPressAnimation(
    private val mesh: Mesh,
    private val keyIndex: Int,
    duration: Float = 0.3f,
    startTime: Float = 0.0f
) : Animation(duration, startTime) {
    
    private val pressDistance = 0.05f
    private val originalVertices = mutableListOf<Float>()
    
    init {

        val vertices = mesh.getVertices()
        originalVertices.addAll(vertices.toList())
    }
    
    override fun updateAnimation() {
        val progress = getProgress()
        

        val pressAmount = sin(progress * PI.toFloat()) * pressDistance
        

        val vertices = mesh.getVertices()
        for (i in vertices.indices step 3) {
            if (i + 1 < vertices.size) {
                vertices[i + 1] = originalVertices[i + 1] - pressAmount
            }
        }
        

        mesh.updateVertices(vertices)
        
    }
}

class DelayedKeyPressAnimation(
    private val mesh: Mesh,
    private val keyIndex: Int,
    private val animationDuration: Float = 0.3f,
    private val delay: Float = 0.0f
) : Animation(animationDuration, 0.0f) {
    
    private val pressDistance = 0.05f
    private val originalVertices = mutableListOf<Float>()
    private var actualStartTime: Float = 0.0f
    private var hasStarted = false
    
    init {

        val vertices = mesh.getVertices()
        originalVertices.addAll(vertices.toList())
    }
    
    override fun updateAnimation() {
        if (!hasStarted) {
            actualStartTime = System.currentTimeMillis() / 1000.0f + delay
            hasStarted = true
        }
        
        val currentTime = System.currentTimeMillis() / 1000.0f
        if (currentTime < actualStartTime) return
        
        val elapsed = currentTime - actualStartTime
        val progress = (elapsed / animationDuration).coerceIn(0.0f, 1.0f)
        
        if (progress >= 1.0f) {
            finished = true
            active = false
        }
        

        val pressAmount = sin(progress * PI.toFloat()) * pressDistance
        

        val vertices = mesh.getVertices()
        for (i in vertices.indices step 3) {
            if (i + 1 < vertices.size) {
                vertices[i + 1] = originalVertices[i + 1] - pressAmount
            }
        }
        

        mesh.updateVertices(vertices)
    }
}
