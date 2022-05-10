package wooteco.subway.dto.info;

import java.util.List;

public class ResponseLineInfo {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationInfo> stationInfos;

    public ResponseLineInfo(Long id, String name, String color,
        List<StationInfo> stationInfos) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationInfos = stationInfos;
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

    public List<StationInfo> getStationInfos() {
        return stationInfos;
    }
}
