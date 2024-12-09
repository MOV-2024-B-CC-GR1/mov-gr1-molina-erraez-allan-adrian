# Examen Primer bimestre
## Empresa y Proyectos

Este proyecto es una implementación en **Kotlin** para gestionar información de empresas y sus proyectos asociados. La clase `Empresa` permite manejar atributos básicos de la empresa, agregar, editar, eliminar y listar proyectos, además de guardar y cargar esta información en archivos de texto.

## Características principales

### Clase `Empresa`

- **Atributos**:
  - `nombre`: Nombre de la empresa.
  - `ubicacion`: Ubicación de la empresa.
  - `telefono`: Teléfono de contacto.
  - `anioFundacion`: Año de fundación.
  - `activa`: Indica si la empresa está activa.

- **Funcionalidades**:
  - **Proyectos**:
    - Agregar un proyecto.
    - Obtener la lista de proyectos.
    - Eliminar un proyecto por índice.
    - Editar un proyecto existente.
  - **Persistencia**:
    - Guardar información de la empresa y sus proyectos en archivos de texto.
    - Cargar información desde archivos de texto.

### Clase `Proyecto`
La clase `Proyecto` debe estar definida en otro archivo o ser una parte complementaria del proyecto, y se espera que contenga los siguientes atributos:
- `nombre`: Nombre del proyecto.
- `duracion`: Duración del proyecto en días.
- `presupuesto`: Presupuesto del proyecto.
- `enCurso`: Indica si el proyecto está en curso.
- `fechaInicio`: Fecha de inicio del proyecto.

## Requisitos previos

- **Kotlin**: Asegúrate de tener Kotlin configurado en tu entorno de desarrollo.
- **JVM**: Se requiere una versión compatible de Java.

## Uso

### 1. Crear una instancia de la clase `Empresa`
```kotlin
val empresa = Empresa(
    nombre = "MiEmpresa",
    ubicacion = "Ciudad Central",
    telefono = "123-456-7890",
    anioFundacion = 2000,
    activa = true
)
```

### 2. Agregar proyectos a la empresa
```kotlin
val proyecto1 = Proyecto("Proyecto Alpha", 30, 5000.0, true, "2024-01-01")
val proyecto2 = Proyecto("Proyecto Beta", 60, 12000.0, false, "2023-10-15")

empresa.agregarProyecto(proyecto1)
empresa.agregarProyecto(proyecto2)
```

### 3. Guardar información en archivos de texto
```kotlin
empresa.guardarEnArchivo()
```
Esto genera dos archivos:
- `MiEmpresa.txt`: Contiene los detalles básicos de la empresa.
- `MiEmpresa-proyectos.txt`: Contiene los detalles de los proyectos.

### 4. Cargar información desde archivos
```kotlin
val empresaCargada = Empresa.cargarDesdeArchivo("MiEmpresa")
if (empresaCargada != null) {
    println("Empresa cargada: ${empresaCargada.nombre}")
}
```

### 5. Editar o eliminar proyectos
```kotlin
// Editar el primer proyecto
val nuevoProyecto = Proyecto("Proyecto Gamma", 45, 8000.0, true, "2024-02-01")
empresa.editarProyecto(0, nuevoProyecto)

// Eliminar el segundo proyecto
empresa.eliminarProyecto(1)
```
