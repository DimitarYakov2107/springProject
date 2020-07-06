package com.project.springProject.onlineshop.model.form.validator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.project.springProject.onlineshop.model.Product;
import com.project.springProject.onlineshop.model.form.ProductForm;
import com.project.springProject.onlineshop.service.ProductService;
 
@Component
public class ProductFormValidator implements Validator {
 
   @Autowired
   private ProductService productService;
 
   // This validator only checks for the ProductForm.
   @Override
   public boolean supports(Class<?> clazz) {
      return clazz == ProductForm.class;
   }
 
   @Override
   public void validate(Object target, Errors errors) {
      ProductForm productForm = (ProductForm) target;
 
      // Check the fields of ProductForm.
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "NotEmpty.productForm.code");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.productForm.name");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "NotEmpty.productForm.price");
 
      String code = productForm.getCode();
      if (code != null && code.length() > 0) {
         if (code.matches("\\s+")) {
            errors.rejectValue("code", "Pattern.productForm.code");
         } else if (productForm.isNewProduct()) {
            Optional<Product> product = productService.findByCode(code);
            if (product.isPresent()) {
               errors.rejectValue("code", "Duplicate.productForm.code");
            }
         }
      }
   }
}
