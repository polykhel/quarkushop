package com.polykhel.quarkushop.web;

import com.polykhel.quarkushop.dto.ProductDto;
import com.polykhel.quarkushop.service.ProductService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/products")
@Produces("application/json")
@Tags(value = @Tag(name = "product", description = "All the product methods"))
public class ProductResource {
    @Inject
    ProductService productService;

    @GET
    public List<ProductDto> findAll() {
        return this.productService.findAll();
    }

    @GET
    @Path("/count")
    public Long countAllProducts() {
        return this.productService.countAll();
    }

    @GET
    @Path("/{id}")
    public ProductDto findById(@PathParam("id") Long id) {
        return this.productService.findById(id);
    }

    @RolesAllowed("admin")
    @POST
    @Consumes("application/json")
    public ProductDto create(ProductDto productDto) {
        return this.productService.create(productDto);
    }

    @RolesAllowed("admin")
    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        this.productService.delete(id);
    }

    @GET
    @Path("/category/{id}")
    public List<ProductDto> findByCategoryId(@PathParam("id") Long id) {
        return this.productService.findByCategoryId(id);
    }

    @GET
    @Path("/count/category/{id}")
    public Long countByCategoryId(@PathParam("id") Long id) {
        return this.productService.countByCategoryId(id);
    }
}
