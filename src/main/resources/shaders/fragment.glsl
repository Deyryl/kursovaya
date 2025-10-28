#version 330 core

// Входящие данные от вершинного шейдера
in vec3 FragPos;    // Позиция фрагмента в мировых координатах
in vec3 Normal;     // Нормаль в мировых координатах
in vec2 TexCoord;   // Текстурные координаты

// Исходящий цвет
out vec4 FragColor;

// Материал
struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
    bool hasDiffuseTexture;
    bool hasSpecularTexture;
    bool hasNormalTexture;
    sampler2D diffuseTexture;
    sampler2D specularTexture;
    sampler2D normalTexture;
};

// Направленный свет
struct DirLight {
    vec3 direction;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

// Точечный свет
struct PointLight {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float constant;
    float linear;
    float quadratic;
};

// Прожектор
struct SpotLight {
    vec3 position;
    vec3 direction;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float constant;
    float linear;
    float quadratic;
    float cutOff;
    float outerCutOff;
};

// Униформы
uniform Material material;
uniform DirLight dirLight;
uniform PointLight pointLights[10];
uniform SpotLight spotLights[10];
uniform int numDirLights;
uniform int numPointLights;
uniform int numSpotLights;
uniform vec3 viewPos;

// Функции для вычисления освещения
vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir);
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir);
vec3 CalcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir);

void main()
{
    // Нормализуем нормаль
    vec3 norm = normalize(Normal);
    
    // Вычисляем направление к камере
    vec3 viewDir = normalize(viewPos - FragPos);
    
    // Инициализируем результат
    vec3 result = vec3(0.0);
    
    // Добавляем направленные источники света
    for(int i = 0; i < numDirLights; i++) {
        result += CalcDirLight(dirLight, norm, viewDir);
    }
    
    // Добавляем точечные источники света
    for(int i = 0; i < numPointLights; i++) {
        result += CalcPointLight(pointLights[i], norm, FragPos, viewDir);
    }
    
    // Добавляем прожекторы
    for(int i = 0; i < numSpotLights; i++) {
        result += CalcSpotLight(spotLights[i], norm, FragPos, viewDir);
    }
    
    FragColor = vec4(result, 1.0);
}

// Вычисление направленного света
vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir)
{
    vec3 lightDir = normalize(-light.direction);
    
    // Диффузное освещение
    float diff = max(dot(normal, lightDir), 0.0);
    
    // Спекулярное освещение
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    
    // Получаем цвета из текстур или используем цвета материала
    vec3 ambientColor = material.ambient;
    vec3 diffuseColor = material.diffuse;
    vec3 specularColor = material.specular;
    
    if(material.hasDiffuseTexture) {
        diffuseColor = texture(material.diffuseTexture, TexCoord).rgb;
    }
    if(material.hasSpecularTexture) {
        specularColor = texture(material.specularTexture, TexCoord).rgb;
    }
    
    // Комбинируем результаты
    vec3 ambient = light.ambient * ambientColor;
    vec3 diffuse = light.diffuse * diff * diffuseColor;
    vec3 specular = light.specular * spec * specularColor;
    
    return (ambient + diffuse + specular);
}

// Вычисление точечного света
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    
    // Диффузное освещение
    float diff = max(dot(normal, lightDir), 0.0);
    
    // Спекулярное освещение
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    
    // Затухание
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    
    // Получаем цвета из текстур или используем цвета материала
    vec3 ambientColor = material.ambient;
    vec3 diffuseColor = material.diffuse;
    vec3 specularColor = material.specular;
    
    if(material.hasDiffuseTexture) {
        diffuseColor = texture(material.diffuseTexture, TexCoord).rgb;
    }
    if(material.hasSpecularTexture) {
        specularColor = texture(material.specularTexture, TexCoord).rgb;
    }
    
    // Комбинируем результаты
    vec3 ambient = light.ambient * ambientColor;
    vec3 diffuse = light.diffuse * diff * diffuseColor;
    vec3 specular = light.specular * spec * specularColor;
    
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;
    
    return (ambient + diffuse + specular);
}

// Вычисление прожектора
vec3 CalcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    
    // Проверяем, находится ли фрагмент в конусе прожектора
    float theta = dot(lightDir, normalize(-light.direction));
    float epsilon = light.cutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0, 1.0);
    
    // Диффузное освещение
    float diff = max(dot(normal, lightDir), 0.0);
    
    // Спекулярное освещение
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    
    // Затухание
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    
    // Получаем цвета из текстур или используем цвета материала
    vec3 ambientColor = material.ambient;
    vec3 diffuseColor = material.diffuse;
    vec3 specularColor = material.specular;
    
    if(material.hasDiffuseTexture) {
        diffuseColor = texture(material.diffuseTexture, TexCoord).rgb;
    }
    if(material.hasSpecularTexture) {
        specularColor = texture(material.specularTexture, TexCoord).rgb;
    }
    
    // Комбинируем результаты
    vec3 ambient = light.ambient * ambientColor;
    vec3 diffuse = light.diffuse * diff * diffuseColor;
    vec3 specular = light.specular * spec * specularColor;
    
    ambient *= attenuation * intensity;
    diffuse *= attenuation * intensity;
    specular *= attenuation * intensity;
    
    return (ambient + diffuse + specular);
}
