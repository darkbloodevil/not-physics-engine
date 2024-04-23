package npe.tools;


import java.util.Arrays;

public class ArrayTool {

    public static String[] get_str_arr(Object[] object_arr) {
        return Arrays.stream(object_arr)
                .map(Object::toString)
                .toArray(String[]::new);
    }
}
