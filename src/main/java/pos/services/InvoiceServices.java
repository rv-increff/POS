package pos.services;

import org.apache.fop.apps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.dao.InventoryDao;
import pos.dao.OrderItemDao;
import pos.dto.OrderItemDto;
import pos.model.InventoryReport;
import pos.model.OrderItemData;
import pos.model.OrderItemDataList;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
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

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryReport> getInventoryReport(){
        return iDao.getInventoryReport();
    }

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

    private void convertToPDF(OrderItemDataList team, File xslt, File pdf,String xml)
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


}
