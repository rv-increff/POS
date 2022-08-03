package pos.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pos.dto.OrderDto;
import pos.model.OrderData;
import pos.spring.ApiException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public void insertOrder(HttpServletResponse response) throws ApiException, IOException {
        dto.add();
        success(response);
    }

    @ApiOperation(value = "get a Order")
    @RequestMapping(path = "/api/orders/{id}", method = RequestMethod.GET)
    public OrderData getOrder(@PathVariable int id) throws ApiException {
        return dto.get(id);

    }
    @ApiOperation(value = "set order status placed")
    @RequestMapping(path = "/api/orders/{id}/place-order", method = RequestMethod.PUT)
    public void setOrderStatusPlaced(@PathVariable int id,HttpServletResponse response) throws ApiException, IOException {
        dto.updateOrderStatusPlaced(id);
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
