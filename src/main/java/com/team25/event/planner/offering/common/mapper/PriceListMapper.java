package com.team25.event.planner.offering.common.mapper;

import com.team25.event.planner.offering.common.dto.PriceListItemResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceListMapper {

    @Mapping(target = "offeringId", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "discount", source = "discount")
    @Mapping(target = "priceWithDiscount", expression = "java(calculatePriceWithDiscount(offering.getPrice(), offering.getDiscount()))")
    PriceListItemResponseDTO toPriceListItem(Offering offering);

    default double calculatePriceWithDiscount(double price, double discount) {
        return price * (1 - discount / 100.0);
    }
}
