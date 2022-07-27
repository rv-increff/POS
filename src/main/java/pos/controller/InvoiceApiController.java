package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.fop.apps.FOPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import pos.model.*;
import pos.services.ApiException;
import pos.services.BrandServices;
import pos.services.InvoiceServices;
import pos.services.OrderServices;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
public class InvoiceApiController {

    @Autowired
    private InvoiceServices service;

    @Autowired
    private BrandServices bService;

    @ApiOperation(value = "Get order invoice for orderId")
    @RequestMapping(path = "/api/invoices/get-order-invoice/{orderId}", method = RequestMethod.GET)
    public void getOrderInvoice(@PathVariable int orderId,HttpServletResponse response) throws ApiException, IOException, TransformerException {
        service.getOrderInvoice(orderId);
        String path = "/Users/rahulverma/Downloads/POS/src/invoice.pdf";
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
    @RequestMapping(path = "/api/invoices/get-sales", method = RequestMethod.POST)
    public List<SalesReport> getsSalesReport(@RequestBody SalesReportForm s) throws ApiException, ParseException {
        return service.getSalesReport(s);
    }


    @ApiOperation(value = "Get Brand report ")
    @RequestMapping(path = "/api/invoices/get-brand-report", method = RequestMethod.GET)
    public List<BrandData> getsBrandReport() throws ApiException {
        return bService.getAll();
    }

    @ApiOperation(value = "Get Inventory report ")
    @RequestMapping(path = "/api/invoices/get-inventory-report", method = RequestMethod.GET)
    public List<InventoryReport> getInventoryReport() throws ApiException {
        return service.getInventoryReport();
    }



}