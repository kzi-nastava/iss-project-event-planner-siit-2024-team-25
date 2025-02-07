package com.team25.event.planner.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.email.dto.EmailDTO;
import com.team25.event.planner.email.exception.EmailSendFailedException;
import com.team25.event.planner.email.service.EmailGeneratorService;
import com.team25.event.planner.email.service.EmailSenderService;
import com.team25.event.planner.email.service.EmailService;
import com.team25.event.planner.event.model.*;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.service.model.ReservationType;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.user.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private EmailGeneratorService emailGeneratorService;

    @Spy
    @InjectMocks
    private EmailService emailService;

    public Event event;
    public Service service;
    public Purchase purchase;

    private ListAppender<ILoggingEvent> listAppender;


    @BeforeEach
    public void setup() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(EmailService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);



        Account account1 = new Account(null, "account1@example.com", "password1", AccountStatus.ACTIVE, null, null);
        Location location = new Location("Serbia", "Belgrade", "Bulevar Kralja Aleksandra 10", 44.0, 20.0);
        PhoneNumber phone = new PhoneNumber("+381601234567");
        EventOrganizer eventOrganizer = new EventOrganizer(
                null,
                "John",
                "Doe",
                "https://example.com/johndoe.jpg",
                UserRole.EVENT_ORGANIZER,
                account1,
                null,
                null,
                null,
                null,
                location,
                phone
        );

        Owner owner = new Owner(
                null,
                "John",
                "Doe",
                "https://example.com/johndoe.jpg",
                UserRole.EVENT_ORGANIZER,
                account1,
                null,
                null,
                null,
                null,
                "Company",
                location,
                phone,
                "Description",
                null
        );

        OfferingCategory offeringCategory = new OfferingCategory(1L,"Conference Catering category", "", OfferingCategoryType.ACCEPTED);
        ArrayList<OfferingCategory> offeringCategories = new ArrayList<>();
        offeringCategories.add(offeringCategory);
        EventType conferenceType = new EventType(1L,"Description", "Conference", true, offeringCategories);

        LocalDate eventStartDate = LocalDate.now().plusYears(1).withDayOfMonth(1);
        LocalDate eventEndDate = LocalDate.now().plusYears(1).withDayOfMonth(1);
        LocalTime eventStartTime = LocalTime.of(9,0);
        LocalTime eventEndTime = LocalTime.of(18,0);

        event = new Event(1L, conferenceType, "Tech Conference 2024", "A conference about technology", 200, PrivacyType.PUBLIC,
                eventStartDate, eventEndDate, eventStartTime, eventEndTime, location, eventOrganizer, Instant.now(),null, null, null);

        service = new Service();
        service.setId(1L);
        service.setName("Conference Catering");
        service.setDescription("Premium catering service for tech events");
        service.setPrice(5000.0);
        service.setDiscount(0);
        service.setImages(null);
        service.setVisible(true);
        service.setAvailable(true);
        service.setSpecifics("Includes food, drinks, and coffee breaks");
        service.setReservationType(ReservationType.AUTOMATIC);
        service.setDuration(3);
        service.setReservationDeadline(24);
        service.setCancellationDeadline(24);
        service.setOfferingCategory(offeringCategory);
        service.setOwner(owner);
        service.setEventTypes(null);
        service.setStatus(OfferingType.ACCEPTED);

        purchase = new Purchase(1L, new Money(service.getPrice()), event.getStartDate(),event.getStartTime(), event.getEndDate(), event.getEndTime(), event, service, null);


    }

    @Test
    @DisplayName("Invalid send service purchase confirmation email when purchase is null")
    public void testSendServicePurchaseConfirmationInvalidPurchase(){


        IllegalArgumentException exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                emailService.sendServicePurchaseConfirmation(null));

        assertEquals(exception.getMessage(),"Purchase is null");

        verify(emailGeneratorService, times(0)).getServicePurchaseConfirmationEmail(any(), any());
        verify(emailSenderService, times(0)).sendEmail(any());
    }

    @Test
    @DisplayName("Valid send service purchase confirmation email ")
    public void testSendServicePurchaseConfirmationValid(){
        EmailDTO emailDTO = new EmailDTO("" ,"", "");
        Mockito.when(emailGeneratorService.getServicePurchaseConfirmationEmail(any(), any())).thenReturn(emailDTO);
        doNothing().when(emailSenderService).sendEmail(any());

        emailService.sendServicePurchaseConfirmation(purchase);

        verify(emailGeneratorService, times(2)).getServicePurchaseConfirmationEmail(any(), any());
        verify(emailSenderService, times(2)).sendEmail(any());
    }

    @Test
    @DisplayName("Invalid send service purchase confirmation email when email sender throw exception")
    public void testSendServicePurchaseConfirmationInvalidEmailSender(){
        Mockito.when(emailGeneratorService.getServicePurchaseConfirmationEmail(any(), any())).thenReturn(null);
        doThrow(new EmailSendFailedException()).when(emailSenderService).sendEmail(any());

        emailService.sendServicePurchaseConfirmation(purchase);

        boolean found = listAppender.list.stream()
                .anyMatch(event -> event.getLevel() == Level.WARN &&
                        event.getFormattedMessage().contains("Failed to send service purchase confirmation email"));

        long count = listAppender.list.stream().filter(event -> event.getLevel() == Level.WARN &&
                event.getFormattedMessage().contains("Failed to send service purchase confirmation email")).count();

        assertTrue(found);
        assertEquals(count, 1);
        verify(emailGeneratorService, times(2)).getServicePurchaseConfirmationEmail(any(), any());
        verify(emailSenderService, times(1)).sendEmail(any());
    }
}
