package wooteco.subway.controller.dto.response;

import javax.validation.constraints.NotEmpty;

public class StationResponse {

    @NotEmpty
    private Long id;
    @NotEmpty
    private String name;

    public StationResponse() {
    }

    public StationResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
