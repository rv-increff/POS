package pos.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.model.InventoryData;
import pos.model.InventoryForm;
import pos.model.InventoryUpdateForm;
import pos.services.ApiException;
import pos.services.InventoryServices;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class InventoryApiController {


    @Autowired
    private InventoryServices service;

    @ApiOperation(value = "Gives all Inventory data")
    @RequestMapping(path = "/api/inventory/get-all", method = RequestMethod.GET)
    public List<InventoryData> getAllInventoryDetails() throws ApiException {
        return service.getAll();
    }

    @ApiOperation(value = "Insert Inventory data")
    @RequestMapping(path = "/api/inventory/insert", method = RequestMethod.POST)
    public void insertInventory(@RequestBody InventoryForm p, HttpServletResponse response) throws ApiException, IOException {
        service.add(p);
        success(response);
    }

    @ApiOperation(value = "Insert bulk Inventory data")
    @RequestMapping(path = "/api/inventory/bulk-insert", method = RequestMethod.POST)
    public void bulkInsertInventory(@RequestBody List<InventoryForm> p, HttpServletResponse response) throws ApiException, IOException {
        service.bulkAdd(p);
        success(response);
    }

    @ApiOperation(value = "get a Inventory")
    @RequestMapping(path = "/api/inventory/get/{id}", method = RequestMethod.GET)
    public InventoryData getInventory(@PathVariable int id) throws ApiException {
        return service.get(id);

    }

    @ApiOperation(value = "update a Inventory")
    @RequestMapping(path = "/api/inventory/update", method = RequestMethod.PUT)
    public void updateInventory(@RequestBody InventoryUpdateForm p, HttpServletResponse response) throws ApiException, IOException {
        service.update(p);
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
