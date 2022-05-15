package wooteco.subway.dto.info;

import java.util.List;

public class ResponseToLineService {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationDto> stationDtos;

    public ResponseToLineService(Long id, String name, String color,
        List<StationDto> stationDtos) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationDtos = stationDtos;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationDto> getStationInfos() {
        return stationDtos;
    }
}
