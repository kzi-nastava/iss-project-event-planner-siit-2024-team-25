package com.team25.event.planner.common.dto;

import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.common.model.ReviewType;
import com.team25.event.planner.user.dto.UserResponseDTO;
import lombok.Data;

import java.util.Date;

@Data
public class ReviewResponseDTO {
    private Long id;
    private String comment;
    private int rating;
    private ReviewType reviewType;
    private ReviewStatus reviewStatus;
    private Date createdDate;
    private Long purchaseId;
    private UserResponseDTO user;
    private String eventOfferingName;
    public ReviewResponseDTO(Review review, String offeringName) {
        this.comment = review.getComment();
        this.rating = review.getRating();
        this.reviewType = review.getReviewType();
        this.reviewStatus = review.getReviewStatus();
        this.createdDate = Date.from(review.getCreatedDate());
        this.purchaseId = review.getPurchase().getId();
        this.user = new UserResponseDTO(review.getUser().getId(),review.getUser().getFirstName(),review.getUser().getLastName(),review.getUser().getProfilePictureUrl(),review.getUser().getUserRole());
        this.eventOfferingName = offeringName;
    }
    public ReviewResponseDTO() {}
}
