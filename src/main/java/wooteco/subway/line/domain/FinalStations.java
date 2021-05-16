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

    public FinalStations addStations(final Section section) {
        return addStations(section.frontStationId(), section.backStationId());
    }

    public FinalStations addStations(final Long frontStationId, final Long backStationId) {
        if (this.upStationId.equals(backStationId)) {
            return new FinalStations(frontStationId, this.downStationId);
        }

        if (this.downStationId.equals(frontStationId)) {
            return new FinalStations(this.upStationId, backStationId);
        }

        return this;
    }

    public boolean isFinalSection(final Section section) {
        return isFinalSection(section.frontStationId(), section.backStationId());
    }

    public boolean isFinalSection(final Long frontStationId, final Long backStationId) {
        return this.upStationId.equals(backStationId) != this.downStationId.equals(frontStationId);
    }

    public FinalStations changeFinalStation(final Section section, final Long stationId) {
        if(upStationId.equals(stationId)){
            return new FinalStations(section.backStationId(), downStationId);
        }

        if(downStationId.equals(stationId)){
            return new FinalStations(upStationId, section.frontStationId());
        }

        throw new LineException("적절하지 않은 구간 삭제입니다.");
    }
}
