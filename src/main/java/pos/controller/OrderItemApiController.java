package pos.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.dto.OrderItemDto;
import pos.model.OrderItemData;
import pos.model.OrderItemForm;
import pos.model.OrderItemUpdateForm;
import pos.spring.ApiException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class OrderItemApiController {

    @Autowired
    private OrderItemDto dto;

    @ApiOperation(value = "Gives all OrderItem data")
    @RequestMapping(path = "/orders/order-items", method = RequestMethod.GET)
    public List<OrderItemData> getAllProductDetails() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Gives all OrderItem data for an order")
    @RequestMapping(path = "/orders/{orderId}/order-items", method = RequestMethod.GET)
    public List<OrderItemData> getOrderItemForOrder(@PathVariable Integer orderId) throws ApiException {
        return dto.getOrderItemForOrder(orderId);
    }

    @ApiOperation(value = "Insert OrderItem data")
    @RequestMapping(path = "/orders/order-items", method = RequestMethod.POST)
    public OrderItemForm insertOrderItem(@RequestBody OrderItemForm orderItemForm) throws ApiException{
        return dto.add(orderItemForm);
    }

    @ApiOperation(value = "Update OrderItem data")
    @RequestMapping(path = "/orders/order-items/{id}", method = RequestMethod.PUT)
    public OrderItemUpdateForm updateOrderItem(@RequestBody OrderItemUpdateForm orderItemUpdateForm,@PathVariable Integer id) throws ApiException{
        return dto.update(orderItemUpdateForm);
    }

    @ApiOperation(value = "Delete OrderItem data")
    @RequestMapping(path = "/orders/order-items/{id}", method = RequestMethod.DELETE)
    public Integer deleteOrderItem(@PathVariable int id) throws ApiException{
        return dto.delete(id);
    }

}
