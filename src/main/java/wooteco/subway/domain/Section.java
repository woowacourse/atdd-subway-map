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

    public static class Builder {

        private final Line line;
        private final Station upStation;
        private final Station downStation;
        private final Integer distance;

        private Long id = null;

        public Builder(Line line, Station upStation, Station downStation, Integer distance) {
            this.line = line;
            this.upStation = upStation;
            this.downStation = downStation;
            this.distance = distance;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Section build() {
            validate(this);
            return new Section(this);
        }

        private void validate(Builder builder) {
            checkUpStationAndDownStationIsDifferent(builder);
            checkDistanceValueIsValid(builder);
        }

        private void checkDistanceValueIsValid(Builder builder) {
            if (builder.distance < MINIMUM_DISTANCE) {
                throw new IllegalArgumentException(UNVALID_DISTANCE_EXCEPTION);
            }
        }

        private void checkUpStationAndDownStationIsDifferent(Builder builder) {
            if (builder.upStation.equals(downStation)) {
                throw new IllegalArgumentException(UNVALID_STATION_EXCEPTION);
            }
        }
    }

    private Section(Builder builder) {
        id = builder.id;
        line = builder.line;
        upStation = builder.upStation;
        downStation = builder.downStation;
        distance = builder.distance;;
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
            parts.add(new Section.Builder(line, upStation, newSection.downStation, newSection.distance)
                    .build());
            parts.add(new Section.Builder(line, newSection.downStation, downStation, distance - newSection.distance)
                    .build());
            return parts;
        }
        parts.add(new Section.Builder(line, upStation, newSection.upStation, newSection.distance)
                .build());
        parts.add(new Section.Builder(line, newSection.upStation, downStation, distance - newSection.distance)
                .build());
        return parts;
    }

    public Section merge(Section other) {
        checkAbleToMerge(other);
        if (isAbleToLinkOnDownStation(other)) {
            return new Section.Builder(line, upStation, other.downStation, distance + other.distance)
                    .build();
        }
        return new Section.Builder(line, other.upStation, downStation, distance + other.distance)
                .build();
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
