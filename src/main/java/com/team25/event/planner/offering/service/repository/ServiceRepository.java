package com.team25.event.planner.offering.service.repository;

import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.dto.PriceListItemResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.user.model.Owner;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {

    List<Service> findAllByOwner(@NotNull(message = "Owner is required") Owner owner);

    @Query("""
    SELECT new com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO(
        s.id,
        s.name,
        CONCAT(s.owner.firstName, ' ', s.owner.lastName),
        s.description,
        s.owner.companyAddress.country,
        s.owner.companyAddress.city,
        COALESCE(AVG(CASE WHEN r.reviewType = com.team25.event.planner.common.model.ReviewType.OFFERING_REVIEW THEN r.rating ELSE null END), 0),
        s.price,
        true
    )
    FROM Service s
    LEFT JOIN Review r ON s.id = r.id
    WHERE s IN :services
    GROUP BY s.id, s.name, s.owner.firstName,s.owner.lastName, s.description, s.owner.companyAddress.country, s.owner.companyAddress.city, s.price
""")
    List<OfferingPreviewResponseDTO> findPreviewsForServices(@Param("services") List<Service> services);
}
