package kr.hs.dgsw.javasuhaeng.domain.dto.request;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class StockIdDeserializer extends ValueDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser parser, DeserializationContext context) {
        if (parser.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return context.reportInputMismatch(Long.class, "종목 ID는 정수여야 합니다.");
        }
        return context.readValue(parser, Long.class);
    }
}
