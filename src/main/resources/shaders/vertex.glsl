#version 330 core

// Входные атрибуты
layout (location = 0) in vec3 aPos;      // Позиция вершины
layout (location = 1) in vec3 aNormal;   // Нормаль вершины
layout (location = 2) in vec2 aTexCoord; // Текстурные координаты

// Исходящие данные для фрагментного шейдера
out vec3 FragPos;        // Позиция фрагмента в мировых координатах
out vec3 Normal;         // Нормаль в мировых координатах
out vec2 TexCoord;       // Текстурные координаты

// Униформы
uniform mat4 model;      // Матрица модели
uniform mat4 view;       // Матрица вида
uniform mat4 projection; // Матрица проекции

void main()
{
    // Вычисляем позицию фрагмента в мировых координатах
    FragPos = vec3(model * vec4(aPos, 1.0));
    
    // Вычисляем нормаль в мировых координатах
    Normal = mat3(transpose(inverse(model))) * aNormal;
    
    // Передаем текстурные координаты
    TexCoord = aTexCoord;
    
    // Вычисляем финальную позицию вершины
    gl_Position = projection * view * vec4(FragPos, 1.0);
}
