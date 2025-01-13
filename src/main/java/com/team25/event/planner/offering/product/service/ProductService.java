package com.team25.event.planner.offering.product.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.exception.ServerError;
import com.team25.event.planner.common.exception.UnauthorizedError;
import com.team25.event.planner.common.util.FileUtils;
import com.team25.event.planner.communication.service.NotificationService;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.event.repository.EventTypeRepository;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.offering.product.dto.ProductRequestDTO;
import com.team25.event.planner.offering.product.dto.ProductResponseDTO;
import com.team25.event.planner.offering.product.mapper.ProductMapper;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.product.repository.ProductRepository;
import com.team25.event.planner.offering.product.specification.ProductSpecification;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.OwnerRepository;
import com.team25.event.planner.user.service.CurrentUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final OfferingRepository offeringRepository;
    private final ProductSpecification productSpecification;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OwnerRepository ownerRepository;
    private final OfferingCategoryRepository offeringCategoryRepository;
    private final EventTypeRepository eventTypeRepository;
    private final NotificationService notificationService;

    private final Path productImagesFileStorageLocation;
    private final CurrentUserService currentUserService;

    public ProductService(
            OfferingRepository offeringRepository,
            ProductSpecification productSpecification,
            ProductRepository productRepository,
            ProductMapper productMapper,
            OwnerRepository ownerRepository,
            OfferingCategoryRepository offeringCategoryRepository,
            EventTypeRepository eventTypeRepository,
            NotificationService notificationService,
            @Value("${file-storage.images.product}") String productImagesSaveDirectory,
            CurrentUserService currentUserService) {
        this.offeringRepository = offeringRepository;
        this.productSpecification = productSpecification;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productImagesFileStorageLocation = Paths.get(productImagesSaveDirectory).toAbsolutePath().normalize();
        this.ownerRepository = ownerRepository;
        this.offeringCategoryRepository = offeringCategoryRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    public ProductResponseDTO getProduct(Long productId) {
        final Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundError("Product not found"));
        User currentUser = currentUserService.getCurrentUser();
        if(product.getOwner().getBlockedUsers().contains(currentUser) || product.getOwner().getBlockedByUsers().contains(currentUser)) {
            throw new UnauthorizedError("You can't see this product page.");
        }
        return productMapper.toDTO(product);
    }

    public Page<OfferingPreviewResponseDTO> getAllProducts(OfferingFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        Specification<Product> spec = productSpecification.createSpecification(filter);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Offering> offeringPage = productRepository.findAll(spec, pageable).map(product -> (Offering) product);
        pageable = PageRequest.of(0, size, Sort.by(direction, sortBy));
        List<OfferingPreviewResponseDTO> offeringsWithRatings = offeringRepository.findOfferingsWithAverageRating(offeringPage.getContent(), pageable);
        return new PageImpl<>(offeringsWithRatings, pageable, offeringPage.getTotalElements());
    }

    public Page<OfferingPreviewResponseDTO> getOwnerProducts(Long ownerId, OfferingFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        Specification<Product> spec = productSpecification.createSpecification(filter);
        spec.and((root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId));

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Offering> offeringPage = productRepository.findAll(spec, pageable).map(product -> (Offering) product);
        pageable = PageRequest.of(0, size, Sort.by(direction, sortBy));

        List<OfferingPreviewResponseDTO> offeringsWithRatings = offeringRepository.findOfferingsWithAverageRating(offeringPage.getContent(), pageable);
        return new PageImpl<>(offeringsWithRatings, pageable, offeringPage.getTotalElements());
    }

    @Transactional
    public ProductResponseDTO createProduct(@Valid ProductRequestDTO productDto) {
        final Owner owner = ownerRepository.findById(productDto.getOwnerId())
                .orElseThrow(() -> new UnauthorizedError("You must be owner to create a product"));

        OfferingCategory offeringCategory;
        OfferingType status;

        if (productDto.getOfferingCategoryId() != null) {
            offeringCategory = offeringCategoryRepository.findById(productDto.getOfferingCategoryId())
                    .orElseThrow(() -> new NotFoundError("Offering Category not found"));
            status = OfferingType.ACCEPTED;
        } else if (productDto.getOfferingCategoryName() != null && !productDto.getOfferingCategoryName().isBlank()) {
            offeringCategory = new OfferingCategory(productDto.getOfferingCategoryName(), "", OfferingCategoryType.PENDING);
            offeringCategoryRepository.save(offeringCategory);
            notificationService.sendOfferingCategoryNotificationToAdmin(offeringCategory);
            status = OfferingType.PENDING;
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("categoryId", "Category is required");
            throw new InvalidRequestError("Validation failed", errors);
        }

        final List<EventType> eventTypes = eventTypeRepository.findAllById(productDto.getEventTypeIds());
        if (eventTypes.isEmpty()) {
            throw new InvalidRequestError("No valid event types were provided");
        }

        Product product = productMapper.toProduct(productDto, status, eventTypes, offeringCategory, owner);

        final List<String> imageFilenames = saveProductImages(productDto.getImages());
        product.setImages(imageFilenames);

        try {
            product = productRepository.save(product);
        } catch (Exception e) {
            // If the database update fails, then delete saved images.
            logger.warn("Starting product image cleanup due to failed database write.", e);
            FileUtils.deleteFiles(productImagesFileStorageLocation, imageFilenames);
            throw e;
        }

        return productMapper.toDTO(product);
    }

    public ProductResponseDTO updateProduct(Long productId, @Valid ProductRequestDTO productDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundError("Product not found"));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setDiscount(productDto.getDiscount());
        product.setVisible(productDto.isVisible());
        product.setAvailable(productDto.isAvailable());

        if (productDto.getImagesToDelete() != null) {
            product.getImages().removeAll(productDto.getImagesToDelete());
            FileUtils.deleteFiles(productImagesFileStorageLocation, productDto.getImagesToDelete());
        }

        if (productDto.getImages() != null) {
            final List<String> filenames = saveProductImages(productDto.getImages());
            product.getImages().addAll(filenames);
        }

        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundError("Product not found"));
        product.setDeleted(true);
        productRepository.save(product);
    }

    // TODO: maybe extract image save and retrieve methods into common OfferingService
    private List<String> saveProductImages(List<MultipartFile> images) {
        if (images == null) {
            return new ArrayList<>();
        }

        try {
            Files.createDirectories(productImagesFileStorageLocation);
        } catch (IOException e) {
            logger.error("Product images directory creation failed: {}", productImagesFileStorageLocation);
            throw new ServerError("Failed to store image", 500);
        }

        List<String> filenames = new ArrayList<>();
        RuntimeException failException = null;
        for (MultipartFile image : images) {
            if (!FileUtils.isImage(image)) {
                failException = new InvalidRequestError("One or more product images are not valid");
                break;
            }

            final String extension = FileUtils.getExtensionOrDefault(image, "png");
            final String filename = UUID.randomUUID() + "." + extension;
            Path filepath = productImagesFileStorageLocation.resolve(filename);

            try {
                image.transferTo(filepath.toFile());
            } catch (IOException e) {
                logger.error("Failed to store a product image");
                failException = new ServerError("Failed to store image", 500);
            }

            filenames.add(filename);
        }

        if (failException != null) {
            // clean up all successfully saved images if one failed
            FileUtils.deleteFiles(productImagesFileStorageLocation, filenames);
            throw failException;
        }

        return filenames;
    }

    public Resource getProductImage(Long productId, String imageFilename) {
        final Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundError("Product not found"));

        // Maybe remove the check for performance reasons
        if (!product.getImages().contains(imageFilename)) {
            throw new NotFoundError("Image not found");
        }

        Path filePath = productImagesFileStorageLocation.resolve(imageFilename);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new NotFoundError("Image not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new NotFoundError("Image not found");
        }
    }
}
