package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wooteco.subway.exception.section.InvalidDistanceException;

import java.util.Objects;

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

    public boolean isUpStation(Station targetStation) {
        return upStation.equals(targetStation);
    }

    public boolean isDownStation(Station targetStation) {
        return downStation.equals(targetStation);
    }

    public void updateUpStation(Section section) {
        int difference = distance.subtract(section.distance).intValue();
        upStation = section.downStation;
        distance = new SectionDistance(difference);
    }

    public void updateDownStation(Section section) {
        int difference = distance.subtract(section.distance).intValue();
        downStation = section.upStation;
        distance = new SectionDistance(difference);
    }

    public boolean isSameSection(Section newSection) {
        return (isUpStation(newSection.upStation) && isDownStation(newSection.downStation)) ||
                (isUpStation(newSection.downStation) && isDownStation(newSection.upStation));
    }

    public int getDistance(){
        return distance.intValue();
    }

}
