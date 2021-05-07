package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Section {
    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(null, upStation, downStation, distance);
    }

    public boolean isStartStation(Station targetStation) {
        return upStation.isSameId(targetStation.getId());
    }

    public boolean isDownStation(Station targetStation) {
        return downStation.isSameId(targetStation.getId());
    }

    public void updateUpStation(Station targetStation) {
        upStation = targetStation;
    }

    public void updateDownStation(Station targetStation) {
        downStation = targetStation;
    }
}
