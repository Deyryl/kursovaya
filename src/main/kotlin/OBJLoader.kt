package org.example

import org.joml.Vector2f
import org.joml.Vector3f
import java.io.File

object OBJLoader {
    private data class VertexKey(val v: Int, val vt: Int, val vn: Int)

    fun loadOBJ(path: String, defaultMaterial: Material = Material.GRAY_PLASTIC): List<Mesh> {
        val positions = mutableListOf<Vector3f>()
        val texCoords = mutableListOf<Vector2f>()
        val normals = mutableListOf<Vector3f>()

        val vertexMap = HashMap<VertexKey, Int>()
        val builtVertices = ArrayList<Float>()
        val indices = ArrayList<Int>()

        File(path).forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("#")) return@forEachLine

            val parts = line.split(Regex("\\s+"))
            when (parts[0]) {
                "v" -> {
                    if (parts.size >= 4) {
                        positions.add(Vector3f(parts[1].toFloat(), parts[2].toFloat(), parts[3].toFloat()))
                    }
                }
                "vt" -> {
                    if (parts.size >= 3) {
                        texCoords.add(Vector2f(parts[1].toFloat(), parts[2].toFloat()))
                    } else if (parts.size >= 2) {
                        texCoords.add(Vector2f(parts[1].toFloat(), 0.0f))
                    }
                }
                "vn" -> {
                    if (parts.size >= 4) {
                        normals.add(Vector3f(parts[1].toFloat(), parts[2].toFloat(), parts[3].toFloat()))
                    }
                }
                "f" -> {
                    if (parts.size < 4) return@forEachLine

                    val faceVerts = parts.drop(1).map { token ->
                        val comps = token.split("/")
                        val vIndex = comps.getOrNull(0)?.toIntOrNull() ?: 0
                        val vtIndex = comps.getOrNull(1)?.toIntOrNull() ?: 0
                        val vnIndex = comps.getOrNull(2)?.toIntOrNull() ?: 0
                        VertexKey(vIndex, vtIndex, vnIndex)
                    }

                    for (i in 2 until faceVerts.size) {
                        val tri = arrayOf(faceVerts[0], faceVerts[i - 1], faceVerts[i])
                        tri.forEach { key ->
                            val idx = addOrGetVertexIndex(
                                key,
                                positions,
                                texCoords,
                                normals,
                                vertexMap,
                                builtVertices
                            )
                            indices.add(idx)
                        }
                    }
                }
            }
        }

        if (indices.isEmpty() || builtVertices.isEmpty()) {
            return emptyList()
        }

        val verticesArray = FloatArray(builtVertices.size)
        for (i in builtVertices.indices) verticesArray[i] = builtVertices[i]
        val indicesArray = IntArray(indices.size)
        for (i in indices.indices) indicesArray[i] = indices[i]

        val mesh = Mesh(verticesArray, indicesArray, defaultMaterial)
        return listOf(mesh)
    }

    private fun addOrGetVertexIndex(
        key: VertexKey,
        positions: List<Vector3f>,
        texCoords: List<Vector2f>,
        normals: List<Vector3f>,
        vertexMap: HashMap<VertexKey, Int>,
        builtVertices: MutableList<Float>
    ): Int {
        val existing = vertexMap[key]
        if (existing != null) return existing

        fun resolveIndex(i: Int, size: Int): Int {
            return when {
                i > 0 -> i - 1
                i < 0 -> size + i
                else -> -1
            }
        }

        val pIdx = resolveIndex(key.v, positions.size)
        val tIdx = resolveIndex(key.vt, texCoords.size)
        val nIdx = resolveIndex(key.vn, normals.size)

        val pos = if (pIdx in positions.indices) positions[pIdx] else Vector3f()
        val tex = if (tIdx in texCoords.indices) texCoords[tIdx] else Vector2f()
        val nor = if (nIdx in normals.indices) normals[nIdx] else Vector3f(0f, 1f, 0f)

        builtVertices.add(pos.x); builtVertices.add(pos.y); builtVertices.add(pos.z)
        builtVertices.add(nor.x); builtVertices.add(nor.y); builtVertices.add(nor.z)
        builtVertices.add(tex.x); builtVertices.add(tex.y)

        val newIndex = (builtVertices.size / 8) - 1
        vertexMap[key] = newIndex
        return newIndex
    }
}


