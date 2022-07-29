package pos.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UIController {

    @RequestMapping(value = "/ui/")
    public String index(){
        return "index.html";
    }

    @RequestMapping(value = "/ui/brands")
    public String brand(){
        return "brand.html";
    }

    @RequestMapping(value = "/ui/products")
    public String product(){
        return "product.html";
    }

    @RequestMapping(value = "/ui/inventory")
    public String inventory(){
        return "inventory.html";
    }

    @RequestMapping(value = "/ui/orders")
    public String order(){
        return "order.html";
    }

    @RequestMapping(value = "/ui/reports")
    public String reports(){
        return "reports.html";
    }

}
