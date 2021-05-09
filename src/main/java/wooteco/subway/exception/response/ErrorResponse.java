package wooteco.subway.exception.response;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ErrorResponse {
    private final String reason;

    @JsonCreator
    public ErrorResponse(final String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
