import java.io.File
import java.io.IOException
import java.util.*


class Empresa(
    val nombre: String,
    val ubicacion: String,
    val telefono: String,
    val anioFundacion: Int,
    var activa: Boolean
) {
    val proyectos = mutableListOf<Proyecto>()

    // Agregar proyecto a la empresa
    fun agregarProyecto(proyecto: Proyecto) {
        proyectos.add(proyecto)
    }

    // Obtener la lista de proyectos de la empresa
    fun obtenerProyectos(): List<Proyecto> {
        return proyectos.toList()
    }

    // Eliminar proyecto de la empresa
    fun eliminarProyecto(index: Int) {
        if (index in 0 until proyectos.size) {
            proyectos.removeAt(index)
        }
    }

    // Editar proyecto de la empresa
    fun editarProyecto(index: Int, proyecto: Proyecto) {
        if (index in 0 until proyectos.size) {
            proyectos[index] = proyecto
        }
    }

    // Guardar la información de la empresa y sus proyectos en archivos de texto
    fun guardarEnArchivo() {
        try {
            val empresaFile = File("$nombre.txt")
            empresaFile.bufferedWriter().use { out ->
                out.write("Nombre: $nombre\n")
                out.write("Ubicación: $ubicacion\n")
                out.write("Teléfono: $telefono\n")
                out.write("Año de Fundación: $anioFundacion\n")
                out.write("Activa: $activa\n")
            }

            val proyectosFile = File("$nombre-proyectos.txt")
            proyectosFile.bufferedWriter().use { out ->
                proyectos.forEach { proyecto ->
                    out.write("${proyecto.nombre}," +
                            "${proyecto.duracion}," +
                            "${proyecto.presupuesto}," +
                            "${proyecto.enCurso}," +
                            "${proyecto.fechaInicio}\n")
                }
            }
            println("Información de la empresa guardada en archivos.")
        } catch (e: IOException) {
            println("Error al guardar la información en archivos.")
            e.printStackTrace()
        }
    }

    // Cargar la información de la empresa y sus proyectos desde archivos de texto
    companion object {
        fun cargarDesdeArchivo(nombreEmpresa: String): Empresa? {
            val empresaFile = File("$nombreEmpresa.txt")
            val proyectosFile = File("$nombreEmpresa-proyectos.txt")

            return try {
                if (!empresaFile.exists() || !proyectosFile.exists()) return null

                val lines = empresaFile.readLines()
                val nombre = lines[0].split(":")[1].trim()
                val ubicacion = lines[1].split(":")[1].trim()
                val telefono = lines[2].split(":")[1].trim()
                val anioFundacion = lines[3].split(":")[1].trim().toInt()
                val activa = lines[4].split(":")[1].trim().toBoolean()

                val empresa = Empresa(nombre, ubicacion, telefono, anioFundacion, activa)

                proyectosFile.readLines().forEach { line ->
                    val parts = line.split(",")
                    if (parts.size == 5) {
                        val nombreProyecto = parts[0].trim()
                        val duracion = parts[1].trim().toInt()
                        val presupuesto = parts[2].trim().toDouble()
                        val enCurso = parts[3].trim().toBoolean()
                        val fechaInicio = parts[4].trim()
                        empresa.agregarProyecto(Proyecto(nombreProyecto, duracion, presupuesto, enCurso, fechaInicio))
                    }
                }

                empresa
            } catch (e: IOException) {
                println("Error al cargar la información de los archivos.")
                e.printStackTrace()
                null
            }
        }
    }
}

// Definimos la clase Proyecto
data class Proyecto(
    var nombre: String,
    var duracion: Int,
    var presupuesto: Double,
    var enCurso: Boolean,
    var fechaInicio: String
)

