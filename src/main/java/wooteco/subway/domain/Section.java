package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wooteco.subway.common.Id;
import wooteco.subway.exception.badRequest.InvalidDistanceException;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Section {

    @Id
    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;

    public static Section create(Station upStation, Station downStation, int distance) {
        return create(null, upStation, downStation, distance);
    }

    public static Section create(Long id, Station upStation, Station downStation, int distance) {
        return new Section(id, upStation, downStation, distance);
    }

    public Long upStationId() {
        return upStation.getId();
    }

    public Long downStationId() {
        return downStation.getId();

    }

    public boolean isDownStation(Station targetStation) {
        return downStation.isSameId(targetStation.getId());
    }

    public boolean isDownStation(Long stationId) {
        return downStation.isSameId(stationId);
    }

    public boolean isUpStation(Station targetStation) {
        return upStation.isSameId(targetStation.getId());
    }

    public boolean isUpStation(Long stationId) {
        return upStation.isSameId(stationId);
    }

    public void updateUpStationFromDownStation(Section section) {
        int difference = distanceDifference(section);
        upStation = section.downStation;
        distance = difference;
    }

    public void updateDownStationFromUpStation(Section section) {
        int difference = distanceDifference(section);
        downStation = section.upStation;
        distance = difference;
    }

    private int distanceDifference(Section section) {
        int difference = distance - section.distance;
        if (difference <= 0) {
            throw new InvalidDistanceException();
        }
        return difference;
    }

    public boolean containsStation(Station station) {
        return isDownStation(station) || isUpStation(station);
    }

    public boolean containsStation(Long stationId) {
        return isUpStation(stationId) || isDownStation(stationId);
    }

    public void combineSection(Section section) {
        if (isDownStation(section.getUpStation())) {
            downStation = section.getDownStation();
        } else {
            upStation = section.getUpStation();
        }
    }
}
