package wooteco.subway.dto;

import wooteco.subway.domain.Station;

import javax.validation.constraints.NotBlank;

public class StationResponse {
    @NotBlank(message = "응답하려는 역의 id 값이 비었습니다.")
    private Long id;
    @NotBlank(message = "응답하려는 역의 name 값이 비었습니다.")
    private String name;

    private StationResponse() {
    }

    private StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
