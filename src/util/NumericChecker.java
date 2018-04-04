package util;

import java.util.regex.Pattern;

public class NumericChecker {

    public static boolean isNumeric(String str){
        //Pattern pattern = Pattern.compile("[0-9]+(\\.?)[0-9]*"); 
            Pattern pattern = Pattern.compile("[-+]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][-+]?\\d+)?[dD]?");
        return pattern.matcher(str).matches();
     }

}
