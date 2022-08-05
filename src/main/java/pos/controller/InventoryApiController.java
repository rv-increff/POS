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
import pos.model.InventoryReport;
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
    @RequestMapping(path = "/inventory", method = RequestMethod.GET) //TODO only get remove all
    public List<InventoryData> getAllInventoryDetails() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Insert Inventory data")
    @RequestMapping(path = "/inventory", method = RequestMethod.POST) //TODO remove insert as its post
    public InventoryForm insertInventory(@RequestBody InventoryForm inventoryForm) throws ApiException{
        return dto.add(inventoryForm);
    }

    @ApiOperation(value = "Insert bulk Inventory data")
    @RequestMapping(path = "/inventory/upload", method = RequestMethod.POST) //TODO upload not bulk-
    public Integer bulkInsertInventory(@RequestBody List<InventoryForm> inventoryFormList) throws ApiException{
       return dto.bulkAdd(inventoryFormList);

    }

    @ApiOperation(value = "get a Inventory")
    @RequestMapping(path = "/inventory/{id}", method = RequestMethod.GET) //TODO remove get read REST conventions
    public InventoryData getInventory(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "update a Inventory")
    @RequestMapping(path = "/inventory/{id}", method = RequestMethod.PUT)
    public InventoryUpdateForm updateInventory(@RequestBody InventoryUpdateForm inventoryUpdateForm,@PathVariable Integer id) throws ApiException{
        return dto.update(inventoryUpdateForm);  //TODO return inv data
    }
    @ApiOperation(value = "Get Inventory report ")
    @RequestMapping(path = "/inventory/inventory-reports", method = RequestMethod.GET)
    public List<InventoryReport> getInventoryReport() {
        return dto.getInventoryReport();
    }

}
