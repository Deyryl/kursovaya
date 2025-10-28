package org.example

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.joml.Vector3f
import java.io.File

fun main() {
    val app = KeyboardRenderer()
    app.run()
}

class KeyboardRenderer {
    private var window: Long = 0
    private var camera: Camera = Camera()
    private var keyboardModel: OBJModel? = null
    private var keyboardAnimator: KeyboardAnimator? = null
    private var shader: Shader? = null
    private var lightingManager: LightingManager = LightingManager()
    
    private val width = 1920
    private val height = 1080
    private val title = "3D Клавиатура - Курсовая работа"
    
    private var lastFrameTime = 0.0
    private var deltaTime = 0.0
    
    private var currentTextureType = 0
    private val textureTypes = listOf("Пластик", "Дерево", "Металл")
    private var keyboardTexture: Int = 0
    
    private var tableModel: OBJModel? = null
    private var tableTexture: Int = 0
    
    private var lightAnimationTime = 0.0f
    private var dynamicLightPosition = org.joml.Vector3f(0.0f, 5.0f, 0.0f)
    
    private var keyTWasPressed = false
    private var key1WasPressed = false
    private var key2WasPressed = false
    private var key3WasPressed = false
    private var key4WasPressed = false
    
    fun run() {
        init()
        loop()
        cleanup()
    }
    
    private fun init() {

        GLFWErrorCallback.createPrint(System.err).set()
        

        if (!glfwInit()) {
            throw RuntimeException("Не удалось инициализировать GLFW")
        }
        

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        

        window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (window == MemoryUtil.NULL) {
            throw RuntimeException("Не удалось создать окно GLFW")
        }
        

        glfwMakeContextCurrent(window)
        GL.createCapabilities()
        

        setupCallbacks()
        

        setupOpenGL()
        

        loadResources()
        

        glfwShowWindow(window)
        
    }
    
