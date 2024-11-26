package com.team25.event.planner.user.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.team25.event.planner.user.model.PhoneNumber;

import java.io.IOException;

public class PhoneNumberDeserializer extends JsonDeserializer<PhoneNumber> {
    @Override
    public PhoneNumber deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String phoneNumberString = parser.getText();
        return new PhoneNumber(phoneNumberString);
    }
}
