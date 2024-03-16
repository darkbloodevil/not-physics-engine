package npe.tools.cmdLine

object CommandData {
    val NULL_COMMAND = "null_command"
    val NULL_TYPE = "null_type"
    val STRING_TYPE = "string_type"
    val INT_TYPE = "int_type"
    val FLOAT_TYPE = "float_type"
    val LIST_TYPE = "list_type"
    /**
     * 只要命令名字没有value的类型
     */
    val ZERO_TYPE="zero_type"
    
    
    def null_command(): CommandData = {
        CommandData(NULL_COMMAND, NULL_TYPE, null)
    }
}

/**
 * 命令数据。提供不同的获取方式
 * 注意这边没有对于数据进行处理，需要在创建类型的时候有提供处理
 *
 * @param command_name  命令名称
 * @param value_type    命令的value的类型
 * @param command_value 命令数据
 */
class CommandData(command_name: String, value_type: String, command_value: Any) {
    
    def get_name(): String = {
        command_name
    }
    
    def get_type(): String = {
        value_type
    }
    
    def get(): Any = {
        command_value
    }
    
    def get_int(): Int = {
        String.valueOf(command_value).toInt
    }
    
    def get_float(): Float = {
        String.valueOf(command_value).toFloat
    }
    
    def get_str(): String = {
        String.valueOf(command_value)
    }
    
    def get_list(): Array[Any] = {
        command_value.asInstanceOf[Array[Any]]
    }
}

