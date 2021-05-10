package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wooteco.subway.common.Id;

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

    public boolean isUpStation(Station targetStation) {
        return upStation.isSameId(targetStation.getId());
    }
}
