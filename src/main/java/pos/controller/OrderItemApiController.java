package pos.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.model.*;
import pos.pojo.OrderPojo;
import pos.services.ApiException;
import pos.services.OrderItemServices;
import pos.services.OrderServices;
import pos.services.ProductServices;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class OrderItemApiController {

    @Autowired
    private OrderItemServices service;


    @ApiOperation(value = "Gives all OrderItem data")
    @RequestMapping(path = "/api/order-item/get-all", method = RequestMethod.GET)
    public List<OrderItemData> getAllProductDetails() throws ApiException {
        return service.getAll();
    }

    @ApiOperation(value = "Gives all OrderItem data for an order")
    @RequestMapping(path = "/api/order-item/get-all/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> getOrderItemForOrder(@PathVariable int orderId) throws ApiException {
        return service.getOrderItemForOrder(orderId);
    }

    @ApiOperation(value = "Insert OrderItem data")
    @RequestMapping(path = "/api/order-item/insert", method = RequestMethod.POST)
    public void insertOrderItem(@RequestBody OrderItemInsertForm p, HttpServletResponse response) throws ApiException, IOException {
        service.add(p);
        success(response);
    }

    @ApiOperation(value = "Update OrderItem data")
    @RequestMapping(path = "/api/order-item/update", method = RequestMethod.PUT)
    public void updateOrderItem(@RequestBody OrderItemUpdateForm p, HttpServletResponse response) throws ApiException, IOException {
        service.update(p);
        success(response);
    }

    @ApiOperation(value = "Update OrderItem data")
    @RequestMapping(path = "/api/order-item/delete/{id}", method = RequestMethod.DELETE)
    public void deleteOrderItem(@PathVariable int id, HttpServletResponse response) throws ApiException, IOException {
        service.delete(id);
        success(response);
    }




    protected void success(HttpServletResponse response) throws IOException {
        JSONObject obj=new JSONObject();
        obj.put("message", "success");
        String json = new Gson().toJson(obj);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