fun manejarMenuProyecto(scanner: Scanner, empresa: Empresa) {
    var opcion: Int
    do {
        println("\n--- Menú Principal ---")
        println("1. Agregar Proyecto")
        println("2. Ver Proyectos")
        println("3. Editar Proyecto")
        println("4. Eliminar Proyecto")
        println("5. Guardar Información")
        println("6. Salir")
        print("Selecciona una opción: ")
        opcion = scanner.nextInt()
        scanner.nextLine() // Consumir el salto de línea

        when (opcion) {
            1 -> {
                println("Introduce el nombre del proyecto:")
                val nombre = scanner.nextLine()
                println("Introduce la duración del proyecto (en meses):")
                val duracion = scanner.nextInt()
                println("Introduce el presupuesto del proyecto:")
                val presupuesto = scanner.nextDouble()
                scanner.nextLine() // Consumir el salto de línea
                val enCurso = leerBooleanoDesdeTexto(scanner, "¿El proyecto está en curso?")
                println("Introduce la fecha de inicio del proyecto (YYYY-MM-DD):")
                val fechaInicio = scanner.nextLine()

                val nuevoProyecto = Proyecto(nombre, duracion, presupuesto, enCurso, fechaInicio)
                empresa.agregarProyecto(nuevoProyecto)
                println("Proyecto agregado exitosamente.")
            }

            2 -> {
                val proyectos = empresa.obtenerProyectos()
                if (proyectos.isEmpty()) {
                    println("No hay proyectos registrados.")
                } else {
                    println("\n--- Lista de Proyectos ---")
                    proyectos.forEachIndexed { index, proyecto ->
                        println("$index. $proyecto")
                    }
                }
            }

            3 -> {
                println("Introduce el índice del proyecto que deseas editar:")
                val index = scanner.nextInt()
                scanner.nextLine() // Consumir el salto de línea

                if (index in empresa.obtenerProyectos().indices) {
                    println("Introduce el nuevo nombre del proyecto:")
                    val nombre = scanner.nextLine()
                    println("Introduce la nueva duración del proyecto (en meses):")
                    val duracion = scanner.nextInt()
                    println("Introduce el nuevo presupuesto del proyecto:")
                    val presupuesto = scanner.nextDouble()
                    scanner.nextLine() // Consumir el salto de línea
                    val enCurso = leerBooleanoDesdeTexto(scanner, "¿El proyecto está en curso?")
                    println("Introduce la nueva fecha de inicio del proyecto (YYYY-MM-DD):")
                    val fechaInicio = scanner.nextLine()

                    val proyectoActualizado = Proyecto(nombre, duracion, presupuesto, enCurso, fechaInicio)
                    empresa.editarProyecto(index, proyectoActualizado)
                    println("Proyecto editado exitosamente.")
                } else {
                    println("Índice inválido.")
                }
            }

            4 -> {
                println("Introduce el índice del proyecto que deseas eliminar:")
                val index = scanner.nextInt()
                if (index in empresa.obtenerProyectos().indices) {
                    empresa.eliminarProyecto(index)
                    println("Proyecto eliminado exitosamente.")
                } else {
                    println("Índice inválido.")
                }
            }

            5 -> {
                empresa.guardarEnArchivo()
            }

            6 -> println("Saliendo del programa...")
            else -> println("Opción no válida. Intenta de nuevo.")
        }
    } while (opcion != 6)
}

fun leerBooleanoDesdeTexto(scanner: Scanner, mensaje: String): Boolean {
    while (true) {
        print("$mensaje (sí/no): ")
        val entrada = scanner.nextLine().trim().lowercase()
        if (entrada == "sí" || entrada == "si") return true
        if (entrada == "no") return false
        println("Entrada inválida. Por favor, escribe 'sí' o 'no'.")
    }
}


fun main() {
    val scanner = Scanner(System.`in`)
    var opcion: Int

    do {
        println("\n--- Menú Inicial ---")
        println("1. Ver empresas existentes")
        println("2. Crear una nueva empresa")
        println("3. Agregar proyectos a una empresa existente")
        println("4. Salir")
        print("Selecciona una opción: ")
        opcion = scanner.nextInt()
        scanner.nextLine() // Consumir el salto de línea

        when (opcion) {
            1 -> {
                val empresas = listarEmpresas()
                if (empresas.isEmpty()) {
                    println("No hay empresas registradas.")
                } else {
                    println("\n--- Empresas Registradas ---")
                    empresas.forEachIndexed { index, empresa ->
                        println("${index + 1}. $empresa")
                    }
                }
            }

            2 -> {
                println("Introduce el nombre de la nueva empresa:")
                val nombreEmpresa = scanner.nextLine()
                val nuevaEmpresa = crearEmpresa(scanner, nombreEmpresa)
                nuevaEmpresa.guardarEnArchivo()
                println("Empresa '$nombreEmpresa' creada exitosamente.")
            }

            3 -> {
                val empresas = listarEmpresas()
                if (empresas.isEmpty()) {
                    println("No hay empresas registradas. Crea una primero.")
                } else {
                    println("\n--- Empresas Registradas ---")
                    empresas.forEachIndexed { index, empresa ->
                        println("${index + 1}. $empresa")
                    }
                    print("Selecciona una empresa por su número: ")
                    val indice = scanner.nextInt()
                    scanner.nextLine() // Consumir el salto de línea

                    if (indice in 1..empresas.size) {
                        val nombreEmpresa = empresas[indice - 1]
                        val empresa = Empresa.cargarDesdeArchivo(nombreEmpresa)
                        if (empresa != null) {
                            manejarMenuProyecto(scanner, empresa)
                            empresa.guardarEnArchivo()
                        } else {
                            println("No se pudo cargar la empresa seleccionada.")
                        }
                    } else {
                        println("Opción no válida.")
                    }
                }
            }

            4 -> println("Saliendo del programa...")
            else -> println("Opción no válida. Intenta de nuevo.")
        }
    } while (opcion != 4)
}

// Función para listar las empresas existentes en los archivos
fun listarEmpresas(): List<String> {
    return File(".").listFiles()
        ?.filter { it.isFile && it.name.endsWith(".txt") && !it.name.contains("-proyectos") }
        ?.map { it.name.removeSuffix(".txt") }
        ?: emptyList()
}


fun crearEmpresa(scanner: Scanner, nombreEmpresa: String): Empresa {
    println("Introduce la ubicación de la empresa:")
    val ubicacion = scanner.nextLine()
    println("Introduce el teléfono de la empresa:")
    val telefono = scanner.nextLine()
    println("Introduce el año de fundación de la empresa:")
    val anioFundacion = scanner.nextInt()
    scanner.nextLine() // Consumir el salto de línea
    val activa = leerBooleanoDesdeTexto(scanner, "¿La empresa está activa?")

    return Empresa(nombreEmpresa, ubicacion, telefono, anioFundacion, activa)
}
