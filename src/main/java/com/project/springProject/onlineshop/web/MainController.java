package com.project.springProject.onlineshop.web;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.springProject.onlineshop.model.Product;
import com.project.springProject.onlineshop.model.dto.CartInfo;
import com.project.springProject.onlineshop.model.dto.CustomerInfo;
import com.project.springProject.onlineshop.model.dto.ProductInfo;
import com.project.springProject.onlineshop.model.form.CustomerForm;
import com.project.springProject.onlineshop.model.form.validator.CustomerFormValidator;
import com.project.springProject.onlineshop.pagination.PaginationResult;
import com.project.springProject.onlineshop.service.OrderService;
import com.project.springProject.onlineshop.service.ProductService;
import com.project.springProject.onlineshop.utils.Utils;

@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "index";
    }
    
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }
    
    @Autowired
    private OrderService orderService;
  
    @Autowired
    private ProductService productService;
  
    @Autowired
    private CustomerFormValidator customerFormValidator;
    
    @InitBinder
    public void myInitBinder(WebDataBinder dataBinder) {
       Object target = dataBinder.getTarget();
       if (target == null) {
          return;
       }
       System.out.println("Target=" + target);
  
       // Case update quantity in cart
       // (@ModelAttribute("cartForm") @Validated CartInfo cartForm)
       if (target.getClass() == CartInfo.class) {
  
       }
  
       // Case save customer information.
       // (@ModelAttribute @Validated CustomerInfo customerForm)
       else if (target.getClass() == CustomerForm.class) {
          dataBinder.setValidator(customerFormValidator);
       }
  
    }
  
    @RequestMapping("/403")
    public String accessDenied() {
       return "/403";
    }
  
    @RequestMapping("/")
    public String home() {
       return "index";
    }
  
    // Product List
    @RequestMapping({ "/productList" })
    public String listProductHandler(Model model, //
          @RequestParam(value = "name", defaultValue = "") String likeName,
          @RequestParam(value = "page", defaultValue = "1") int page) {
       final int maxResult = 5;
       final int maxNavigationPage = 10;
  
       PaginationResult<ProductInfo> result = productService.queryProducts(page, //
             maxResult, maxNavigationPage, likeName);
  
       model.addAttribute("paginationProducts", result);
       return "productList";
    }
  
    @RequestMapping({ "/buyProduct" })
    public String listProductHandler(HttpServletRequest request, Model model, //
          @RequestParam(value = "code", defaultValue = "") String code) {
  
       Optional<Product> product = null;
       if (code != null && code.length() > 0) {
          product = productService.findByCode(code);
       }
       if (product.isPresent()) {
  
          //
          CartInfo cartInfo = Utils.getCartInSession(request);
  
          ProductInfo productInfo = new ProductInfo(product.get());
  
          cartInfo.addProduct(productInfo, 1);
       }
  
       return "redirect:/shoppingCart";
    }
  
    @RequestMapping({ "/shoppingCartRemoveProduct" })
    public String removeProductHandler(HttpServletRequest request, Model model, //
          @RequestParam(value = "code", defaultValue = "") String code) {
       Optional<Product> product = null;
       if (code != null && code.length() > 0) {
          product = productService.findByCode(code);
       }
       if (product.isPresent()) {
  
          CartInfo cartInfo = Utils.getCartInSession(request);
  
          ProductInfo productInfo = new ProductInfo(product.get());
  
          cartInfo.removeProduct(productInfo);
       }
  
       return "redirect:/shoppingCart";
    }
  
    // POST: Update quantity for product in cart
    @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.POST)
    public String shoppingCartUpdateQty(HttpServletRequest request, //
          Model model, //
          @ModelAttribute("cartForm") CartInfo cartForm) {
  
       CartInfo cartInfo = Utils.getCartInSession(request);
       cartInfo.updateQuantity(cartForm);
  
       return "redirect:/shoppingCart";
    }
  
    // GET: Show cart.
    @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.GET)
    public String shoppingCartHandler(HttpServletRequest request, Model model) {
       CartInfo myCart = Utils.getCartInSession(request);
  
       model.addAttribute("cartForm", myCart);
       return "shoppingCart";
    }
  
    // GET: Enter customer information.
    @RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.GET)
    public String shoppingCartCustomerForm(HttpServletRequest request, Model model) {
  
       CartInfo cartInfo = Utils.getCartInSession(request);
  
       if (cartInfo.isEmpty()) {
  
          return "redirect:/shoppingCart";
       }
       CustomerInfo customerInfo = cartInfo.getCustomerInfo();
  
       CustomerForm customerForm = new CustomerForm(customerInfo);
  
       model.addAttribute("customerForm", customerForm);
  
       return "shoppingCartCustomer";
    }
  
    // POST: Save customer information.
    @RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.POST)
    public String shoppingCartCustomerSave(HttpServletRequest request, //
          Model model, //
          @ModelAttribute("customerForm") @Validated CustomerForm customerForm, //
          BindingResult result, //
          final RedirectAttributes redirectAttributes) {
  
       if (result.hasErrors()) {
          customerForm.setValid(false);
          // Forward to reenter customer info.
          return "shoppingCartCustomer";
       }
  
       customerForm.setValid(true);
       CartInfo cartInfo = Utils.getCartInSession(request);
       CustomerInfo customerInfo = new CustomerInfo(customerForm);
       cartInfo.setCustomerInfo(customerInfo);
  
       return "redirect:/shoppingCartConfirmation";
    }
  
    // GET: Show information to confirm.
    @RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.GET)
    public String shoppingCartConfirmationReview(HttpServletRequest request, Model model) {
       CartInfo cartInfo = Utils.getCartInSession(request);
  
       if (cartInfo == null || cartInfo.isEmpty()) {
  
          return "redirect:/shoppingCart";
       } else if (!cartInfo.isValidCustomer()) {
  
          return "redirect:/shoppingCartCustomer";
       }
       model.addAttribute("myCart", cartInfo);
  
       return "shoppingCartConfirmation";
    }
  
    // POST: Submit Cart (Save)
    @RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.POST)
  
    public String shoppingCartConfirmationSave(HttpServletRequest request, Model model) {
       CartInfo cartInfo = Utils.getCartInSession(request);
  
       if (cartInfo.isEmpty()) {
  
          return "redirect:/shoppingCart";
       } else if (!cartInfo.isValidCustomer()) {
  
          return "redirect:/shoppingCartCustomer";
       }
       try {
          orderService.saveOrder(cartInfo);
       } catch (Exception e) {
  
          return "shoppingCartConfirmation";
       }
  
       // Remove Cart from Session.
       Utils.removeCartInSession(request);
  
       // Store last cart.
       Utils.storeLastOrderedCartInSession(request, cartInfo);
  
       return "redirect:/shoppingCartFinalize";
    }
  
    @RequestMapping(value = { "/shoppingCartFinalize" }, method = RequestMethod.GET)
    public String shoppingCartFinalize(HttpServletRequest request, Model model) {
  
       CartInfo lastOrderedCart = Utils.getLastOrderedCartInSession(request);
  
       if (lastOrderedCart == null) {
          return "redirect:/shoppingCart";
       }
       model.addAttribute("lastOrderedCart", lastOrderedCart);
       return "shoppingCartFinalize";
    }
  
    @RequestMapping(value = { "/productImage" }, method = RequestMethod.GET)
    public void productImage(HttpServletRequest request, HttpServletResponse response, Model model,
          @RequestParam("code") String code) throws IOException {
       Optional<Product> product = null;
       if (code != null) {
          product = this.productService.findByCode(code);
       }
       if (product.isPresent() && product.get().getImage() != null) {
          response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
          response.getOutputStream().write(product.get().getImage());
       }
       response.getOutputStream().close();
    }
  
}