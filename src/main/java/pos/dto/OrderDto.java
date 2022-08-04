package pos.dto;

import javafx.util.Pair;
import org.apache.fop.apps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import pos.model.*;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;
import pos.services.BrandServices;
import pos.services.OrderItemServices;
import pos.services.OrderServices;
import pos.services.ProductServices;
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

import static java.util.Objects.isNull;
import static pos.util.HelperUtil.*;

@Service
public class OrderDto {
    private final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
    @Autowired
    OrderServices orderServices;
    @Autowired
    OrderItemServices orderItemServices;
    @Autowired
    BrandServices brandServices;
    @Autowired
    ProductServices productServices;


    public List<OrderData> getAll() throws ApiException {
        List<OrderPojo> orderPojoList = orderServices.getAll();
        List<OrderData> orderDataList = new ArrayList<>();
        for (OrderPojo orderPojo : orderPojoList) {
            orderDataList.add(convertPojoToOrderData(orderPojo));
        }
        return orderDataList;
    }

    public ZonedDateTime add() throws ApiException {
        return orderServices.add();

    }

    public OrderData get(Integer id) throws ApiException {
        return convertPojoToOrderData(orderServices.get(id));
    }

    public Integer updateOrderStatusPlaced(Integer id) throws ApiException {
        orderServices.updateOrderStatusPlaced(id);
        return id;
    }

    public BufferedInputStream getOrderInvoice(int orderId) throws ApiException, IOException, TransformerException { //TODO move to its own dto
        List<OrderItemPojo> orderItemPojoList = getOrderItemForOrderUtil(orderId); //TODO remove invoice controller
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo i : orderItemPojoList) {
            OrderItemData j = convertPojoToOrderItemData(i);
            orderItemDataList.add(j);
        }

        ZonedDateTime time = orderServices.get(orderId).getTime();
        Double total = 0.;

        for (OrderItemData i : orderItemDataList) {
            total += i.getQuantity() * i.getSellingPrice();
        }
        OrderItemDataList oItem = new OrderItemDataList(orderItemDataList, time, total, orderId);


        String xml = jaxbObjectToXML(oItem);
        File xsltFile = new File("src", "invoice.xsl");
        File pdfFile = new File("src", "invoice.pdf");
        System.out.println(xml);
        convertToPDF(oItem, xsltFile, pdfFile, xml);
        return returnFileStream();
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<SalesReport> getSalesReport(SalesReportForm s) throws ApiException {

        ZonedDateTime from = s.getFrom();
        ZonedDateTime to = s.getTo();

        if (ChronoUnit.DAYS.between(from, to) > 30) {
            throw new ApiException("Date range should not be greater than 30 days");
        }

        if (ChronoUnit.DAYS.between(from, to) <= 0) {
            throw new ApiException("From date should be greater than To date");
        }

        if (s.getBrand() != "" & s.getCategory() != "" & brandServices.selectByBrandCategory(s.getBrand(), s.getCategory()) == null) {
            throw new ApiException(s.getBrand() + " - " + s.getCategory() + " Brand-category pair does not exist");
        }

        if (s.getCategory() == "" & s.getBrand() != "" & brandServices.selectByBrand(s.getBrand()) == null) {
            throw new ApiException("Brand does not exist");
        }

        if (s.getBrand() == "" & s.getCategory() != "" & brandServices.selectByCategory(s.getCategory()) == null) {
            throw new ApiException("Brand does not exist");
        }
        List<SalesReport> salesReportData = getSalesReportUtil(s);
        if (CollectionUtils.isEmpty(salesReportData)) {
            throw new ApiException("No sales for given range");
        }
        return salesReportData;

    }

    public List<SalesReport> getSalesReportUtil(SalesReportForm s) throws ApiException {
        //get order
        List<OrderPojo> orderList = orderServices.selectByFromToDate(s.getFrom(), s.getTo());
        List<Integer> orderIdList = new ArrayList<>();
        for (OrderPojo order : orderList) {
            orderIdList.add(order.getId());
        }
        List<OrderItemPojo> orderItemList;
        if (CollectionUtils.isEmpty(orderIdList)) {
            throw new ApiException("No sales");
        }

        orderItemList = orderItemServices.selectFromOrderIdList(orderIdList);

        List<ProductPojo> productPojoList;

        if (s.getBrand() != "" && s.getCategory() != "") {
            productPojoList = productServices.getByBrandAndCategory(s.getBrand(), s.getCategory());
        } else if (s.getBrand() == "" & s.getCategory() != "") {
            productPojoList = productServices.getByCategory(s.getCategory());
        } else if (s.getCategory() == "" & s.getBrand() != "") {
            productPojoList = productServices.getByBrand(s.getBrand());
        } else {
            productPojoList = productServices.getAll();
        }
        List<SalesReport> salesReportDataSparse = getSalesData(orderItemList, productPojoList);
        List<SalesReport> salesReportData = getGroupedData(salesReportDataSparse);
        return salesReportData;

    }

    private List<SalesReport> getSalesData(List<OrderItemPojo> orderItemPojoList, List<ProductPojo> productPojoList) {
        List<SalesReport> commonPojo = new ArrayList<>();

        Map<Integer, ProductPojo> productMap = new HashMap<>();

        for (ProductPojo productPojo : productPojoList) {
            productMap.put(productPojo.getId(), productPojo);
        }

        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            if (productMap.containsKey(orderItemPojo.getProductId())) {

                ProductPojo temp = productMap.get(orderItemPojo.getProductId());
                SalesReport salesReport = new SalesReport(temp.getBrand(), temp.getCategory(), (long) orderItemPojo.getQuantity(), orderItemPojo.getSellingPrice() * orderItemPojo.getQuantity());
                commonPojo.add(salesReport);
            }
        }

        return commonPojo;
    }

    private List<SalesReport> getGroupedData(List<SalesReport> salesReportDataSparse) {
        Map<Pair<String, String>, SalesReport> map = new HashMap<>();
        for (SalesReport data : salesReportDataSparse) {
            Pair brandPair = new Pair(data.getBrand(), data.getCategory());
            if (map.containsKey(brandPair)) {
                SalesReport salesReport = map.get(brandPair);
                long qty = salesReport.getQuantity() + data.getQuantity();
                double revenue = salesReport.getRevenue() + data.getRevenue();
                salesReport.setQuantity(qty);
                salesReport.setRevenue(revenue);
                map.put(brandPair, salesReport);
            } else {
                map.put(brandPair, data);
            }
        }
        List<SalesReport> salesReportList = new ArrayList<>(map.values());
        return salesReportList;
    }

    @Transactional(rollbackOn = ApiException.class)
    private List<OrderItemPojo> getOrderItemForOrderUtil(Integer orderId) throws ApiException {
        OrderPojo oPojo = orderServices.get(orderId);
        if (isNull(oPojo)) {
            throw new ApiException("Order with this id does not exist, id : " + orderId);
        }
        return orderItemServices.selectFromOrderId(orderId);

    }

    private OrderData convertPojoToOrderData(OrderPojo p) {
        OrderData orderData = new OrderData();
        orderData.setId(p.getId());
        orderData.setTime(p.getTime().toLocalDateTime().toString());
        orderData.setOrderPlaced(p.getOrderPlaced());
        return orderData;
    }

    private void convertToPDF(OrderItemDataList team, File xslt, File pdf, String xml) throws IOException, TransformerException {

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

}
