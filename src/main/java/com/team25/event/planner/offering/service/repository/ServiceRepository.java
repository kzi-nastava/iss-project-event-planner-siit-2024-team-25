package com.team25.event.planner.offering.service.repository;

import com.team25.event.planner.offering.common.dto.PriceListItemResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.user.model.Owner;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {

    List<Service> findAllByOwner(@NotNull(message = "Owner is required") Owner owner);
}
