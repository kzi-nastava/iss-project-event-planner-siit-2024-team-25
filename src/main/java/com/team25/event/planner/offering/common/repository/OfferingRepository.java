package com.team25.event.planner.offering.common.repository;

import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfferingRepository extends JpaRepository<Offering, Long>, JpaSpecificationExecutor<Offering> {

    @Query("SELECT new com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO(" +
            "o.id, " +
            "o.name, " +
            "o.owner.firstName || ' ' || o.owner.lastName, " +
            "o.description, " +
            "o.owner.companyAddress.country, " +
            "o.owner.companyAddress.city, " +
            "COALESCE(AVG(r.rating), 0), " +
            "o.price) " +
            "FROM Offering o " +
            "LEFT JOIN Purchase p ON p.offering.id = o.id " +
            "LEFT JOIN OfferingReview r ON r.purchase.id = p.id " +
            "WHERE o IN :offerings " +
            "GROUP BY o.id, o.name, o.owner.firstName, o.owner.lastName, o.description, o.owner.companyAddress.country, o.owner.companyAddress.city, o.price")
    List<OfferingPreviewResponseDTO> findOfferingsWithAverageRating(@Param("offerings") List<Offering> offerings);
}
