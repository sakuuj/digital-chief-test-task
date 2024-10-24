package by.sakuuj.digital.chief.testproj.controller;

import by.sakuuj.digital.chief.testproj.dto.SkuRequest;
import by.sakuuj.digital.chief.testproj.dto.SkuResponse;
import by.sakuuj.digital.chief.testproj.paging.PagedResponse;
import by.sakuuj.digital.chief.testproj.service.SkuService;
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
@RequestMapping(path = "/skus", produces = MediaType.APPLICATION_JSON_VALUE)
public class SkusController {

    private final SkuService skuService;

    @Operation(parameters = {
            @Parameter(name = "page", example = "0"),
            @Parameter(name = "size", example = "3"),
    })
    @ApiResponse(responseCode = "200", content = @Content)
    @GetMapping
    public PagedResponse<SkuResponse> findAll(@Parameter(hidden = true) Pageable pageable) {

        return skuService.findAllSortedByCreatedAtDesc(pageable);
    }

    @ApiResponse(responseCode = "200", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<SkuResponse> findById(@PathVariable("id") UUID id) {

        return skuService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SkuResponse> create(
            @RequestBody @Valid SkuRequest request,
            @RequestParam("product-id") UUID productId
    ) {

        UUID id = skuService.save(request, productId);
        return ResponseEntity.created(URI.create("/skus/" + id)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {

        skuService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") UUID id,
            @RequestParam("version") short version,
            @RequestBody @Valid SkuRequest request
    ) {
        skuService.update(id, version, request);
        return ResponseEntity.noContent().build();
    }
}
