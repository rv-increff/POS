package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pos.model.InventoryData;
import pos.model.InventoryForm;
import pos.model.InventoryReport;
import pos.model.InventoryUpdateForm;
import pos.pojo.InventoryPojo;
import pos.pojo.ProductPojo;
import pos.services.InventoryServices;
import pos.services.ProductServices;
import pos.spring.ApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static pos.util.DataUtil.checkNotNullBulkUtil;
import static pos.util.DataUtil.validate;
import static pos.util.ErrorUtil.throwError;
import static pos.util.HelperUtil.convertInventoryFormToInventoryPojo;
import static pos.util.HelperUtil.convertPojoToInventoryForm;

@Service
public class InventoryDto {

    @Autowired
    private InventoryServices service;
    @Autowired
    private ProductServices productService;

    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> inventoryPojoList = service.getAll();
        List<InventoryData> inventoryData = new ArrayList<>();
        for (InventoryPojo pj : inventoryPojoList) {
            inventoryData.add(convertPojoToInventoryForm(pj));
        }

        return inventoryData;
    }

    public InventoryForm add(InventoryForm inventoryForm) throws ApiException {
        validate(inventoryForm, "Barcode or quantity cannot be NULL");
        InventoryPojo inventoryPojo = convertInventoryFormToInventoryPojo(inventoryForm);

        service.add(addProductId(inventoryPojo));
        return inventoryForm;
    }

    public Integer bulkAdd(List<InventoryForm> inventoryFormList) throws ApiException {
        if (CollectionUtils.isEmpty(inventoryFormList)) {
            throw new ApiException("Empty data");
        }
        validateInventoryList(inventoryFormList);
        List<InventoryPojo> inventoryPojoList = new ArrayList<>();
        for (InventoryForm inventoryForm : inventoryFormList) {
            InventoryPojo inventoryPojo = convertInventoryFormToInventoryPojo(inventoryForm);
            inventoryPojoList.add(addProductId(inventoryPojo));
        }

        service.bulkAdd(inventoryPojoList);

        return inventoryPojoList.size();
    }

    public InventoryData get(Integer id) throws ApiException {
        return convertPojoToInventoryForm(service.get(id));
    }

    public InventoryUpdateForm update(InventoryUpdateForm inventoryUpdateForm) throws ApiException {
        validate(inventoryUpdateForm, "Quantity cannot be NULL");
        service.update(inventoryUpdateForm);

        return inventoryUpdateForm;
    }

    public List<InventoryReport> getInventoryReport() {
        return service.getInventoryReport();
    }

    private void validateInventoryList(List<InventoryForm> inventoryFormList) throws ApiException {
        List<String> errorList = new ArrayList<>();
        Set<String> barcodeSet = new HashSet<>();
        Integer row = 1;
        for (InventoryForm inventoryForm : inventoryFormList) {

            if (!checkNotNullBulkUtil(inventoryForm)) {
                errorList.add("Error : row -> " + (row) + " barcode or quantity cannot be NULL");
                continue;
            }
            ProductPojo productPojo = productService.selectByBarcode(inventoryForm.getBarcode());

            if (isNull(productPojo)) {
                errorList.add("Error : row -> " + (row) + " product with the barcode " + inventoryForm.getBarcode() + " does not exist");
            } //TODO extra call made should be optimized?

            if (barcodeSet.contains(inventoryForm.getBarcode())) {
                errorList.add("Error : row -> " + (row) + " Barcode should not be repeated, barcode : " + inventoryForm.getBarcode());
            } else {
                barcodeSet.add(inventoryForm.getBarcode());
            }
        }

        if (!CollectionUtils.isEmpty(errorList)) throwError(errorList);

    }

    private InventoryPojo addProductId(InventoryPojo inventoryPojo) throws ApiException {
        ProductPojo productPojo = productService.selectByBarcode(inventoryPojo.getBarcode());

        if (isNull(productPojo)) {
            throw new ApiException("Product with this barcode does not exist");
        }
        int productId = productPojo.getId();
        inventoryPojo.setProductId(productId);
        return inventoryPojo;
    }


}
