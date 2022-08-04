package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.dto.OrderDto;
import pos.model.OrderData;
import pos.model.SalesReport;
import pos.model.SalesReportForm;
import pos.spring.ApiException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@Api
@RestController
public class OrderApiController {

    @Autowired
    private OrderDto dto;

    @ApiOperation(value = "Gives all Order data")
    @RequestMapping(path = "/api/orders", method = RequestMethod.GET)
    public List<OrderData> getAllOrderDetails() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Insert Order data")
    @RequestMapping(path = "/api/orders", method = RequestMethod.POST)
    public ZonedDateTime insertOrder() throws ApiException {
        return dto.add();
    }

    @ApiOperation(value = "get a Order")
    @RequestMapping(path = "/api/orders/{id}", method = RequestMethod.GET)
    public OrderData getOrder(@PathVariable int id) throws ApiException {
        return dto.get(id);

    }

    @ApiOperation(value = "set order status placed")
    @RequestMapping(path = "/api/orders/{id}/place-order", method = RequestMethod.PUT)
    public Integer setOrderStatusPlaced(@PathVariable int id) throws ApiException {
        return dto.updateOrderStatusPlaced(id);
    }

    @ApiOperation(value = "Get order invoice for orderId")
    @RequestMapping(path = "/api/orders/{orderId}/invoices", method = RequestMethod.GET)
    public BufferedInputStream getOrderInvoice(@PathVariable int orderId) throws ApiException, IOException, TransformerException {
        return dto.getOrderInvoice(orderId);
    }

    @ApiOperation(value = "Get Sales report ")
    @RequestMapping(path = "/api/orders/sales-reports", method = RequestMethod.POST) //no entity in sales
    public List<SalesReport> getsSalesReport(@RequestBody SalesReportForm s) throws ApiException {
        return dto.getSalesReport(s);
    }

}
