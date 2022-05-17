package wooteco.subway.domain.line;

import java.util.List;

import wooteco.subway.domain.station.Station;

public interface LineRepository {

    Line saveLine(Line line);

    List<Line> findLines();

    Line findLineById(Long lineId);

    Line updateLine(Line line);

    void updateSections(Line line);

    void removeLine(Long lineId);

    Station findStationById(Long stationId);
}
