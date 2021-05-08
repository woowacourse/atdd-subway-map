package wooteco.subway.service.dto;

import javax.validation.constraints.NotEmpty;

public class StationServiceDto {

    private final Long id;
    @NotEmpty
    private final String name;

    public StationServiceDto(final Long id) {
        this(id, null);
    }

    public StationServiceDto(final String name) {
        this(null, name);
    }

    public StationServiceDto(final Long id, final String name) {
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
