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
        return new Section(null, upStation, downStation, distance);
    }

    public Long upStationId() {
        return upStation.getId();
    }

    public Long downStationId() {
        return downStation.getId();

    }
}
