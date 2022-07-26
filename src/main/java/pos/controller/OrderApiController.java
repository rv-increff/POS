package pos.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.model.OrderData;
import pos.services.ApiException;
import pos.services.OrderServices;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class OrderApiController {

    @Autowired
    private OrderServices service;

    @ApiOperation(value = "Gives all Order data")
    @RequestMapping(path = "/api/order/get-all", method = RequestMethod.GET)
    public List<OrderData> getAllOrderDetails() throws ApiException {
        return service.getAll();
    }

    @ApiOperation(value = "Insert Order data")
    @RequestMapping(path = "/api/order/insert", method = RequestMethod.POST)
    public void insertOrder(HttpServletResponse response) throws ApiException, IOException {
        service.add();
        success(response);
    }

    @ApiOperation(value = "get a Order")
    @RequestMapping(path = "/api/order/get/{id}", method = RequestMethod.GET)
    public OrderData getOrder(@PathVariable int id) throws ApiException {
        return service.get(id);

    }
    @ApiOperation(value = "set order status placed")
    @RequestMapping(path = "/api/order/placed/{id}", method = RequestMethod.PUT)
    public void setOrderStatusPlaced(@PathVariable int id,HttpServletResponse response) throws ApiException, IOException {
        service.updateOrderStatusPlaced(id);
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
