package pos.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.dto.BrandDto;
import pos.model.BrandData;
import pos.model.BrandForm;
import pos.spring.ApiException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class BrandApiController {

    @Autowired
    private BrandDto dto;

    @ApiOperation(value = "say hi")
    @RequestMapping(path = "/api/brands/smoke", method = RequestMethod.GET)
    public String hi() throws ApiException {
        return "smoke test";
    }

    @ApiOperation(value = "Gives all brand data")
    @RequestMapping(path = "/api/brands", method = RequestMethod.GET)
    public List<BrandData> getAllBrandDetails() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Insert brand data")
    @RequestMapping(path = "/api/brands", method = RequestMethod.POST)
    public void insertBrand(@RequestBody BrandForm p, HttpServletResponse response) throws ApiException, IOException {
        dto.add(p);
        success(response);
    }

    @ApiOperation(value = "Insert bulk brand data")
    @RequestMapping(path = "/api/brands/bulk-upload", method = RequestMethod.POST)
    public void bulkInsertBrand(@RequestBody List<BrandForm> p, HttpServletResponse response) throws ApiException, IOException {
        dto.bulkAdd(p);
        success(response);
    }

    @ApiOperation(value = "get a brand")
    @RequestMapping(path = "/api/brands/{id}", method = RequestMethod.GET)
    public BrandData getBrand(@PathVariable int id) throws ApiException {
        return dto.get(id);

    }

    @ApiOperation(value = "update a brand")
    @RequestMapping(path = "/api/brands", method = RequestMethod.PUT)
    public void updateBrand(@RequestBody BrandData p, HttpServletResponse response) throws ApiException, IOException {
        dto.update(p);
        success(response);//TODO do not use this
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
