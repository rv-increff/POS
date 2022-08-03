package pos.dto;

import javafx.util.Pair;
import org.apache.fop.apps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.*;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;
import pos.services.*;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pos.util.HelperUtil.convertPojoToOrderData;

@Service
public class InvoiceDto {
    @Autowired
    BrandServices brandServices;
    @Autowired
    OrderServices orderServices;
    @Autowired
    OrderItemServices orderItemServices;
    @Autowired
    ProductServices productServices;
    @Autowired
    InventoryServices inventoryServices;

    private final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

    public void getOrderInvoice(int orderId) throws ApiException, IOException, TransformerException{

        List<OrderItemData> o = getOrderItemForOrder(orderId);

        ZonedDateTime time = orderServices.get(orderId).getTime();
        Double total = 0.;

        for(OrderItemData i : o){
            total += i.getQuantity() * i.getSellingPrice();
        }
        OrderItemDataList oItem = new OrderItemDataList(o,time,total,orderId);


        String xml = jaxbObjectToXML(oItem);
        File xsltFile = new File("src", "invoice.xsl");
        File pdfFile = new File("src", "invoice.pdf");
        System.out.println(xml);
        convertToPDF(oItem,xsltFile,pdfFile,xml);
    }
    @Transactional(rollbackOn = ApiException.class)
    public List<SalesReport> getSalesReport(SalesReportForm s) throws ApiException{

        ZonedDateTime from = s.getFrom();
        ZonedDateTime to = s.getTo();

        if( ChronoUnit.DAYS.between(from, to) > 30){
            throw new ApiException("Date range should not be greater than 30 days");
        }

        if(ChronoUnit.DAYS.between(from, to) <=0){
            throw new ApiException("From date should be greater than To date");
        }

        if(s.getBrand()!="" & s.getCategory()!="" & brandServices.selectByBrandCategory(s.getBrand(), s.getCategory())==null) {
            throw new ApiException(s.getBrand() + " - " +  s.getCategory() +" Brand-category pair does not exist");
        }

        if(s.getCategory()=="" & s.getBrand()!="" & brandServices.selectByBrand(s.getBrand())==null){
            throw new ApiException("Brand does not exist");
        }

        if(s.getBrand()=="" & s.getCategory()!="" & brandServices.selectByCategory(s.getCategory())==null){
            throw new ApiException("Brand does not exist");
        }
        List<SalesReport> salesReportData = getSalesReportUtil(s);
        if(salesReportData.size()==0){
            throw new ApiException("No sales for given range");
        }
        return salesReportData;

    }

    public List<SalesReport> getSalesReportUtil(SalesReportForm s) throws ApiException {
        //get order
        List<OrderPojo> orderList = orderServices.selectByFromToDate(s.getFrom(),s.getTo());
        List<Integer> orderIdList = getOrderIdList(orderList);
        List<OrderItemPojo> orderItemList;
        if(orderIdList.size()>0) {
            orderItemList = orderItemServices.selectFromOrderIdList(orderIdList);
        }
        else {
            throw new ApiException("No sales");
        }
        List<ProductPojo> productPojoList;

        if(s.getBrand()!="" && s.getCategory()!=""){
            productPojoList = productServices.getByBrandAndCategory(s.getBrand(),s.getCategory());
        }
        else if (s.getBrand() == "" & s.getCategory() != "") {
            productPojoList = productServices.getByCategory(s.getCategory());
        }
        else if (s.getCategory() == "" & s.getBrand() != "") {
            productPojoList = productServices.getByBrand(s.getBrand());
        }
        else {
            productPojoList = productServices.getAll();
        }

        List<SalesReport> salesReportDataSparse = getSalesData(orderItemList,productPojoList);
        List<SalesReport> salesReportData = getGroupedData(salesReportDataSparse);
        return salesReportData;

    }

    private List<SalesReport> getSalesData(List<OrderItemPojo> orderItemPojoList, List<ProductPojo> productPojoList){
        List<SalesReport> commonPojo = new ArrayList<>();

        Map<Integer,ProductPojo> productMap = new HashMap<>();

        for(ProductPojo productPojo : productPojoList){
            productMap.put(productPojo.getId(),productPojo);
        }

        for(OrderItemPojo orderItemPojo : orderItemPojoList){
            if(productMap.containsKey(orderItemPojo.getProductId())){

                ProductPojo temp = productMap.get(orderItemPojo.getProductId());
                SalesReport salesReport = new SalesReport(temp.getBrand(),temp.getCategory(),
                        (long)orderItemPojo.getQuantity(),
                        orderItemPojo.getSellingPrice() * orderItemPojo.getQuantity());
                commonPojo.add(salesReport);

            }
        }

        return commonPojo;
    }
    private List<SalesReport> getGroupedData(List<SalesReport> salesReportDataSparse){
        Map<Pair<String,String>,SalesReport> map = new HashMap<>();
        for(SalesReport data : salesReportDataSparse){
            Pair brandPair = new Pair(data.getBrand(),data.getCategory());
            if(map.containsKey(brandPair)){
                SalesReport salesReport = map.get(brandPair);
                long qty = salesReport.getQuantity() + data.getQuantity();
                double revenue = salesReport.getRevenue() + data.getRevenue();
                salesReport.setQuantity(qty);
                salesReport.setRevenue(revenue);
                map.put(brandPair,salesReport);
            }else {
                map.put(brandPair,data);
            }
        }
        List<SalesReport> salesReportList = new ArrayList<>(map.values());
        return salesReportList;
    }
    public List<Integer> getOrderIdList(List<OrderPojo> orderPojoList){
        List<Integer> orderIdList = new ArrayList<>();
        for( OrderPojo order : orderPojoList){
            orderIdList.add(order.getId());
        }
        return orderIdList;
    }
    public List<InventoryReport> getInventoryReport() {
        return inventoryServices.getInventoryReport();
    }

    private static String jaxbObjectToXML(OrderItemDataList orderItemList)
    {
        try
        {
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

    private void convertToPDF(OrderItemDataList team, File xslt, File pdf,String xml)
            throws IOException, TransformerException{

        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        // configure foUserAgent as desired

        // Setup output
        OutputStream out = new java.io.FileOutputStream(pdf);
        out = new java.io.BufferedOutputStream(out);
        try {
            // Construct fop with desired output format
            Fop fop = null;
            try {
                fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
            } catch (FOPException e) {
                throw new RuntimeException(e);
            }

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xslt));

            // Setup input for XSLT transformation
            Source src = new StreamSource(new StringReader(xml));
//                        Source src = new StreamSource(new FileInputStream(xml));
//
            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);
        } catch (FOPException e) {
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    public List<OrderItemData> getOrderItemForOrder(Integer orderId) throws ApiException{
        List<OrderItemPojo> orderItemPojoList = getOrderItemForOrderUtil(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for(OrderItemPojo i : orderItemPojoList){
            OrderItemData j = convertPojoToOrderData(i);
            orderItemDataList.add(j);
        }
        return orderItemDataList;
    }
    @Transactional(rollbackOn = ApiException.class)
    private List<OrderItemPojo> getOrderItemForOrderUtil(Integer orderId) throws ApiException {
        OrderPojo oPojo = orderServices.get(orderId);
        if (oPojo == null) {
            throw new ApiException("Order with this id does not exist, id : " + orderId);
        }
        return orderItemServices.selectFromOrderId(orderId);

    }
}
