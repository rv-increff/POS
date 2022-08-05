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

    @ApiOperation(value = "Gives all Product data")
    @RequestMapping(path = "/products", method = RequestMethod.GET)
    public List<ProductData> getAllProductDetails() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Insert Product data")
    @RequestMapping(path = "/products", method = RequestMethod.POST)
    public ProductForm insertProduct(@RequestBody ProductForm p) throws ApiException{
        return dto.add(p);
    }

    @ApiOperation(value = "Insert bulk Product data")
    @RequestMapping(path = "/products/upload", method = RequestMethod.POST)
    public Integer bulkInsertProduct(@RequestBody List< @Valid ProductForm> p) throws ApiException {
        return dto.bulkAdd(p);
    }

    @ApiOperation(value = "get a Product")
    @RequestMapping(path = "/products/{id}", method = RequestMethod.GET)
    public ProductData getProduct(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }
    @ApiOperation(value = "update a Product")
    @RequestMapping(path = "/products", method = RequestMethod.PUT)
    public ProductUpdateForm updateProduct( @RequestBody ProductUpdateForm productUpdateForm) throws ApiException{
        return dto.update(productUpdateForm);
    }

}
