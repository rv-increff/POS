package pos.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.dto.ProductDto;
import pos.model.ProductData;
import pos.model.ProductForm;
import pos.model.ProductUpdateForm;
import pos.spring.ApiException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class ProductApiController {
    @Autowired
    private ProductDto dto;

    @ApiOperation(value = "say hi")
    @RequestMapping(path = "/api/products/smoke", method = RequestMethod.GET)
    public String hi() throws ApiException {
        return "smoke test";
    }

    @ApiOperation(value = "Gives all Product data")
    @RequestMapping(path = "/api/products", method = RequestMethod.GET)
    public List<ProductData> getAllProductDetails() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Insert Product data")
    @RequestMapping(path = "/api/products", method = RequestMethod.POST)
    public void insertProduct(@RequestBody ProductForm p, HttpServletResponse response) throws ApiException, IOException {
        dto.add(p);
        success(response);
    }

    @ApiOperation(value = "Insert bulk Product data")
    @RequestMapping(path = "/api/products/bulk-upload", method = RequestMethod.POST)
    public void bulkInsertProduct(@RequestBody List< @Valid ProductForm> p, HttpServletResponse response) throws ApiException, IOException {
        dto.bulkAdd(p);
        success(response);
    }

    @ApiOperation(value = "get a Product")
    @RequestMapping(path = "/api/products/{id}", method = RequestMethod.GET)
    public ProductData getProduct(@PathVariable int id) throws ApiException {
        return dto.get(id);

    }
    @ApiOperation(value = "update a Product")
    @RequestMapping(path = "/api/products", method = RequestMethod.PUT)
    public void updateProduct( @RequestBody ProductUpdateForm p, HttpServletResponse response) throws ApiException, IOException {
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
