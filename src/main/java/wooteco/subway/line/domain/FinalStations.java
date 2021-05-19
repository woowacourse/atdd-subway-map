package wooteco.subway.line.domain;

import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.domain.Section;

public class FinalStations {
    private final Long firstStationId;
    private final Long lastStationId;

    public FinalStations(final Long firstStationId, final Long lastStationId) {
        this.firstStationId = firstStationId;
        this.lastStationId = lastStationId;
    }

    public boolean isFinalSection(final Section section) {
        return this.firstStationId.equals(section.backStationId()) != this.lastStationId.equals(section.frontStationId());
    }

    public boolean isFinalStation(final Long stationId) {
        return firstStationId.equals(stationId) || lastStationId.equals(stationId);
    }

    public FinalStations addSection(final Section section) {
        return update(section.backStationId(), section.frontStationId());
    }

    public FinalStations deleteSection(final Section section) {
        return update(section.frontStationId(), section.backStationId());
    }

    private FinalStations update(final Long station1, final Long station2) {
        if (this.firstStationId.equals(station1)) {
            return new FinalStations(station2, this.lastStationId);
        }

        if (this.lastStationId.equals(station2)) {
            return new FinalStations(this.firstStationId, station1);
        }

        throw new LineException("종점이 아닙니다.");
    }

    public Long firstStationId() {
        return firstStationId;
    }

    public Long lastStationId() {
        return lastStationId;
    }
}
