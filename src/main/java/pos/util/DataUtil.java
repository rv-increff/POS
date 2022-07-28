package pos.util;

import jdk.internal.org.objectweb.asm.Type;
import pos.spring.ApiException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DataUtil {
    public static <T> void checkNotNullUtil(T form, String e) throws ApiException {
        try {
            Method[] methods = form.getClass().getDeclaredMethods();
            for (Method m : methods) {
                if (!m.getGenericReturnType().toString().equals("void")) {
                    if (m.invoke(form) == null) {
                        throw new ApiException(e);
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException err) {
            System.out.println(err);
        }
    }

    public static <T> Boolean checkNotNullBulkUtil(T form) throws ApiException {
        try {
            Method[] methods = form.getClass().getDeclaredMethods();
            for (Method m : methods) {
                if (!m.getGenericReturnType().toString().equals("void")) {
                    if (m.invoke(form) == null) {
                        return false;
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException err) {
            System.out.println(err);

        }
        return true;
    }
    public static <T> void normalizeUtil(T form) {
        try {
            Field[] fields = form.getClass().getDeclaredFields();
            for (Field m : fields) {
                if (m.getGenericType().getTypeName().equals("java.lang.String") & m.getName() != "barcode") {
                    System.out.println(m.getName());
                    m.setAccessible(true);
                    m.set(form,m.get(form).toString().toLowerCase());
                }
            }
        } catch (IllegalAccessException err) {
            System.out.println(err);

        }
    }
}
