package wooteco.subway.domain;

import lombok.Getter;
import wooteco.subway.exception.line.InsufficientLineInformationException;
import wooteco.subway.exception.section.InvalidDistanceException;
import wooteco.subway.exception.section.NotPositiveDistanceException;

import java.util.Objects;

@Getter
public class Section {

    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    private Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        if (Objects.isNull(upStation) || Objects.isNull(downStation)) {
            throw new InsufficientLineInformationException();
        }
        if (distance <= 0) {
            throw new NotPositiveDistanceException();
        }
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Section section, Long lineId) {
        return of(section.getId(), lineId, section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return of(null, upStation, downStation, distance);
    }

    public static Section of(Long id, Station upStation, Station downStation, int distance) {
        return of(id, null, upStation, downStation, distance);
    }

    public static Section of(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        return new Section(id, lineId, upStation, downStation, distance);
    }

    public boolean isUpStation(Station targetStation) {
        return upStation.isSameId(targetStation.getId());
    }

    public boolean isDownStation(Station targetStation) {
        return downStation.isSameId(targetStation.getId());
    }

    public void updateId(Section section) {
        id = section.id;
        lineId = section.lineId;
    }

    public void updateUpStation(Section section) {
        int difference = distance - section.distance;

        if (difference <= 0) {
            throw new InvalidDistanceException();
        }
        upStation = section.downStation;
        distance = difference;
    }

    public void updateDownStation(Section section) {
        int difference = distance - section.distance;

        if (difference <= 0) {
            throw new InvalidDistanceException();
        }
        downStation = section.upStation;
        distance = difference;
    }

    public boolean isSameSection(Section newSection) {
        return (isUpStation(newSection.upStation) && isDownStation(newSection.downStation)) ||
                (isUpStation(newSection.downStation) && isDownStation(newSection.upStation));
    }

    public boolean hasStation(Long stationId) {
        return upStation.isSameId(stationId) || downStation.isSameId(stationId);
    }

    public boolean isUpStationId(Long stationId) {
        return upStation.isSameId(stationId);
    }

    public boolean isDownStationId(Long stationId) {
        return downStation.isSameId(stationId);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }
}
