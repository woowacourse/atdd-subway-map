package wooteco.subway.line.domain;

import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.domain.Section;

public class FinalStations {
    private final Long upStationId;
    private final Long downStationId;

    public FinalStations(final Long upStationId, final Long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long upStationId() {
        return upStationId;
    }

    public Long downStationId() {
        return downStationId;
    }

    public boolean isFinalSection(final Section section) {
        return isFinalSection(section.frontStationId(), section.backStationId());
    }

    public boolean isFinalSection(final Long frontStationId, final Long backStationId) {
        return this.upStationId.equals(backStationId) != this.downStationId.equals(frontStationId);
    }

    // TODO :: before에 해당하는 종점역을 after로 변경한다는 것인데, 괜찮을까...? 변수명은 어떻게 해야할까
    public FinalStations change(final Long before, final Long after) {
        if (upStationId.equals(before)) {
            return new FinalStations(after, downStationId);
        }

        if (downStationId.equals(before)) {
            return new FinalStations(upStationId, after);
        }

        throw new LineException("적절하지 않은 구간 삭제입니다.");
    }

    public boolean isFinalStation(final Long stationId) {
        return upStationId.equals(stationId) || downStationId.equals(stationId);
    }

    public boolean isUpStation(final Long stationId) {
        return upStationId.equals(stationId);
    }

    public boolean isDownStation(final Long stationId) {
        return downStationId.equals(stationId);
    }

    public FinalStations addSection(final Section section) {
        return update(section.backStationId(), section.frontStationId());
    }

    public FinalStations deleteSection(final Section section) {
        return update(section.frontStationId(), section.backStationId());
    }

    private FinalStations update(final Long station1, final Long station2) {
        if (this.upStationId.equals(station1)) {
            return new FinalStations(station2, this.downStationId);
        }

        if (this.downStationId.equals(station2)) {
            return new FinalStations(this.upStationId, station1);
        }

        throw new LineException("종점이 아닙니다.");
    }
}
