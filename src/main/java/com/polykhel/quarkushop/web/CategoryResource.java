package com.polykhel.quarkushop.web;

import com.polykhel.quarkushop.dto.CategoryDto;
import com.polykhel.quarkushop.dto.ProductDto;
import com.polykhel.quarkushop.service.CategoryService;
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

@Path("/categories")
@Produces("application/json")
@Tags(value = @Tag(name = "category", description = "All the category methods"))
public class CategoryResource {

    @Inject
    CategoryService categoryService;

    @GET
    public List<CategoryDto> findAll() {
        return this.categoryService.findAll();
    }

    @GET
    @Path("/{id}")
    public CategoryDto findById(@PathParam("id") Long id) {
        return this.categoryService.findById(id);
    }

    @GET
    @Path("/{id}/products")
    public List<ProductDto> findProductsByCategoryId(@PathParam("id") Long id) {
        return this.categoryService.findProductsByCategoryId(id);
    }

    @RolesAllowed("admin")
    @POST
    @Consumes("application/json")
    public CategoryDto create(CategoryDto categoryDto) {
        return this.categoryService.create(categoryDto);
    }

    @RolesAllowed("admin")
    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        this.categoryService.delete(id);
    }
}
