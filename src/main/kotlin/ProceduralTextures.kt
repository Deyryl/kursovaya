package org.example

import org.lwjgl.opengl.GL33.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ProceduralTextures {
    
    companion object {
        fun createCheckerboardTexture(size: Int = 256, squares: Int = 8): Int {
            val textureID = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureID)
            
            val buffer = ByteBuffer.allocateDirect(size * size * 3).order(ByteOrder.nativeOrder())
            
            val squareSize = size / squares
            
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val squareX = x / squareSize
                    val squareY = y / squareSize
                    
                    val isWhite = (squareX + squareY) % 2 == 0
                    val color = if (isWhite) 255 else 0
                    
                    buffer.put(color.toByte())
                    buffer.put(color.toByte())
                    buffer.put(color.toByte())
                }
            }
            
            buffer.flip()
            
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGB,
                size, size, 0,
                GL_RGB, GL_UNSIGNED_BYTE, buffer
            )
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            
            glBindTexture(GL_TEXTURE_2D, 0)
            
            return textureID
        }
        
        fun createGradientTexture(size: Int = 256): Int {
            val textureID = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureID)
            
            val buffer = ByteBuffer.allocateDirect(size * size * 3).order(ByteOrder.nativeOrder())
            
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val r = (x.toFloat() / size * 255).toInt().coerceIn(0, 255)
                    val g = (y.toFloat() / size * 255).toInt().coerceIn(0, 255)
                    val b = 128
                    
                    buffer.put(r.toByte())
                    buffer.put(g.toByte())
                    buffer.put(b.toByte())
                }
            }
            
            buffer.flip()
            
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGB,
                size, size, 0,
                GL_RGB, GL_UNSIGNED_BYTE, buffer
            )
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            
            glBindTexture(GL_TEXTURE_2D, 0)
            
            return textureID
        }
        
        fun createNoiseTexture(size: Int = 256): Int {
            val textureID = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureID)
            
            val buffer = ByteBuffer.allocateDirect(size * size * 3).order(ByteOrder.nativeOrder())
            
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val noise = (Math.random() * 255).toInt()
                    
                    buffer.put(noise.toByte())
                    buffer.put(noise.toByte())
                    buffer.put(noise.toByte())
                }
            }
            
            buffer.flip()
            
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGB,
                size, size, 0,
                GL_RGB, GL_UNSIGNED_BYTE, buffer
            )
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            
            glBindTexture(GL_TEXTURE_2D, 0)
            
            return textureID
        }
        
        fun createKeyboardTexture(size: Int = 512): Int {
            val textureID = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureID)
            
            val buffer = ByteBuffer.allocateDirect(size * size * 3).order(ByteOrder.nativeOrder())
            

            val baseColor = byteArrayOf(45, 45, 50)
            val keyColor = byteArrayOf(60, 60, 65)
            val borderColor = byteArrayOf(25, 25, 30)
            
            for (y in 0 until size) {
                for (x in 0 until size) {
                    var r: Int
                    var g: Int
                    var b: Int
                    

                    val normalizedX = x.toFloat() / size
                    val normalizedY = y.toFloat() / size
                    

                    val keyWidth = 1.0f / 15.0f
                    val keyHeight = 1.0f / 5.0f
                    
                    val keyX = (normalizedX / keyWidth).toInt()
                    val keyY = (normalizedY / keyHeight).toInt()
                    
                    val localX = (normalizedX % keyWidth) / keyWidth
                    val localY = (normalizedY % keyHeight) / keyHeight
                    

                    val isBorder = localX < 0.05f || localX > 0.95f || localY < 0.05f || localY > 0.95f
                    
                    if (isBorder) {

                        r = borderColor[0].toInt() and 0xFF
                        g = borderColor[1].toInt() and 0xFF
                        b = borderColor[2].toInt() and 0xFF
                    } else {

                        r = keyColor[0].toInt() and 0xFF
                        g = keyColor[1].toInt() and 0xFF
                        b = keyColor[2].toInt() and 0xFF
                        

                        val gradient = 1.0f - (localX - 0.5f) * (localX - 0.5f) - (localY - 0.5f) * (localY - 0.5f)
                        val gradientFactor = 0.8f + gradient * 0.4f
                        
                        r = (r * gradientFactor).toInt().coerceIn(0, 255)
                        g = (g * gradientFactor).toInt().coerceIn(0, 255)
                        b = (b * gradientFactor).toInt().coerceIn(0, 255)
                    }
                    

                    val noise = (Math.random() * 0.1f + 0.95f)
                    r = (r * noise).toInt().coerceIn(0, 255)
                    g = (g * noise).toInt().coerceIn(0, 255)
                    b = (b * noise).toInt().coerceIn(0, 255)
                    

                    if (keyY == 4 || (keyY == 3 && (keyX == 0 || keyX == 14))) {
                        r = (r * 0.9).toInt()
                        g = (g * 0.9).toInt()
                        b = (b * 0.9).toInt()
                    }
                    
                    buffer.put(r.toByte())
                    buffer.put(g.toByte())
                    buffer.put(b.toByte())
                }
            }
            
            buffer.flip()
            
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGB,
                size, size, 0,
                GL_RGB, GL_UNSIGNED_BYTE, buffer
            )
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            
            glBindTexture(GL_TEXTURE_2D, 0)
            
            return textureID
        }
        
        fun createWoodTexture(size: Int = 512): Int {
            val textureID = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureID)
            
            val buffer = ByteBuffer.allocateDirect(size * size * 3).order(ByteOrder.nativeOrder())
            
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val normalizedX = x.toFloat() / size
                    val normalizedY = y.toFloat() / size
                    

                    var r = 139
                    var g = 90
                    var b = 43
                    

                    val grain = Math.sin(normalizedX * 20.0 + Math.random() * 0.5) * 0.3
                    val grainFactor = 1.0f + grain.toFloat()
                    
                    r = (r * grainFactor).toInt().coerceIn(0, 255)
                    g = (g * grainFactor).toInt().coerceIn(0, 255)
                    b = (b * grainFactor).toInt().coerceIn(0, 255)
                    

                    val variation = (Math.random() * 0.2f + 0.9f)
                    r = (r * variation).toInt().coerceIn(0, 255)
                    g = (g * variation).toInt().coerceIn(0, 255)
                    b = (b * variation).toInt().coerceIn(0, 255)
                    
                    buffer.put(r.toByte())
                    buffer.put(g.toByte())
                    buffer.put(b.toByte())
                }
            }
            
            buffer.flip()
            
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGB,
                size, size, 0,
                GL_RGB, GL_UNSIGNED_BYTE, buffer
            )
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            
            glBindTexture(GL_TEXTURE_2D, 0)
            
            return textureID
        }
        
        fun createMetalTexture(size: Int = 512): Int {
            val textureID = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureID)
            
            val buffer = ByteBuffer.allocateDirect(size * size * 3).order(ByteOrder.nativeOrder())
            
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val normalizedX = x.toFloat() / size
                    val normalizedY = y.toFloat() / size
                    

                    var r = 120
                    var g = 120
                    var b = 130
                    

                    val highlight = Math.sin(normalizedX * 30.0) * Math.cos(normalizedY * 30.0) * 0.3
                    val highlightFactor = 1.0f + highlight.toFloat()
                    
                    r = (r * highlightFactor).toInt().coerceIn(0, 255)
                    g = (g * highlightFactor).toInt().coerceIn(0, 255)
                    b = (b * highlightFactor).toInt().coerceIn(0, 255)
                    

                    val roughness = (Math.random() * 0.15f + 0.925f)
                    r = (r * roughness).toInt().coerceIn(0, 255)
                    g = (g * roughness).toInt().coerceIn(0, 255)
                    b = (b * roughness).toInt().coerceIn(0, 255)
                    
                    buffer.put(r.toByte())
                    buffer.put(g.toByte())
                    buffer.put(b.toByte())
                }
            }
            
            buffer.flip()
            
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGB,
                size, size, 0,
                GL_RGB, GL_UNSIGNED_BYTE, buffer
            )
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            
            glBindTexture(GL_TEXTURE_2D, 0)
            
            return textureID
        }
        
        fun createTableWoodTexture(size: Int = 512): Int {
            val textureID = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureID)
            
            val buffer = ByteBuffer.allocateDirect(size * size * 3).order(ByteOrder.nativeOrder())
            
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val normalizedX = x.toFloat() / size
                    val normalizedY = y.toFloat() / size
                    

                    var r = 101
                    var g = 67
                    var b = 33
                    

                    val grain1 = Math.sin(normalizedX * 15.0 + Math.random() * 0.3) * 0.4
                    val grain2 = Math.sin(normalizedX * 8.0 + normalizedY * 2.0 + Math.random() * 0.2) * 0.2
                    val grainFactor = 1.0f + grain1.toFloat() + grain2.toFloat()
                    
                    r = (r * grainFactor).toInt().coerceIn(0, 255)
                    g = (g * grainFactor).toInt().coerceIn(0, 255)
                    b = (b * grainFactor).toInt().coerceIn(0, 255)
                    

                    val centerX = 0.5f
                    val centerY = 0.5f
                    val distance = Math.sqrt(((normalizedX - centerX) * (normalizedX - centerX) + (normalizedY - centerY) * (normalizedY - centerY)).toDouble())
                    val rings = Math.sin(distance * 20.0) * 0.1
                    val ringFactor = 1.0f + rings.toFloat()
                    
                    r = (r * ringFactor).toInt().coerceIn(0, 255)
                    g = (g * ringFactor).toInt().coerceIn(0, 255)
                    b = (b * ringFactor).toInt().coerceIn(0, 255)
                    

                    val variation = (Math.random() * 0.15f + 0.925f)
                    r = (r * variation).toInt().coerceIn(0, 255)
                    g = (g * variation).toInt().coerceIn(0, 255)
                    b = (b * variation).toInt().coerceIn(0, 255)
                    

                    val scratches = if (Math.random() < 0.02) 0.8f else 1.0f
                    r = (r * scratches).toInt().coerceIn(0, 255)
                    g = (g * scratches).toInt().coerceIn(0, 255)
                    b = (b * scratches).toInt().coerceIn(0, 255)
                    
                    buffer.put(r.toByte())
                    buffer.put(g.toByte())
                    buffer.put(b.toByte())
                }
            }
            
            buffer.flip()
            
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGB,
                size, size, 0,
                GL_RGB, GL_UNSIGNED_BYTE, buffer
            )
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            
            glBindTexture(GL_TEXTURE_2D, 0)
            
            return textureID
        }
    }
}
