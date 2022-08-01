package pos.dto;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.InventoryReport;
import pos.model.SalesReport;
import pos.model.SalesReportForm;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;
import pos.services.*;
import pos.spring.ApiException;
import javax.transaction.Transactional;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceDto {

    @Autowired
    InvoiceServices invoiceServices;
    @Autowired
    BrandServices brandServices;
    @Autowired
    OrderServices orderServices;
    @Autowired
    OrderItemServices orderItemServices;
    @Autowired
    ProductServices productServices;

    public void getOrderInvoice(int orderId) throws ApiException, IOException, TransformerException{

        invoiceServices.getOrderInvoice(orderId);
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
        List<OrderPojo> orderList = orderServices.selectByFromTODate(s.getFrom(),s.getTo());
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
        return invoiceServices.getInventoryReport();
    }
}
