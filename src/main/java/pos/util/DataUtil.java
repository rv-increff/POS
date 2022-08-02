package pos.util;

import pos.spring.ApiException;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtil {
    public static <T> void validate(T form, String e) throws ApiException {
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
                    if(m.get(form) == null)
                        return false;
            }
        } catch (IllegalAccessException err) {
            System.out.println(err);

        }
        return true;
    }
    public static <T> void normalize(T form) {
        try {
            Field[] fields = form.getClass().getDeclaredFields();
            for (Field m : fields) {
                if (m.getGenericType().getTypeName().equals("java.lang.String") & m.getName() != "barcode") {
                    m.setAccessible(true);
                    if(m.get(form)!=null) {
                        m.set(form, m.get(form).toString().toLowerCase().trim());
                    }
                }
            }
        } catch (IllegalAccessException err) {
            System.out.println(err);
        }
    }
    public static void validateBarcode(String barcode) throws ApiException {
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
        Matcher matcher = pattern.matcher(barcode);
        if(!matcher.find()){
            throw new ApiException("barcode "  + barcode + " not valid, barcode can only have alphanumeric values");
        }
    }

    public static Boolean validateBarcodeBulk(String barcode){
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
        Matcher matcher = pattern.matcher(barcode);
        return matcher.find();
    }

    public static void validateMRP(Double MRP) throws ApiException {
        Pattern numP = Pattern.compile("^[0-9]+$|^[0-9]+\\.[0-9]*$");
        Matcher matcher = numP.matcher(MRP.toString());
        if(!matcher.find()){
            throw new ApiException("mrp "  + MRP +  " not valid, mrp should be a positive number");
        }
    }
    public static Boolean validateMRPBulk(Double MRP) throws ApiException {
        Pattern numP = Pattern.compile("^[0-9]+$|^[0-9]+\\.[0-9]*$");
        Matcher matcher = numP.matcher(MRP.toString());
        return matcher.find();
    }

}
