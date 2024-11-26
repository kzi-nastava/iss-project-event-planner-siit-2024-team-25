package com.team25.event.planner.user.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.team25.event.planner.user.serializer.PhoneNumberDeserializer;
import com.team25.event.planner.user.serializer.PhoneNumberSerializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

@Value
@JsonSerialize(using = PhoneNumberSerializer.class)
@JsonDeserialize(using = PhoneNumberDeserializer.class)
public class PhoneNumber {
    // E.164 phone number string
    private static final String PHONE_NUMBER_REGEX = "^\\+[1-9]\\d{1,14}$";

    @NotNull
    @Pattern(regexp = PHONE_NUMBER_REGEX, message = "Phone number must be a valid E.164-compliant string.")
    String phoneNumber;
}
