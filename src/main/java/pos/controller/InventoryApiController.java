package pos.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.dto.InventoryDto;
import pos.model.InventoryData;
import pos.model.InventoryForm;
import pos.model.InventoryUpdateForm;
import pos.spring.ApiException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class InventoryApiController {

    @Autowired
    private InventoryDto dto;

    @ApiOperation(value = "Gives all Inventory data")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.GET) //TODO only get remove all
    public List<InventoryData> getAllInventoryDetails() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Insert Inventory data")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.POST) //TODO remove insert as its post
    public void insertInventory(@RequestBody InventoryForm p, HttpServletResponse response) throws ApiException, IOException {
        dto.add(p);
        success(response);
    }

    @ApiOperation(value = "Insert bulk Inventory data")
    @RequestMapping(path = "/api/inventory/bulk-upload", method = RequestMethod.POST) //TODO upload not bulk-
    public void bulkInsertInventory(@RequestBody List<InventoryForm> p, HttpServletResponse response) throws ApiException, IOException {
        dto.bulkAdd(p);
        success(response);
    }

    @ApiOperation(value = "get a Inventory")
    @RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.GET) //TODO remove get read REST conventions
    public InventoryData getInventory(@PathVariable int id) throws ApiException {
        return dto.get(id);

    }

    @ApiOperation(value = "update a Inventory")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.PUT)
    public void updateInventory(@RequestBody InventoryUpdateForm p, HttpServletResponse response) throws ApiException, IOException {
        dto.update(p);
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
