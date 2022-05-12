package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Section {

    public static final int MINIMUM_DISTANCE = 1;
    public static final String UNVALID_DISTANCE_EXCEPTION = "종점 사이 거리는 양의 정수여야 합니다.";
    public static final String UNVALID_STATION_EXCEPTION = "상행 종점과 하행 종점은 같을 수 없습니다.";
    public static final String UNABLE_TO_MERGE_EXCEPTION = "합칠 수 없는 section입니다.";
    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private Integer distance;

    public Section(Long id, Line line, Station upStation, Station downStation, Integer distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Long id, Line line, Station upStation, Station downStation, Integer distance) {
        return new Section(id, line, upStation, downStation, distance);
    }

    public static Section of(Long id, Section other) {
        return Section.of(id, other.line, other.upStation, other.downStation, other.distance);
    }

    public static Section of(Line line, Station upStation, Station downStation, Integer distance) {
        validate(upStation, downStation, distance);
        return Section.of(null, line, upStation, downStation, distance);
    }

    private static void validate(Station upStation, Station downStation, Integer distance) {
        checkUpStationAndDownStationIsDifferent(upStation, downStation);
        checkDistanceValueIsValid(distance);
    }

    private static void checkDistanceValueIsValid(Integer distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new IllegalArgumentException(UNVALID_DISTANCE_EXCEPTION);
        }
    }

    private static void checkUpStationAndDownStationIsDifferent(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(UNVALID_STATION_EXCEPTION);
        }
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public boolean isAbleToLinkOnUpStation(Section other) {
        return upStation.equals(other.downStation);
    }

    public boolean isAbleToLinkOnDownStation(Section other) {
        return downStation.equals(other.upStation);
    }

    public boolean hasStation(Station station) {
        return (upStation.equals(station) || downStation.equals(station));
    }

    public boolean isSameUpStation(Section other) {
        return upStation.equals(other.upStation);
    }

    public boolean isSameDownStation(Section other) {
        return downStation.equals(other.downStation);
    }

    public boolean ableToDivide(Section newSection) {
        return (isSameUpStation(newSection) != isSameDownStation(newSection)) && (distance > newSection.distance);
    }

    public List<Section> divide(Section newSection) {
        List<Section> parts = new ArrayList<>();
        if (isSameUpStation(newSection)) {
            parts.add(Section.of(line, upStation, newSection.downStation, newSection.distance));
            parts.add(Section.of(line, newSection.downStation, downStation, distance - newSection.distance));
            return parts;
        }
        parts.add(Section.of(line, upStation, newSection.upStation, newSection.distance));
        parts.add(Section.of(line, newSection.upStation, downStation, distance - newSection.distance));
        return parts;
    }

    public Section merge(Section other) {
        checkAbleToMerge(other);
        if (isAbleToLinkOnDownStation(other)) {
            return new Section(null, line, upStation, other.downStation, distance + other.distance);
        }
        return new Section(null, line, other.upStation, downStation, distance + other.distance);
    }

    private void checkAbleToMerge(Section other) {
        if (!(isAbleToLinkOnUpStation(other) || isAbleToLinkOnDownStation(other))) {
            throw new IllegalArgumentException(UNABLE_TO_MERGE_EXCEPTION);
        }
    }

    public Long getLineId() {
        return line.getId();
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }
}
