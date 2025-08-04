package com.team25.event.planner.user.converter;

import com.team25.event.planner.user.model.PhoneNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String> {
    @Override
    public String convertToDatabaseColumn(PhoneNumber phoneNumber) {
        return phoneNumber == null ? null : phoneNumber.getPhoneNumber();
    }

    @Override
    public PhoneNumber convertToEntityAttribute(String phoneNumberString) {
        return phoneNumberString == null ? null : new PhoneNumber(phoneNumberString);
    }
}
