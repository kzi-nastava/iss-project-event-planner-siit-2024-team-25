package com.team25.event.planner.offering.common.repository;

import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingSubmittedResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfferingRepository extends JpaRepository<Offering, Long>, JpaSpecificationExecutor<Offering> {

    Long countOfferingsByOfferingCategoryId(Long offeringCategoryId);

    @Query("SELECT new com.team25.event.planner.offering.common.dto.OfferingSubmittedResponseDTO("+
            "o.id,"+
            " o.name,"+
            " ot.id,"+
            " ot.name,"+
            " ot.description,"+
            " ot.status) " +
            "FROM Offering o " +
            "LEFT JOIN OfferingCategory ot ON o.offeringCategory.id = ot.id " +
            "WHERE ot.status = 'PENDING'")
    List<OfferingSubmittedResponseDTO> getOfferingSubmittedResponseDTOs();

    @Query("SELECT new com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO(" +
            "o.id, " +
            "o.name, " +
            "CONCAT(o.owner.firstName, ' ', o.owner.lastName), " +
            "o.description, " +
            "o.owner.companyAddress.country, " +
            "o.owner.companyAddress.city, " +
            "COALESCE(AVG(r.rating), 0), " +
            "o.price, " +
            "(CASE WHEN COUNT(s.id) = 0 THEN false ELSE true END)) " +
            "FROM Offering o " +
            "LEFT JOIN Service s ON s.id = o.id " +
            "LEFT JOIN Purchase p ON p.offering.id = o.id " +
            "LEFT JOIN OfferingReview r ON r.purchase.id = p.id " +
            "WHERE o IN :offerings " +
            "GROUP BY o.id, o.name, o.owner.firstName, o.owner.lastName, " +
            "o.description, o.owner.companyAddress.country, o.owner.companyAddress.city, o.price")
    List<OfferingPreviewResponseDTO> findOfferingsWithAverageRating(@Param("offerings") List<Offering> offerings,
            Pageable pageable);


    @Query("SELECT new com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO(" +
            "o.id, " +
            "o.name, " +
            "o.owner.firstName || ' ' || o.owner.lastName, " +
            "o.description, " +
            "o.owner.companyAddress.country, " +
            "o.owner.companyAddress.city, " +
            "COALESCE(AVG(r.rating), 0), " +
            "o.price," +
            "(CASE WHEN COUNT(s.id) = 0 THEN false ELSE true END))  " +
            "FROM Offering o " +
            "LEFT JOIN Service s ON s.id = o.id " +
            "LEFT JOIN Purchase p ON p.offering.id = o.id " +
            "LEFT JOIN OfferingReview r ON r.purchase.id = p.id " +
            "WHERE " +
            "(:country IS NULL OR :country = '' OR o.owner.companyAddress.country = :country) AND " +
            "(:city IS NULL OR :city = '' OR o.owner.companyAddress.city = :city) " +
            "GROUP BY o.id, o.name, o.owner.firstName, o.owner.lastName, o.description, o.owner.companyAddress.country, o.owner.companyAddress.city, o.price " +
            "ORDER BY COALESCE(AVG(r.rating), 0) DESC")
    Page<OfferingPreviewResponseDTO> findTopOfferings(String country, String city, Pageable pageable);
}
