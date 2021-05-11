package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wooteco.subway.exception.section.InvalidDistanceException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Section {

    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

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

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }
}
