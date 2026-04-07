import platform.posix.*




fun main() {
    println("✅ ¡Configuración completada con éxito!")
    println("---")
    
    // Esto llama directamente a un comando de Linux desde tu binario
    println("Tu nombre de usuario es:")
    system("whoami")


    println(mifuncion())
    
    println("---")
    println("Presiona ENTER para cerrar este script nativo...")
    readLine()

}

fun mifuncion():String = "Hola"