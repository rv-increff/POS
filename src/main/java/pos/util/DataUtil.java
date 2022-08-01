package pos.util;

import pos.spring.ApiException;

import java.lang.reflect.Field;

public class DataUtil {
    public static <T> void checkNotNullUtil(T form, String e) throws ApiException {
        try {
            Field[] fields = form.getClass().getDeclaredFields();
            for (Field m : fields) {
                m.setAccessible(true);
                System.out.println(m.get(form));
                if(m.get(form) == null){
                    throw new ApiException(e);
                }
            }
        } catch (IllegalAccessException err) {
            System.out.println(err);
        }
    }

    public static <T> Boolean checkNotNullBulkUtil(T form) {
        try {
            Field[] fields = form.getClass().getDeclaredFields();
            for (Field m : fields) {
                    m.setAccessible(true);
                System.out.println(m.get(form));
                    if(m.get(form) == null)
                        return false;
            }
        } catch (IllegalAccessException err) {
            System.out.println(err);

        }
        return true;
    }
    public static <T> void normalizeUtil(T form) {
        try {
            Field[] fields = form.getClass().getDeclaredFields();
            for (Field m : fields) {
                if (m.getGenericType().getTypeName().equals("java.lang.String") & m.getName() != "barcode") {
                    m.setAccessible(true);
                    m.set(form,m.get(form).toString().toLowerCase().trim());
                }
            }
        } catch (IllegalAccessException err) {
            System.out.println(err);
        }
    }
}
