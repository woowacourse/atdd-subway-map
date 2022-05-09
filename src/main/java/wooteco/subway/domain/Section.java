package wooteco.subway.domain;

import wooteco.subway.utils.exception.NotValidSectionCreateException;

public class Section {

    private static final int DISTANCE_STANDARD = 0;
    private static final String DISTANCE_FAIL_MESSAGE = "거리는 0km 초과이어야 합니다.";

    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= DISTANCE_STANDARD) {
            throw new NotValidSectionCreateException(DISTANCE_FAIL_MESSAGE);
        }
    }

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this(null, lineId, upStation, downStation, distance);
    }
}
