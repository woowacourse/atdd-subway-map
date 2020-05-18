package wooteco.subway.admin.dto.response;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

import java.util.List;

public class StationsAtLineResponse {
    private Long lineId;
    private String lineName;
    private String bgColor;
    private List<Station> stations;

    private StationsAtLineResponse() {
    }

    private StationsAtLineResponse(Long lineId, String lineName, String bgColor,
                                   List<Station> stations) {
        this.lineId = lineId;
        this.lineName = lineName;
        this.bgColor = bgColor;
        this.stations = stations;
    }

    public StationsAtLineResponse(Line line, List<Station> stations) {
        this(line.getId(), line.getName(), line.getBgColor(), stations);
    }

    public Long getLineId() {
        return lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public String getBgColor() {
        return bgColor;
    }

    public List<Station> getStations() {
        return stations;
    }
}
