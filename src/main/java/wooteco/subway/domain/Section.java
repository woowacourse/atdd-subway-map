package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private static final String ERROR_INVALID_DISTANCE = "[ERROR] 부적절한 거리가 입력되었습니다. 0보다 큰 거리를 입력해주세요.";
    private static final int INVALID_DINSTANCE_STANDARD = 0;
    private final String ERROR_SAME_STATION = "[ERROR] 상행 종점과 하행 종점이 같을 수 없습니다.";

    private final Long id;
    private final Long lineId;
    private final int distance;
    private final Station upStation;
    private final Station downStation;

    public Section(final Long lineId, final Station upStation, final Station downStation, final int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Section(final Long id, final Long lineId, final Station upStation, final Station downStation,
                   final int distance) {
        validateStations(upStation, downStation);
        validateDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateStations(final Station upStationId, final Station downStationId) {
        if (Objects.equals(upStationId, downStationId)) {
            throw new IllegalArgumentException(ERROR_SAME_STATION);
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= INVALID_DINSTANCE_STANDARD) {
            throw new IllegalArgumentException(ERROR_INVALID_DISTANCE);
        }
    }
}
