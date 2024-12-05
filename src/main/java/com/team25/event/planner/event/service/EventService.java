package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.util.VerificationCodeGenerator;
import com.team25.event.planner.email.service.EmailService;
import com.team25.event.planner.event.dto.*;
import com.team25.event.planner.event.mapper.ActivityMapper;
import com.team25.event.planner.event.mapper.EventInvitationMapper;
import com.team25.event.planner.event.mapper.EventMapper;
import com.team25.event.planner.event.model.*;
import com.team25.event.planner.event.repository.EventInvitationRepository;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.EventTypeRepository;
import com.team25.event.planner.event.specification.EventSpecification;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import com.team25.event.planner.user.model.Account;
import com.team25.event.planner.user.model.AccountStatus;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.model.UserRole;
import com.team25.event.planner.user.repository.AccountRepository;
import com.team25.event.planner.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final static int VERIFICATION_CODE_LENGTH = 64;

    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventMapper eventMapper;
    private final ActivityMapper activityMapper;
    private final EventSpecification eventSpecification;
    private final AccountRepository accountRepository;
    private final EventInvitationMapper eventInvitationMapper;
    private final EventInvitationRepository eventInvitationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundError("Event not found"));
        return eventMapper.toDTO(event);
    }

    public Page<EventResponseDTO> getEvents(EventFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        Specification<Event> spec = eventSpecification.createSpecification(filter);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return eventRepository.findAll(spec, pageable).map(eventMapper::toDTO);
    }

    // More complex validation is handled in a service method instead of validation annotations.
    // Note that this can also be achieved using custom validators.
    private void validateDto(@Valid EventRequestDTO eventDto) {
        if (eventDto.getEndDate().isBefore(LocalDate.now())) {
            throw new InvalidRequestError("Event must be scheduled in the future");
        }

        if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
            throw new InvalidRequestError("Start date must be before end date");
        }

        if (eventDto.getStartTime().isAfter(eventDto.getEndTime())) {
            throw new InvalidRequestError("Start time must be before end time");
        }
    }

    public EventResponseDTO createEvent(@Valid EventRequestDTO eventDto) {
        validateDto(eventDto);

        EventType eventType = eventTypeRepository.findById(eventDto.getEventTypeId())
                .orElseThrow(() -> new NotFoundError("Event type not found"));

        Event event = eventMapper.toEvent(eventDto, eventType);
        event = eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public EventResponseDTO updateEvent(Long id, @Valid EventRequestDTO eventDto) {
        validateDto(eventDto);

        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundError("Event not found"));

        // only certain fields are allowed to change
        event.setDescription(eventDto.getDescription());
        event.setMaxParticipants(eventDto.getMaxParticipants());

        event = eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Page<EventPreviewResponseDTO> getAllEvents(EventFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
      Specification<Event> spec = eventSpecification.createSpecification(filter);
      Sort.Direction direction = Sort.Direction.fromString(sortDirection);
      Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
      return eventRepository.findAll(spec, pageable).map(eventMapper::toEventPreviewResponseDTO);
    }


    public Page<EventPreviewResponseDTO> getTopEvents(String country, String city) {
        PageRequest pageable = PageRequest.of(0, 5);
        return eventRepository
                .findTopEvents(country, city, pageable)
                .map(eventMapper::toEventPreviewResponseDTO);
    }

    public boolean isProductSuitable(double price, OfferingCategoryType offeringCategoryType, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        Collection<BudgetItem> items = event.getBudgetItemCollection();
        for (BudgetItem item : items) {
            if (item.getOfferingCategoryType().equals(offeringCategoryType)) {
                if (price <= item.getMoney().getAmount()) {
                    item.getMoney().setAmount(item.getMoney().getAmount()-price);// save to repo
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public void sendInvitations(Long eventId, List<EventInvitationRequestDTO> requestDTO) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        requestDTO.stream().forEach(eventInvitationRequestDTO -> {
            EventInvitation eventInvitation = eventInvitationMapper.toEventInvitation(eventInvitationRequestDTO, event, EventInvitationStatus.PENDING);
            eventInvitation.setInvitationCode(VerificationCodeGenerator.generateVerificationCode(VERIFICATION_CODE_LENGTH));
            eventInvitationRepository.save(eventInvitation);
            Optional<Account> account = accountRepository.findByEmail(eventInvitationRequestDTO.getGuestEmail());
            if(!account.isEmpty()) {
                EventInvitationEmailDTO eventInvitationEmailDTO = eventInvitationMapper.toEventInvitationEmailDTO(account.get().getUser(), event, eventInvitation.getInvitationCode());
                emailService.sendEventInvitationEmail(account.get().getEmail(), eventInvitationEmailDTO);
            }else{
                EventInvitationShortEmailDTO dto = eventInvitationMapper.toEventInvitationShortEmailDto( event, eventInvitation.getInvitationCode());
                emailService.sendQuickRegisterEmail(eventInvitationRequestDTO.getGuestEmail(), dto);
            }
        });
    }

    public ActivityResponseDTO addActivityToAgenda(Long eventId, @Valid ActivityRequestDTO activityRequestDTO) {
        Activity activity = activityMapper.toActivity(activityRequestDTO);
        return activityMapper.toDTO(activity);
    }

    public List<ActivityResponseDTO> getEventAgenda(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        return event.getAgenda().stream().map(activityMapper::toDTO).toList();
    }
}
