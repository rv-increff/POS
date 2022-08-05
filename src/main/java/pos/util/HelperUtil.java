package pos.util;

import pos.model.*;
import pos.pojo.BrandPojo;
import pos.pojo.InventoryPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.ProductPojo;
import pos.spring.ApiException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static pos.util.DataUtil.checkNotNullBulkUtil;

public class HelperUtil {
    public static ProductData convertPojoToProductData(ProductPojo p){  //TODO put all static function to util
        ProductData productData = new ProductData();
        productData.setId(p.getId());
        productData.setBrand(p.getBrand());
        productData.setCategory(p.getCategory());
        productData.setBrandPojoId(p.getBrandId());
        productData.setBarcode(p.getBarcode());
        productData.setMrp(p.getMrp());
        productData.setName(p.getName());
        return productData;
    }
    public static ProductPojo convertFormToProductPojo(ProductForm p){
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBrand(p.getBrand());
        productPojo.setCategory(p.getCategory());
        productPojo.setBarcode(p.getBarcode());
        productPojo.setMrp(p.getMrp());
        productPojo.setName(p.getName());
        return productPojo;
    }
    public static ProductPojo convertFormToProductPojo(ProductUpdateForm productUpdateForm){
        ProductPojo productPojo = new ProductPojo();
        productPojo.setId(productUpdateForm.getId());
        productPojo.setBrand(productUpdateForm.getBrand());
        productPojo.setCategory(productUpdateForm.getCategory());
        productPojo.setBarcode(productUpdateForm.getBarcode());
        productPojo.setMrp(productUpdateForm.getMrp());
        productPojo.setName(productUpdateForm.getName());
        return productPojo;
    }
    public static void validateList(List<BrandForm> brandFormList) throws ApiException {
        List<String> errorList = new ArrayList<>();
        Integer row = 1;
        for(BrandForm brandForm : brandFormList){
            if(!checkNotNullBulkUtil(brandForm)){
                errorList.add("Error : row -> " + (row) + " brand or category cannot be empty");
            }
            row ++;
        }
        if(errorList.size()>0) {
            String errorStr = "";
            for(String e : errorList){
                errorStr += e + "\n";
            }
            throw new ApiException(errorStr);
        }
    }
    public static BrandData convertPojoToBrandData(BrandPojo brandPojo){
        BrandData brandData = new BrandData();
        brandData.setId(brandPojo.getId());
        brandData.setBrand(brandPojo.getBrand());
        brandData.setCategory(brandPojo.getCategory());
        return brandData;
    }
    public static BrandPojo convertBrandFormToPojo(BrandForm brandForm){
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(brandForm.getBrand());
        brandPojo.setCategory(brandForm.getCategory());
        return brandPojo;
    }
    public static BrandPojo convertBrandDataToPojo(BrandData brandData){
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setId(brandData.getId());
        brandPojo.setBrand(brandData.getBrand());
        brandPojo.setCategory(brandData.getCategory());
        return brandPojo;
    }
    public static InventoryData convertPojoToInventoryForm(InventoryPojo p){
        InventoryData inventoryData = new InventoryData();
        inventoryData.setId(p.getId());
        inventoryData.setBarcode(p.getBarcode());
        inventoryData.setQuantity(p.getQuantity());
        inventoryData.setProductId(p.getProductId());
        return inventoryData;
    }

    public static InventoryPojo convertInventoryFormToInventoryPojo(InventoryForm inventoryForm){
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setBarcode(inventoryForm.getBarcode());
        inventoryPojo.setQuantity(inventoryForm.getQuantity());
        return inventoryPojo;
    }
    public static OrderItemPojo convertOrderItemFormToPojo(OrderItemForm orderItemForm){
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setQuantity(orderItemForm.getQuantity());
        orderItemPojo.setSellingPrice(orderItemForm.getSellingPrice());
        orderItemPojo.setProductId(orderItemForm.getProductId());
        orderItemPojo.setOrderId(orderItemForm.getOrderId());
        return orderItemPojo;
    }
    public static OrderItemPojo convertOrderItemFormToPojo(OrderItemUpdateForm orderItemUpdateForm){
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setQuantity(orderItemUpdateForm.getQuantity());
        orderItemPojo.setSellingPrice(orderItemUpdateForm.getSellingPrice());
        orderItemPojo.setId(orderItemUpdateForm.getId());
        return orderItemPojo;
    }
    public static OrderItemData convertPojoToOrderItemData(OrderItemPojo p){
        OrderItemData b = new OrderItemData();
        b.setId(p.getId());
        b.setQuantity(p.getQuantity());
        b.setSellingPrice(p.getSellingPrice());
        b.setProductId(p.getProductId());
        b.setOrderId(p.getOrderId());
        return b;
    }
    public static String jaxbObjectToXML(OrderItemDataList orderItemList) {
        try {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(OrderItemDataList.class);

            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            //Print XML String to Console
            StringWriter sw = new StringWriter();

            //Write XML to StringWriter
            jaxbMarshaller.marshal(orderItemList, sw);

            //Verify XML Content
            String xmlContent = sw.toString();
            return xmlContent;

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static byte[] returnFileStream() throws FileNotFoundException {
        String path = "/Users/rahulverma/Downloads/git/POS/src/invoice.pdf";
        File file = new File(path);
        if (file.exists()) {

            byte[] inFileBytes = new byte[0];
            try {
                byte[] bytes = loadFile(file);
                byte[] encoded = Base64.getEncoder().encode(bytes);
                byte[] pdf = Files.readAllBytes(file.toPath());
                return pdf;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


//            return new BufferedInputStream(new FileInputStream(file));
        }
        return null;
    }
    @SuppressWarnings("resource")
    public static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }
}
