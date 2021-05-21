package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class InvalidDistanceException extends SubwayException {

    public InvalidDistanceException(final int minimumDistance) {
        super(String.format("입력된 값이 최소 거리 값 %d 보다 작습니다.", minimumDistance));
    }
}

