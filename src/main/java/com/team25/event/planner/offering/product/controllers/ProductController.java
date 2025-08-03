package com.team25.event.planner.offering.product.controllers;

import com.team25.event.planner.common.exception.ServerError;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.product.dto.ProductPreviewResponseDTO;
import com.team25.event.planner.offering.product.dto.ProductRequestDTO;
import com.team25.event.planner.offering.product.dto.ProductResponseDTO;
import com.team25.event.planner.offering.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("api/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }


    @GetMapping("/all")
    public ResponseEntity<Page<OfferingPreviewResponseDTO>> getAllProducts(
            @ModelAttribute OfferingFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(productService.getAllProducts(filter, page, size, sortBy, sortDirection));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<ProductPreviewResponseDTO>> getOwnerProducts(
            @PathVariable Long ownerId,
            @ModelAttribute OfferingFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(productService.getOwnerProducts(ownerId, filter, page, size, sortBy, sortDirection));
    }



    @PostMapping
    @Secured("ROLE_OWNER")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @ModelAttribute ProductRequestDTO productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_OWNER') and @offeringPermissionEvaluator.canEdit(authentication, #id)")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductRequestDTO productDto
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_OWNER') and @offeringPermissionEvaluator.canEdit(authentication, #id)")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Resource> getProductImage(@PathVariable Long productId, @PathVariable String imageId) {
        try {
            Resource resource = productService.getProductImage(productId, imageId);

            // Determine the content type (e.g., image/jpeg)
            String contentType = Files.probeContentType(Path.of(resource.getFile().getAbsolutePath()));
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback to binary stream
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new ServerError("Could not load image", 500);
        }
    }
}