    private fun setupCallbacks() {

        glfwSetFramebufferSizeCallback(window) { _, w, h ->
            glViewport(0, 0, w, h)
        }
        

        glfwSetKeyCallback(window) { _, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true)
            }
        }
        

        glfwSetCursorPosCallback(window) { _, xPos, yPos ->
            camera.processMouseMovement(xPos.toFloat(), yPos.toFloat())
        }
        

        glfwSetScrollCallback(window) { _, _, yOffset ->
            camera.processMouseScroll(yOffset.toFloat())
        }
        

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
    }
    
    private fun setupOpenGL() {

        glEnable(GL_DEPTH_TEST)
        

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        

        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
        

        glClearColor(0.5f, 0.8f, 1.0f, 1.0f) 
    }
    
    private fun loadResources() {
        try {

            val vertexShaderPath = "src/main/resources/shaders/vertex.glsl"
            val fragmentShaderPath = "src/main/resources/shaders/fragment.glsl"
            
            if (File(vertexShaderPath).exists() && File(fragmentShaderPath).exists()) {
                shader = Shader(vertexShaderPath, fragmentShaderPath)
            } else {
                throw RuntimeException("Файлы шейдеров не найдены")
            }
            

            val meshes = createFullKeyboardModel()
            

            keyboardTexture = createCurrentTexture()
            

            meshes.forEach { mesh ->
                val material = mesh.getMaterial()
                material.diffuseTexture = keyboardTexture
                material.hasDiffuseTexture = true
            }
            
            keyboardModel = OBJModel(meshes)

            keyboardModel!!.setPosition(Vector3f(0.0f, 0.0f, -5.0f))
            keyboardModel!!.setScale(Vector3f(1.0f, 1.0f, 1.0f))
            keyboardAnimator = KeyboardAnimator(keyboardModel!!, meshes)
            

            createTable()
            
            

            setupLighting()
            

            setupAnimations()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupLighting() {

        val directionalLight = DirectionalLight(
            direction = org.joml.Vector3f(-0.2f, -1.0f, -0.3f),
            ambient = org.joml.Vector3f(0.15f, 0.2f, 0.25f), 
            diffuse = org.joml.Vector3f(0.4f, 0.45f, 0.5f), 
            specular = org.joml.Vector3f(0.3f, 0.3f, 0.3f) 
        )
        lightingManager.addDirectionalLight(directionalLight)
        

        val dynamicPointLight = PointLight(
            position = dynamicLightPosition,
            ambient = org.joml.Vector3f(0.1f, 0.1f, 0.1f), 
            diffuse = org.joml.Vector3f(0.5f, 0.45f, 0.35f), 
            specular = org.joml.Vector3f(0.4f, 0.4f, 0.3f), 
            constant = 1.0f,
            linear = 0.09f,
            quadratic = 0.032f
        )
        lightingManager.addPointLight(dynamicPointLight)
        

        val staticPointLight = PointLight(
            position = org.joml.Vector3f(0.0f, 3.0f, -3.0f),
            ambient = org.joml.Vector3f(0.05f, 0.05f, 0.08f), 
            diffuse = org.joml.Vector3f(0.2f, 0.25f, 0.3f), 
            specular = org.joml.Vector3f(0.2f, 0.25f, 0.3f), 
            constant = 1.0f,
            linear = 0.09f,
            quadratic = 0.032f
        )
        lightingManager.addPointLight(staticPointLight)
        
    }
    
    private fun setupAnimations() {
        keyboardAnimator?.let { animator ->

            animator.setFloating(true)
            animator.setRotating(true, 15.0f)
            
        }
    }
    
    private fun loop() {
        while (!glfwWindowShouldClose(window)) {
            val currentTime = glfwGetTime()
            deltaTime = currentTime - lastFrameTime
            lastFrameTime = currentTime
            

            processInput()
            

            updateDynamicLighting(deltaTime.toFloat())
            

            keyboardAnimator?.update(deltaTime.toFloat())
            

            render()
            

            glfwSwapBuffers(window)
            glfwPollEvents()
        }
    }
    
    private fun updateDynamicLighting(deltaTime: Float) {

        lightAnimationTime += deltaTime
        

        val radius = 4.0f
        val height = 3.0f + Math.sin(lightAnimationTime * 0.5).toFloat() * 1.0f 
        

        dynamicLightPosition.x = Math.cos(lightAnimationTime * 0.3).toFloat() * radius
        dynamicLightPosition.y = height
        dynamicLightPosition.z = -5.0f + Math.sin(lightAnimationTime * 0.3).toFloat() * radius
        

        val pointLights = lightingManager.getPointLights()
        if (pointLights.isNotEmpty()) {
            pointLights[0].position = dynamicLightPosition
        }
    }
    
    private fun processInput() {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true)
        }
        

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.processKeyboard(CameraMovement.FORWARD, deltaTime.toFloat())
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.processKeyboard(CameraMovement.BACKWARD, deltaTime.toFloat())
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.processKeyboard(CameraMovement.LEFT, deltaTime.toFloat())
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.processKeyboard(CameraMovement.RIGHT, deltaTime.toFloat())
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            camera.processKeyboard(CameraMovement.UP, deltaTime.toFloat())
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            camera.processKeyboard(CameraMovement.DOWN, deltaTime.toFloat())
        }
        

        if (glfwGetKey(window, GLFW_KEY_1) == GLFW_PRESS && !key1WasPressed) {

            keyboardAnimator?.let { animator ->
                animator.animateMultipleKeys(listOf(0, 1, 2, 12, 13, 14), 0.08f)
            }
            key1WasPressed = true
        } else if (glfwGetKey(window, GLFW_KEY_1) == GLFW_RELEASE) {
            key1WasPressed = false
        }
        
        if (glfwGetKey(window, GLFW_KEY_2) == GLFW_PRESS && !key2WasPressed) {

            keyboardAnimator?.let { animator ->
                animator.animateKeyPress(1) 
            }
            key2WasPressed = true
        } else if (glfwGetKey(window, GLFW_KEY_2) == GLFW_RELEASE) {
            key2WasPressed = false
        }
        
        if (glfwGetKey(window, GLFW_KEY_4) == GLFW_PRESS && !key4WasPressed) {

            keyboardAnimator?.let { animator ->
                val currentState = animator.getAnimationState()
                animator.setScaling(!currentState.isScaling)
            }
            key4WasPressed = true
        } else if (glfwGetKey(window, GLFW_KEY_4) == GLFW_RELEASE) {
            key4WasPressed = false
        }
        
        if (glfwGetKey(window, GLFW_KEY_3) == GLFW_PRESS && !key3WasPressed) {

            keyboardAnimator?.resetAnimations()
            key3WasPressed = true
        } else if (glfwGetKey(window, GLFW_KEY_3) == GLFW_RELEASE) {
            key3WasPressed = false
        }
        
        if (glfwGetKey(window, GLFW_KEY_T) == GLFW_PRESS && !keyTWasPressed) {

            switchTexture()
            keyTWasPressed = true
        } else if (glfwGetKey(window, GLFW_KEY_T) == GLFW_RELEASE) {
            keyTWasPressed = false
        }
    }
    
    private fun render() {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        

        shader?.use()
        

        val projection = camera.getProjectionMatrix(width, height)
        val view = camera.getViewMatrix()
        
        shader?.setMat4("projection", projection)
        shader?.setMat4("view", view)
        

        shader?.setVec3("viewPos", camera.getPosition())
        

        lightingManager.applyToShader(shader!!)
        

        glDisable(GL_CULL_FACE)
        tableModel?.draw(shader!!)
        glEnable(GL_CULL_FACE)
        

        keyboardModel?.draw(shader!!)
        

        shader?.stop()
    }
    
    
    private fun createFullKeyboardModel(): List<Mesh> {
        val meshes = mutableListOf<Mesh>()
        

        val keyWidth = 0.8f
        val keyHeight = 0.2f
        val keyDepth = 0.8f
        val keySpacing = 0.1f
        val rowSpacing = 0.2f
        

        val baseWidth = 15 * (keyWidth + keySpacing) + keySpacing 
        val baseDepth = 6 * (keyDepth + rowSpacing) + rowSpacing 
        val baseHeight = 0.1f
        
        val baseVertices = createCubeVertices(
            Vector3f(-baseWidth / 2, -baseHeight / 2, -baseDepth / 2),
            Vector3f(baseWidth, baseHeight, baseDepth)
        )
        val baseIndices = createCubeIndices()
        meshes.add(Mesh(baseVertices, baseIndices, Material.BLACK_PLASTIC))
        

        val keyY = baseHeight / 2 + keyHeight / 2 
        var keyCount = 0
        

        val fRowZ = baseDepth / 2 - rowSpacing - keyDepth / 2
        val fRowTotalWidth = baseWidth - 2 * keySpacing
        val fRowKeyCount = 12
        val fRowKeyWidth = (fRowTotalWidth - (fRowKeyCount - 1) * keySpacing) / fRowKeyCount
        
        for (i in 0 until 12) {
            val x = -baseWidth / 2 + keySpacing + fRowKeyWidth / 2 + i * (fRowKeyWidth + keySpacing)
            meshes.add(createKeyMesh(Vector3f(x, keyY, fRowZ), fRowKeyWidth, keyDepth, Material.GRAY_PLASTIC))
            keyCount++
        }
        

        val row1Z = fRowZ - (keyDepth + rowSpacing)
        val row1Keys = listOf("`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "Backspace")
        

        val row1StartX = -baseWidth / 2 + keySpacing
        val row1EndX = baseWidth / 2 - keySpacing
        val row1TotalWidth = row1EndX - row1StartX
        

        val backspaceWidth = row1TotalWidth * 0.15f 
        val row1RemainingWidth = row1TotalWidth - backspaceWidth - 13 * keySpacing 
        val row1StandardKeyWidth = row1RemainingWidth / 13 
        
        var currentX = row1StartX
        for (i in row1Keys.indices) {
            val width = if (i == 13) backspaceWidth else row1StandardKeyWidth
            meshes.add(createKeyMesh(Vector3f(currentX + width / 2, keyY, row1Z), width, keyDepth, Material.WHITE_PLASTIC))
            currentX += width + keySpacing
            keyCount++
        }
        

        val row2Z = row1Z - (keyDepth + rowSpacing)
        val row2Keys = listOf("Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\")
        

        val row2StartX = -baseWidth / 2 + keySpacing
        val row2EndX = baseWidth / 2 - keySpacing
        val row2TotalWidth = row2EndX - row2StartX
        

        val tabWidth = row2TotalWidth * 0.12f 
        val row2RemainingWidth = row2TotalWidth - tabWidth - 13 * keySpacing 
        val row2StandardKeyWidth = row2RemainingWidth / 13 
        
        currentX = row2StartX
        for (i in row2Keys.indices) {
            val width = if (i == 0) tabWidth else row2StandardKeyWidth
            meshes.add(createKeyMesh(Vector3f(currentX + width / 2, keyY, row2Z), width, keyDepth, Material.WHITE_PLASTIC))
            currentX += width + keySpacing
            keyCount++
        }
        

        val row3Z = row2Z - (keyDepth + rowSpacing)
        val row3Keys = listOf("Caps", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "Enter")
        

        val row3StartX = -baseWidth / 2 + keySpacing
        val row3EndX = baseWidth / 2 - keySpacing
        val row3TotalWidth = row3EndX - row3StartX
        

        val capsWidth = row3TotalWidth * 0.12f 
        val enterWidth = row3TotalWidth * 0.18f 
        val row3RemainingWidth = row3TotalWidth - capsWidth - enterWidth - 12 * keySpacing 
        val row3StandardKeyWidth = row3RemainingWidth / 11 
        
        currentX = row3StartX
        for (i in row3Keys.indices) {
            val width = when (i) {
                0 -> capsWidth 
                12 -> enterWidth 
                else -> row3StandardKeyWidth
            }
            meshes.add(createKeyMesh(Vector3f(currentX + width / 2, keyY, row3Z), width, keyDepth, Material.WHITE_PLASTIC))
            currentX += width + keySpacing
            keyCount++
        }
        

        val row4Z = row3Z - (keyDepth + rowSpacing)
        val row4Keys = listOf("Shift", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "/", "Shift")
        

        val row4StartX = -baseWidth / 2 + keySpacing
        val row4EndX = baseWidth / 2 - keySpacing
        val row4TotalWidth = row4EndX - row4StartX
        

        val shiftWidth = row4TotalWidth * 0.15f 
        val row4RemainingWidth = row4TotalWidth - 2 * shiftWidth - 11 * keySpacing 
        val row4StandardKeyWidth = row4RemainingWidth / 10 
        
        currentX = row4StartX
        for (i in row4Keys.indices) {
            val width = if (i == 0 || i == 11) shiftWidth else row4StandardKeyWidth
            meshes.add(createKeyMesh(Vector3f(currentX + width / 2, keyY, row4Z), width, keyDepth, Material.WHITE_PLASTIC))
            currentX += width + keySpacing
            keyCount++
        }
        

        val row5Z = row4Z - (keyDepth + rowSpacing)
        val row5Keys = listOf("Ctrl", "Win", "Alt", "Space", "Alt", "Win", "Menu", "Ctrl")
        

        val row5StartX = -baseWidth / 2 + keySpacing
        val row5EndX = baseWidth / 2 - keySpacing
        val row5TotalWidth = row5EndX - row5StartX
        

        val spaceWidth = row5TotalWidth * 0.4f 
        val row5RemainingWidth = row5TotalWidth - spaceWidth - 7 * keySpacing 
        val row5StandardKeyWidth = row5RemainingWidth / 7 
        
        currentX = row5StartX
        for (i in row5Keys.indices) {
            val width = if (i == 3) spaceWidth else row5StandardKeyWidth 
            meshes.add(createKeyMesh(Vector3f(currentX + width / 2, keyY, row5Z), width, keyDepth, Material.WHITE_PLASTIC))
            currentX += width + keySpacing
            keyCount++
        }
        
        
        
        return meshes
    }
    
    private fun createKeyMesh(position: Vector3f, width: Float, depth: Float, material: Material): Mesh {
        val keyVertices = createCubeVertices(
            Vector3f(position.x - width / 2, position.y, position.z - depth / 2),
            Vector3f(width, 0.2f, depth)
        )
        val keyIndices = createCubeIndices()
        return Mesh(keyVertices, keyIndices, material)
    }
    
    private fun createCubeVertices(origin: Vector3f, size: Vector3f): FloatArray {
        val x = origin.x
        val y = origin.y
        val z = origin.z
        val w = size.x
        val h = size.y
        val d = size.z
        
        return floatArrayOf(

            x, y, z + d, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            x + w, y, z + d, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            x + w, y + h, z + d, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            x, y + h, z + d, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            

            x, y, z, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            x + w, y, z, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
            x + w, y + h, z, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            x, y + h, z, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
            

            x, y + h, z + d, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            x + w, y + h, z + d, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            x + w, y + h, z, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            x, y + h, z, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            

            x, y, z + d, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            x + w, y, z + d, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            x + w, y, z, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
            x, y, z, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            

            x + w, y, z + d, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            x + w, y, z, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            x + w, y + h, z, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            x + w, y + h, z + d, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            

            x, y, z + d, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            x, y, z, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            x, y + h, z, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            x, y + h, z + d, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f
        )
    }
    
    private fun createCubeIndices(): IntArray {
        return intArrayOf(
            0, 1, 2, 2, 3, 0,       
            4, 5, 6, 6, 7, 4,       
            8, 9, 10, 10, 11, 8,    
            12, 13, 14, 14, 15, 12, 
            16, 17, 18, 18, 19, 16, 
            20, 21, 22, 22, 23, 20  
        )
    }
    
    private fun createTable() {

        tableTexture = ProceduralTextures.createTableWoodTexture()
        

        val tableMesh = createTableMesh()
        

        val material = tableMesh.getMaterial()
        material.diffuseTexture = tableTexture
        material.hasDiffuseTexture = true
        

        tableModel = OBJModel(listOf(tableMesh))
        

        tableModel!!.setPosition(Vector3f(0.0f, -0.2f, -5.0f))
        tableModel!!.setScale(Vector3f(8.0f, 0.4f, 6.0f)) 
        
    }
    
    private fun createTableMesh(): Mesh {

        val vertices = floatArrayOf(

            


            -1.0f,  0.1f, -1.0f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f,  
             1.0f,  0.1f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f,  
             1.0f,  0.1f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f, 1.0f,  
            -1.0f,  0.1f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f,  
            

            -1.0f,  0.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f, 0.0f,  
             1.0f,  0.0f, -1.0f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f,  
             1.0f,  0.0f,  1.0f,  0.0f, -1.0f,  0.0f,  1.0f, 1.0f,  
            -1.0f,  0.0f,  1.0f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f,  
            


            -1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f, 0.0f,  
             1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f, 0.0f,  
             1.0f,  0.1f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f, 1.0f,  
            -1.0f,  0.1f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f, 1.0f,  
            

            -1.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,  
             1.0f,  0.0f, -1.0f,  0.0f,  0.0f, -1.0f,  1.0f, 0.0f,  
             1.0f,  0.1f, -1.0f,  0.0f,  0.0f, -1.0f,  1.0f, 1.0f,  
            -1.0f,  0.1f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f, 1.0f,  
            

            -1.0f,  0.0f, -1.0f, -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
            -1.0f,  0.0f,  1.0f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
            -1.0f,  0.1f,  1.0f, -1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
            -1.0f,  0.1f, -1.0f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,  
            

             1.0f,  0.0f, -1.0f,  1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
             1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
             1.0f,  0.1f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
             1.0f,  0.1f, -1.0f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f,  
            


            -0.8f,  0.0f,  0.8f,  -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
            -0.7f,  0.0f,  0.8f,   1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
            -0.7f,  0.0f,  0.7f,   1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
            -0.8f,  0.0f,  0.7f,  -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,  
            -0.8f, -1.0f,  0.8f,  -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
            -0.7f, -1.0f,  0.8f,   1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
            -0.7f, -1.0f,  0.7f,   1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
            -0.8f, -1.0f,  0.7f,  -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,  
            

             0.7f,  0.0f,  0.8f,   1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
             0.8f,  0.0f,  0.8f,  -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
             0.8f,  0.0f,  0.7f,  -1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
             0.7f,  0.0f,  0.7f,   1.0f,  0.0f,  0.0f,  0.0f, 1.0f,  
             0.7f, -1.0f,  0.8f,   1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
             0.8f, -1.0f,  0.8f,  -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
             0.8f, -1.0f,  0.7f,  -1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
             0.7f, -1.0f,  0.7f,   1.0f,  0.0f,  0.0f,  0.0f, 1.0f,  
            

            -0.8f,  0.0f, -0.7f,  -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
            -0.7f,  0.0f, -0.7f,   1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
            -0.7f,  0.0f, -0.8f,   1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
            -0.8f,  0.0f, -0.8f,  -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,  
            -0.8f, -1.0f, -0.7f,  -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
            -0.7f, -1.0f, -0.7f,   1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
            -0.7f, -1.0f, -0.8f,   1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
            -0.8f, -1.0f, -0.8f,  -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,  
            

             0.7f,  0.0f, -0.7f,   1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
             0.8f,  0.0f, -0.7f,  -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
             0.8f,  0.0f, -0.8f,  -1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
             0.7f,  0.0f, -0.8f,   1.0f,  0.0f,  0.0f,  0.0f, 1.0f,  
             0.7f, -1.0f, -0.7f,   1.0f,  0.0f,  0.0f,  0.0f, 0.0f,  
             0.8f, -1.0f, -0.7f,  -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,  
             0.8f, -1.0f, -0.8f,  -1.0f,  0.0f,  0.0f,  1.0f, 1.0f,  
             0.7f, -1.0f, -0.8f,   1.0f,  0.0f,  0.0f,  0.0f, 1.0f   
        )
        
        val indices = intArrayOf(


            0, 2, 1, 0, 3, 2,

            4, 6, 5, 4, 7, 6,

            8, 9, 10, 10, 11, 8,

            12, 13, 14, 14, 15, 12,

            16, 17, 18, 18, 19, 16,

            20, 21, 22, 22, 23, 20,
            

            24, 25, 26, 26, 27, 24, 
            28, 29, 30, 30, 31, 28, 
            24, 25, 29, 29, 28, 24, 
            26, 27, 31, 31, 30, 26, 
            24, 27, 31, 31, 28, 24, 
            25, 26, 30, 30, 29, 25, 
            
            32, 33, 34, 34, 35, 32,
            36, 37, 38, 38, 39, 36,
            32, 33, 37, 37, 36, 32,
            34, 35, 39, 39, 38, 34,
            32, 35, 39, 39, 36, 32,
            33, 34, 38, 38, 37, 33,
            
            40, 41, 42, 42, 43, 40,
            44, 45, 46, 46, 47, 44,
            40, 41, 45, 45, 44, 40,
            42, 43, 47, 47, 46, 42,
            40, 43, 47, 47, 44, 40,
            41, 42, 46, 46, 45, 41,
            
            48, 49, 50, 50, 51, 48,
            52, 53, 54, 54, 55, 52,
            48, 49, 53, 53, 52, 48,
            50, 51, 55, 55, 54, 50,
            48, 51, 55, 55, 52, 48,
            49, 50, 54, 54, 53, 49
        )
        
        val tableMaterial = Material(
            ambient = org.joml.Vector3f(0.2f, 0.2f, 0.2f),
            diffuse = org.joml.Vector3f(0.8f, 0.8f, 0.8f),
            specular = org.joml.Vector3f(0.1f, 0.1f, 0.1f),
            shininess = 32.0f
        )
        return Mesh(vertices, indices, tableMaterial)
    }

    private fun createCurrentTexture(): Int {
        return when (currentTextureType) {
            0 -> ProceduralTextures.createKeyboardTexture()
            1 -> ProceduralTextures.createWoodTexture()
            2 -> ProceduralTextures.createMetalTexture()
            else -> ProceduralTextures.createKeyboardTexture()
        }
    }

    private fun switchTexture() {
        currentTextureType = (currentTextureType + 1) % textureTypes.size
        keyboardTexture = createCurrentTexture()
        
        keyboardModel?.let { model ->
            model.getMeshes().forEach { mesh ->
                val material = mesh.getMaterial()
                material.diffuseTexture = keyboardTexture
                material.hasDiffuseTexture = true
            }
        }
    }

    private fun cleanup() {
        keyboardModel?.cleanup()
        tableModel?.cleanup()
        shader?.cleanup()
        
        glfwDestroyWindow(window)
        glfwTerminate()
        
    }
}