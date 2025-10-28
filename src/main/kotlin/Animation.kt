package org.example

abstract class Animation(
    private val duration: Float,
    private val startTime: Float = 0.0f
) {
    protected var active: Boolean = false
    protected var finished: Boolean = false
    private var actualStartTime: Float = 0.0f
    
    fun start() {
        active = true
        finished = false
        actualStartTime = System.currentTimeMillis() / 1000.0f
    }
    
    fun stop() {
        active = false
    }
    
    fun isActive(): Boolean = active
    fun isFinished(): Boolean = finished
    
    protected fun getProgress(): Float {
        if (!active) return 0.0f
        val elapsed = (System.currentTimeMillis() / 1000.0f) - actualStartTime
        val progress = (elapsed / duration).coerceIn(0.0f, 1.0f)
        if (progress >= 1.0f) {
            finished = true
            active = false
        }
        return progress
    }
    
    abstract fun updateAnimation()
}
