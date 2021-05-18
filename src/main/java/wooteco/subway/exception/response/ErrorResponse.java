package wooteco.subway.exception.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
    private final String reason;

    @JsonCreator
    public ErrorResponse(final @JsonProperty(value = "reason") String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
