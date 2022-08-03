package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import pos.dto.BrandDto;
import pos.dto.InvoiceDto;
import pos.model.BrandData;
import pos.model.InventoryReport;
import pos.model.SalesReport;
import pos.model.SalesReportForm;
import pos.services.BrandServices;
import pos.spring.ApiException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.text.ParseException;
import java.util.List;

@Api
@RestController
public class InvoiceApiController {

    @Autowired
    private InvoiceDto inventoryDto;

    @Autowired
    private BrandServices brandService;

    @Autowired
    private BrandDto brandDto;

    @ApiOperation(value = "Get order invoice for orderId")
    @RequestMapping(path = "/api/orders/{orderId}/invoices", method = RequestMethod.GET)
    public void getOrderInvoice(@PathVariable int orderId,HttpServletResponse response) throws ApiException, IOException, TransformerException {
        inventoryDto.getOrderInvoice(orderId);
        String path = "/Users/rahulverma/Downloads/git/POS/src/invoice.pdf";
        File file = new File(path);
        if (file.exists()) {
            String mimeType = "application/pdf";
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
            response.setContentLength((int) file.length());
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
    }

    @ApiOperation(value = "Get Sales report ")
    @RequestMapping(path = "/api/orders/sales-reports/invoices", method = RequestMethod.POST) //no entity in sales
    public List<SalesReport> getsSalesReport(@RequestBody SalesReportForm s) throws ApiException, ParseException {
        return inventoryDto.getSalesReport(s);
    }

    @ApiOperation(value = "Get Brand report ")
    @RequestMapping(path = "/api/brands/brand-reports/invoices", method = RequestMethod.GET)
    public List<BrandData> getsBrandReport() throws ApiException {
        return brandDto.getAll();
    }

    @ApiOperation(value = "Get Inventory report ")
    @RequestMapping(path = "/api/inventory/inventory-reports/invoices", method = RequestMethod.GET)
    public List<InventoryReport> getInventoryReport() throws ApiException {
        return inventoryDto.getInventoryReport();
    }



}
