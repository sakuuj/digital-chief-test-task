package by.sakuuj.digital.chief.testproj.controller;

import by.sakuuj.digital.chief.testproj.dto.ProductRequest;
import by.sakuuj.digital.chief.testproj.dto.ProductResponse;
import by.sakuuj.digital.chief.testproj.paging.PagedResponse;
import by.sakuuj.digital.chief.testproj.service.ProductElasticsearchService;
import by.sakuuj.digital.chief.testproj.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductsController {

    private static final String PRODUCT_TITLE_PARAM = "product_title";
    private static final String PRODUCT_DESCRIPTION_PARAM = "product_description";

    private static final String CHILD_SKU_DEPARTMENT_PARAM = "child_sku_department";
    private static final String PRODUCT_BRAND_PARAM = "product_brand";

    private final ProductService productService;
    private final ProductElasticsearchService productElasticsearchService;


    @Operation(
            parameters = {
                    @Parameter(name = PRODUCT_TITLE_PARAM,
                            description = "If used, then you must also use '" + PRODUCT_DESCRIPTION_PARAM + "', otherwise PostgreSQL will be used"),
                    @Parameter(name = PRODUCT_DESCRIPTION_PARAM,
                            description = "If used, then you must also use '" + PRODUCT_TITLE_PARAM + "', otherwise PostgreSQL will be used"),

                    @Parameter(name = CHILD_SKU_DEPARTMENT_PARAM, example = "Electronics",
                            description = "If used, then you must also use '" + PRODUCT_BRAND_PARAM + "', otherwise PostgreSQL will be used"),
                    @Parameter(name = PRODUCT_BRAND_PARAM, example = "LogiTech",
                            description = "If used, then you must also use '" + CHILD_SKU_DEPARTMENT_PARAM + "', otherwise PostgreSQL will be used"),
                    @Parameter(name = "page", example = "0"),
                    @Parameter(name = "size", example = "3"),

            },
            description = "To use Elasticsearch, you should specify either both "
                    + PRODUCT_TITLE_PARAM + " and " + PRODUCT_DESCRIPTION_PARAM + " or "
                    + CHILD_SKU_DEPARTMENT_PARAM + " and " + PRODUCT_BRAND_PARAM
                    + ". Otherwise PostgreSQL will be used"
    )
    @ApiResponse(responseCode = "200", content = @Content)
    @GetMapping
    public PagedResponse<ProductResponse> findAll(@Parameter(hidden = true) Pageable pageable) {

        return productService.findAllSortedByCreatedAtDesc(pageable);
    }

    @ApiResponse(responseCode = "200", content = @Content)
    @GetMapping(params = {PRODUCT_TITLE_PARAM, PRODUCT_DESCRIPTION_PARAM})
    public PagedResponse<ProductResponse> findProductsByProductTitleOrProductDescription(
            @Parameter(hidden = true) @RequestParam(name = PRODUCT_TITLE_PARAM) String productTitle,
            @Parameter(hidden = true) @RequestParam(name = PRODUCT_DESCRIPTION_PARAM) String productDescription,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return productElasticsearchService.findAllByProductTitleOrProductDescription(
                productTitle,
                productDescription,
                pageable.getPageNumber() * pageable.getPageSize(),
                pageable.getPageSize()
        );
    }

    @ApiResponse(responseCode = "200", content = @Content)
    @GetMapping(params = {CHILD_SKU_DEPARTMENT_PARAM, PRODUCT_BRAND_PARAM})
    public PagedResponse<ProductResponse> findByChildHavingSkuDepartmentAndProductBrand(
            @Parameter(hidden = true) @RequestParam(name = CHILD_SKU_DEPARTMENT_PARAM) String childSkuDepartment,
            @Parameter(hidden = true) @RequestParam(name = PRODUCT_BRAND_PARAM) String productBrand,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return productElasticsearchService.findAllByHavingChildWithSkuDepartmentAndProductBrand(
                productBrand,
                childSkuDepartment,
                pageable.getPageNumber() * pageable.getPageSize(),
                pageable.getPageSize()
        );
    }

    @ApiResponse(responseCode = "200", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable("id") UUID id) {

        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid ProductRequest request) {

        UUID id = productService.save(request);
        return ResponseEntity.created(URI.create("/products/" + id)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {

        productService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateByIdAndVersion(
            @PathVariable("id") UUID id,
            @RequestParam("version") short version,
            @RequestBody @Valid ProductRequest request
    ) {

        productService.update(id, version, request);

        return ResponseEntity.noContent().build();
    }
}
