package pos.util;

import pos.spring.ApiException;

import java.util.List;

public class ErrorUtil {
    public static void throwError(List<String> errorList) throws ApiException {
        String errorStr = "";
        for(String e : errorList){
            errorStr += e + "\n";
        }
        throw new ApiException(errorStr);
    }
}
