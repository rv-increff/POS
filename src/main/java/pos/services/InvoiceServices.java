package pos.services;

import org.apache.fop.apps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.dao.InventoryDao;
import pos.dao.OrderItemDao;
import pos.dto.OrderItemDto;
import pos.model.*;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class InvoiceServices {

    @Autowired
    private OrderItemDao dao ;
    @Autowired
    private BrandDao bDao;
    @Autowired
    private InventoryDao iDao;
    @Autowired
    private OrderItemServices oItemService;
    @Autowired
    private OrderServices oService;
    @Autowired
    private OrderItemDto ODto;

    private final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

    @Transactional(rollbackOn = ApiException.class)
    public List<SalesReport> getSalesReport(SalesReportForm s) throws ApiException, ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String from = s.getFrom();
        String to = s.getTo();
        if(s.getFrom() != "") {
            try {
//                s.setFrom(sdf.parse(s.getFrom()).toString());
                sdf.parse(from);
            } catch (ParseException e) {
                System.out.println(e);
                throw new ApiException("From Date format not matching it should be like yyyy-MM-dd");
            }
        }
        else{
            throw new ApiException("From Date cannot be empty");
        }
        if(s.getTo() != "") {
            try {
//                s.setTo(sdf.parse(s.getTo()).toString());
               sdf.parse(to);
            } catch (Exception e) {
                System.out.println(e);
                throw new ApiException("To Date format not matching it should be like yyyy-MM-dd" );
            }
        }
        else{
            throw new ApiException("To Date cannot be empty");
        }

        if((((sdf.parse(to).getTime() - sdf.parse(from).getTime())/(1000 * 60 * 60 * 24))% 365) >= 30){
            throw new ApiException("Date range should not be greater than 30 days");
        }

        if((sdf.parse(to).getTime() - sdf.parse(from).getTime())<=0){
            throw new ApiException("From date should be less than To date");
        }

        if(s.getBrand()!="" & s.getCategory()!="" & bDao.selectFromBrandCategory(s.getBrand(), s.getCategory())==null) {
            throw new ApiException(s.getBrand() + " - " +  s.getCategory() +" Brand-category pair does not exist");
        }

        if(s.getCategory()=="" & s.getBrand()!="" & bDao.selectBrand(s.getBrand())==null){
            throw new ApiException("Brand does not exist");
        }

        if(s.getBrand()=="" & s.getCategory()!="" & bDao.selectCategory(s.getCategory())==null){
            throw new ApiException("Brand does not exist");
        }
        List<SalesReport> salesReportData = dao.getSalesReport(s);
        if(salesReportData.size()==0){
            throw new ApiException("No sales for given range");
        }
        return salesReportData;
    }

    @Transactional(rollbackOn = ApiException.class)
    private static String jaxbObjectToXML(OrderItemDataList orderItemList)
    {
        try
        {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(OrderItemDataList.class);

            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            //Print XML String to Console
            StringWriter sw = new StringWriter();

            //Write XML to StringWriter
            jaxbMarshaller.marshal(orderItemList, sw);

            //Verify XML Content
            String xmlContent = sw.toString();
            return xmlContent;

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Transactional(rollbackOn = ApiException.class)
    public void getOrderInvoice(int orderId) throws ApiException, IOException, TransformerException {
       List<OrderItemData> o = ODto.getOrderItemForOrder(orderId);

       ZonedDateTime time = oService.get(orderId).getTime();
       Double total = 0.;

        for(OrderItemData i : o){
            total += i.getQuantity() * i.getSellingPrice();
        }
       OrderItemDataList oItem = new OrderItemDataList(o,time,total,orderId);


        String xml = jaxbObjectToXML(oItem);
        File xsltFile = new File("src", "invoice.xsl");
        File pdfFile = new File("src", "invoice.pdf");
        System.out.println(xml);
        convertToPDF(oItem,xsltFile,pdfFile,xml);

    }

    public void convertToPDF(OrderItemDataList team, File xslt, File pdf,String xml)
            throws IOException, TransformerException{

         	        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
         	        // configure foUserAgent as desired

         	        // Setup output
         	        OutputStream out = new java.io.FileOutputStream(pdf);
         	        out = new java.io.BufferedOutputStream(out);
         	        try {
             	            // Construct fop with desired output format
                        Fop fop = null;
                        try {
                            fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
                        } catch (FOPException e) {
                            throw new RuntimeException(e);
                        }

                        // Setup XSLT
             	            TransformerFactory factory = TransformerFactory.newInstance();
             	            Transformer transformer = factory.newTransformer(new StreamSource(xslt));

             	            // Setup input for XSLT transformation
                        Source src = new StreamSource(new StringReader(xml));
//                        Source src = new StreamSource(new FileInputStream(xml));
//
             	            // Resulting SAX events (the generated FO) must be piped through to FOP
             	            Result res = new SAXResult(fop.getDefaultHandler());

             	            // Start XSLT transformation and FOP processing
             	            transformer.transform(src, res);
             	        } catch (FOPException e) {
                        throw new RuntimeException(e);
                    } finally {
             	            out.close();
             	        }
         	    }

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryReport> getInventoryReport() throws ApiException {
        return iDao.getInventoryReport();
    }
}
