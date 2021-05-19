package wooteco.subway.station.controller.dto;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.station.service.dto.StationDto;

public class StationResponse {

    private Long id;
    private String name;

    public StationResponse() {
    }

    private StationResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(StationDto stationDto) {
        return new StationResponse(stationDto.getId(), stationDto.getName());
    }

    public static List<StationResponse> ofList(final List<StationDto> stationInfos) {
        return stationInfos.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
