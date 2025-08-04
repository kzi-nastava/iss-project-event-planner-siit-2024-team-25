package com.team25.event.planner.user.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.team25.event.planner.user.model.PhoneNumber;

import java.io.IOException;

public class PhoneNumberSerializer extends JsonSerializer<PhoneNumber> {
    @Override
    public void serialize(PhoneNumber phoneNumber, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeString(phoneNumber.getPhoneNumber());
    }
}