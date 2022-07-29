package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.InventoryReport;
import pos.model.SalesReport;
import pos.model.SalesReportForm;
import pos.services.InvoiceServices;
import pos.spring.ApiException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public class InvoiceDto {

    @Autowired
    InvoiceServices services;

    public void getOrderInvoice(int orderId) throws ApiException, IOException, TransformerException{

        services.getOrderInvoice(orderId);
    }

    public List<SalesReport> getSalesReport(SalesReportForm s) throws ApiException, ParseException{
        return services.getSalesReport(s);
    }

    public List<InventoryReport> getInventoryReport() throws ApiException{
        return services.getInventoryReport();
    }
}
