package pos.controller;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import pos.model.BrandInsertForm;
import pos.model.BrandData;
import pos.services.ApiException;
import pos.services.BrandServices;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class BrandApiController {

    @Autowired
    private BrandServices service;

    @ApiOperation(value = "say hi")
    @RequestMapping(path = "/api/brand/smoke", method = RequestMethod.GET)
    public String hi() throws ApiException {
        return "smoke test";
    }

    @ApiOperation(value = "Gives all brand data")
    @RequestMapping(path = "/api/brand/get-all", method = RequestMethod.GET)
    public List<BrandData> getAllBrandDetails() throws ApiException {
        return service.getAll();
    }

    @ApiOperation(value = "Insert brand data")
    @RequestMapping(path = "/api/brand/insert", method = RequestMethod.POST)
    public void insertBrand(@RequestBody BrandInsertForm p, HttpServletResponse response) throws ApiException, IOException {
        service.add(p);
        success(response);
    }

    @ApiOperation(value = "Insert bulk brand data")
    @RequestMapping(path = "/api/brand/bulk-insert", method = RequestMethod.POST)
    public void bulkInsertBrand(@RequestBody List<BrandInsertForm> p, HttpServletResponse response) throws ApiException, IOException {
        service.bulkAdd(p);
        success(response);
    }

    @ApiOperation(value = "get a brand")
    @RequestMapping(path = "/api/brand/get/{id}", method = RequestMethod.GET)
    public BrandData getBrand(@PathVariable int id) throws ApiException {
        return service.get(id);

    }

    @ApiOperation(value = "delete a brand")
    @RequestMapping(path = "/api/brand/delete/{id}", method = RequestMethod.DELETE)
    public void deleteBrand(@PathVariable int id, HttpServletResponse response) throws ApiException, IOException {
        service.delete(id);
        success(response);
    }

    @ApiOperation(value = "update a brand")
    @RequestMapping(path = "/api/brand/update", method = RequestMethod.PUT)
    public void updateBrand(@RequestBody BrandData p, HttpServletResponse response) throws ApiException, IOException {
        service.update(p);
        success(response);
    }

    protected void success(HttpServletResponse response) throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("message", "success");
        String json = new Gson().toJson(obj);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }



}
