package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wooteco.subway.exception.section.DuplicatedSectionException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Section {

    private Long id;
    private Station upStation;
    private Station downStation;
    private SectionDistance distance;

    public static Section create(Station upStation, Station downStation, int distance) {
        return create(null, upStation, downStation, distance);
    }

    public static Section create(Long id, Station upStation, Station downStation, int distance) {
        return new Section(id, upStation, downStation, new SectionDistance(distance));
    }

    public boolean isUpStation(Station target) {
        return upStation.equals(target);
    }

    public boolean isDownStation(Station target) {
        return downStation.equals(target);
    }

    public Section updateByNewSection(Section target) {
        if (upStation.equals(target.upStation)) {
            int difference = distance.subtract(target.distance).intValue();
            upStation = target.downStation;
            distance = new SectionDistance(difference);
            return this;
        }
        if (downStation.equals(target.downStation)) {
            int difference = distance.subtract(target.distance).intValue();
            downStation = target.upStation;
            distance = new SectionDistance(difference);
            return this;
        }
        return this;
    }

    public boolean isSameOrReversed(Section target) {
        return (isUpStation(target.upStation) && isDownStation(target.downStation)) ||
                (isUpStation(target.downStation) && isDownStation(target.upStation));
    }

    public boolean isAdjacent(Section target) {
        if (isSameOrReversed(target)) {
            throw new DuplicatedSectionException();
        }
        return isUpStation(target.getUpStation()) ||
                isDownStation(target.getDownStation()) ||
                isUpStation(target.getDownStation()) ||
                isDownStation(target.getUpStation());
    }

    public int getDistance() {
        return distance.intValue();
    }

}
