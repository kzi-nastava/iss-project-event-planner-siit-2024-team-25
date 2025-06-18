package com.team25.event.planner.offering.common.repository;

import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingSubmittedResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.User;
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

    @Query(""" 
            SELECT new com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO(
            o.id, 
            o.name, 
            CONCAT(o.owner.firstName, ' ', o.owner.lastName),
            o.description, 
            o.owner.companyAddress.country, 
            o.owner.companyAddress.city, 
            COALESCE(AVG(CASE WHEN r.reviewType = com.team25.event.planner.common.model.ReviewType.OFFERING_REVIEW THEN r.rating ELSE null END), 0),
            o.price, 
            (CASE WHEN (s.id is null) THEN false ELSE true END), 
            (CASE 
                WHEN EXISTS (
                    SELECT 1 FROM User u1 
                    JOIN u1.favoriteServices fs 
                    WHERE u1.id = :currentUserId AND fs.id = o.id
                )
                OR EXISTS (
                    SELECT 1 FROM User u2 
                    JOIN u2.favoriteProducts fp 
                    WHERE u2.id = :currentUserId AND fp.id = o.id
                )
                THEN true ELSE false END)
            ) 
            FROM Offering o 
            LEFT JOIN Service s ON s.id = o.id 
            LEFT JOIN Purchase p ON p.offering.id = o.id
            LEFT JOIN Review r ON r.purchase.id = p.id 
            WHERE o IN :offerings
            GROUP BY o.id, o.name, o.owner.firstName, o.owner.lastName, 
            o.description, o.owner.companyAddress.country, o.owner.companyAddress.city, o.price, s.id""")
    List<OfferingPreviewResponseDTO> findOfferingsWithAverageRating(@Param("offerings") List<Offering> offerings, Pageable pageable, Long currentUserId);


    @Query("""
SELECT new com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO(
    o.id,
    o.name,
    o.owner.firstName || ' ' || o.owner.lastName,
    o.description,
    o.owner.companyAddress.country,
    o.owner.companyAddress.city,
    COALESCE(AVG(r.rating), 0),
    o.price,
    (CASE WHEN (s.id IS NULL) THEN false ELSE true END),
    (CASE 
        WHEN EXISTS (
            SELECT 1 FROM User u1 
            JOIN u1.favoriteServices fs 
            WHERE u1.id = :currentUserId AND fs.id = o.id
        )
        OR EXISTS (
            SELECT 1 FROM User u2 
            JOIN u2.favoriteProducts fp 
            WHERE u2.id = :currentUserId AND fp.id = o.id
        )
        THEN true ELSE false END)
)
FROM Offering o
LEFT JOIN Service s ON s.id = o.id
LEFT JOIN Purchase p ON p.offering.id = o.id
LEFT JOIN Review r ON r.purchase.id = p.id 
    AND r.reviewType = com.team25.event.planner.common.model.ReviewType.OFFERING_REVIEW
WHERE
    (:country IS NULL OR :country = '' OR o.owner.companyAddress.country = :country) AND
    (:city IS NULL OR :city = '' OR o.owner.companyAddress.city = :city) AND
    (:currentUserId IS NULL OR
        NOT EXISTS (
            SELECT 1 FROM User u1
            JOIN u1.blockedUsers bu1
            WHERE u1.id = :currentUserId AND bu1.id = o.owner.id
        )
    ) AND
    (:currentUserId IS NULL OR
        NOT EXISTS (
            SELECT 1 FROM User u2
            WHERE u2.id = :currentUserId AND u2 MEMBER OF o.owner.blockedUsers
        )
    )

GROUP BY o.id, o.name, o.owner.firstName, o.owner.lastName, o.description, 
         o.owner.companyAddress.country, o.owner.companyAddress.city, o.price, s.id
ORDER BY COALESCE(AVG(r.rating), 0) DESC
""")
    Page<OfferingPreviewResponseDTO> findTopOfferings(String country, String city, Long currentUserId, Pageable pageable);



    @Query("SELECT o FROM Owner o " +
            "LEFT JOIN Offering off ON off.owner.id = o.id " +
            "WHERE (off IS NOT NULL AND off.offeringCategory.id = :id)")
    List<Owner> findOwnersByOfferingCategoryId(@Param("id") Long id);
}
