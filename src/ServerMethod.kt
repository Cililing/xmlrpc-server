@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ServerMethod(val description: String = "\\nUnexpectedEndOfLine")