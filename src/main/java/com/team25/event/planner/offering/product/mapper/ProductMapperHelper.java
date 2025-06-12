package com.team25.event.planner.offering.product.mapper;

import com.team25.event.planner.offering.product.dto.ProductResponseDTO;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapperHelper {
    private final CurrentUserService currentUserService;

    @AfterMapping
    public void addFavoriteFlag(@MappingTarget ProductResponseDTO productResponseDTO, Product product) {
        User currentUser = currentUserService.getCurrentUser();
        //productResponseDTO.setFavorite(currentUser != null && currentUser.getFavoriteProducts().contains(product));

    }
}
