package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pos.model.ProductData;
import pos.model.ProductForm;
import pos.model.ProductUpdateForm;
import pos.pojo.BrandPojo;
import pos.pojo.ProductPojo;
import pos.services.BrandServices;
import pos.services.InventoryServices;
import pos.services.ProductServices;
import pos.spring.ApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static pos.util.DataUtil.*;
import static pos.util.ErrorUtil.throwError;
import static pos.util.HelperUtil.convertFormToProductPojo;
import static pos.util.HelperUtil.convertPojoToProductData;

@Service
public class ProductDto {

    @Autowired
    private ProductServices service;
    @Autowired
    private BrandServices brandService;
    @Autowired
    private InventoryServices inventoryServices;

    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> productPojoList = service.getAll();
        List<ProductData> productData = new ArrayList<>();
        for (ProductPojo productPojo : productPojoList) {
            productData.add(convertPojoToProductData(productPojo));
        }

        return productData;
    }

    public ProductForm add(ProductForm productForm) throws ApiException {
        validate(productForm, "parameters in the Insert form cannot be null");

        Integer brandId = getBrandIdByBrandCategory(productForm.getBrand(), productForm.getCategory());
        ProductPojo productPojo = convertFormToProductPojo(productForm);
        productPojo.setBrandId(brandId);

        service.add(productPojo);
        return productForm;
    }

    public Integer bulkAdd(List<ProductForm> productFormList) throws ApiException {
        if (CollectionUtils.isEmpty(productFormList)) {
            throw new ApiException("Empty data");
        }
        checkDuplicates(productFormList);
        validateProductList(productFormList); //cannot do this in service as we need to call other dto/service
        List<ProductPojo> productPojoList = new ArrayList<>();

        for (ProductForm productForm : productFormList) {
            Integer brandId = getBrandIdByBrandCategory(productForm.getBrand(), productForm.getCategory());
            ProductPojo productPojo = convertFormToProductPojo(productForm);
            productPojo.setBrandId(brandId);
            productPojoList.add(productPojo);
        }

        service.bulkAdd(productPojoList);
        return productFormList.size();
    }

    public ProductData get(Integer id) throws ApiException {
        return convertPojoToProductData(service.get(id));
    }

    public ProductUpdateForm update(ProductUpdateForm productUpdateForm) throws ApiException {
        validate(productUpdateForm, "parameters in the Update form cannot be null");
        ProductPojo productPojo = service.get(productUpdateForm.getId());
        if (!isNull(inventoryServices.selectByBarcode(productUpdateForm.getBarcode())) & productUpdateForm.getBarcode() != productPojo.getBarcode()) {
            throw new ApiException("cannot change barcode as Inventory exist for this");
        }

        Integer brandId = getBrandIdByBrandCategory(productUpdateForm.getBrand(), productUpdateForm.getCategory());
        ProductPojo productPojoConverted = convertFormToProductPojo(productUpdateForm);
        productPojoConverted.setBrandId(brandId);

        service.update(productPojoConverted);
        return productUpdateForm;
    }

    private Integer getBrandIdByBrandCategory(String brand, String category) throws ApiException {
        BrandPojo brandPojo = brandService.selectByBrandCategory(brand, category);
        if (isNull(brandPojo)) {
            throw new ApiException(brand + " - " + category + " brand-category does not exist");
        }

        return brandPojo.getId();
    }

    private void checkDuplicates(List<ProductForm> productFormList) {
        Set<String> barcodeSet = new HashSet<>();
        List<String> errorList = new ArrayList<>();
        Integer row = 1;
        for (ProductForm p : productFormList) {
            if (barcodeSet.contains(p.getBarcode())) {
                errorList.add("Error : row -> " + row + " Barcode should not be repeated, barcode : " + p.getBarcode());
            } else {
                barcodeSet.add(p.getBarcode());
            }
            row++;
        }
    }

    public void validateProductList(List<ProductForm> productFormList) throws ApiException {
        List<String> errorList = new ArrayList<>();
        Integer row = 1;
        for (ProductForm productForm : productFormList) {
            normalize(productForm);
            if (!checkNotNullBulkUtil(productForm)) {
                errorList.add("Error : row -> " + row + " parameters in the Insert form cannot be null");
                continue;
            }
            if (!validateMRPBulk(productForm.getMrp())) {
                errorList.add("Error : row -> " + row + " mrp " + productForm.getMrp() + " not valid, mrp should be a positive number");
            }
            if (!validateBarcodeBulk(productForm.getBarcode())) {
                errorList.add("Error : row -> " + row + " barcode " + productForm.getBarcode() + " not valid, barcode can only have alphanumeric values");
            }
            if (!isNull(service.selectByBarcode(productForm.getBarcode()))) {
                errorList.add("Error : row -> " + row + " barcode " + productForm.getBarcode() + " already exists");
            }
            BrandPojo brandPojo = brandService.selectByBrandCategory(productForm.getBrand(), productForm.getCategory());
            if (isNull(brandPojo)) {
                errorList.add("Error : row -> " + row + " " + productForm.getBrand() + " - " + productForm.getCategory() + " brand-category does not exist");
            }
            row++;
        }
        if (!CollectionUtils.isEmpty(errorList)) {
            throwError(errorList);
        }
    }

}
