package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.dto.BrandDto;
import pos.model.BrandData;
import pos.model.BrandForm;
import pos.spring.ApiException;

import java.util.List;

@Api
@RestController
public class BrandApiController {

    @Autowired
    private BrandDto dto;

    @ApiOperation(value = "Gives all brand data")
    @RequestMapping(path = "/api/brands", method = RequestMethod.GET)
    public List<BrandData> getAllBrandDetails() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Insert brand data")
    @RequestMapping(path = "/api/brands", method = RequestMethod.POST)
    public BrandForm insertBrand(@RequestBody BrandForm brandForm) throws ApiException {
        return dto.add(brandForm);
    }

    @ApiOperation(value = "Insert bulk brand data")
    @RequestMapping(path = "/api/brands/upload", method = RequestMethod.POST)
    public Integer bulkInsertBrand(@RequestBody List<BrandForm> brandFormList) throws ApiException {
        return dto.bulkAdd(brandFormList);
    }

    @ApiOperation(value = "get a brand")
    @RequestMapping(path = "/api/brands/{id}", method = RequestMethod.GET)
    public BrandData getBrand(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "update a brand")
    @RequestMapping(path = "/api/brands/{id}", method = RequestMethod.PUT)
    public BrandData updateBrand(@RequestBody BrandData brandData) throws ApiException {
        return dto.update(brandData);
        //TODO do not use this  return brand data and take form and id
    }

    @ApiOperation(value = "Get Brand report ")
    @RequestMapping(path = "/api/brands/brand-reports", method = RequestMethod.GET)
    public List<BrandData> getsBrandReport() throws ApiException {
        return dto.getAll();
    }

}
