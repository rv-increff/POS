package pos.controller;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pos.spring.ApiException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


@ControllerAdvice
public class RestControllerAdvice {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ApiException.class,Exception.class})
    @ResponseBody
    public void handleBindException(HttpServletRequest request, HttpServletResponse response,Exception e) throws IOException {
        JSONObject obj=new JSONObject();
        System.out.println(Arrays.stream(e.getStackTrace()).findFirst() + "--- main handler " + e.getClass());
        System.out.println(e.getMessage());
        if(e.getClass() == HttpMessageNotReadableException.class ||e.getClass() == MethodArgumentTypeMismatchException.class){

            obj.put("description", "Invalid data format");
        }
        else{
        obj.put("description", e.getMessage());}
        obj.put("code", 400);
        String json = new Gson().toJson(obj);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}

