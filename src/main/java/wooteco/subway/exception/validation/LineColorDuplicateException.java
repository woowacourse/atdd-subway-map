package wooteco.subway.exception.validation;

import wooteco.subway.exception.SubwayValidationException;

public class LineColorDuplicateException extends SubwayValidationException {

    private static final String DEFAULT_MESSAGE = "이미 존재하는 노선 색상입니다 : ";

    public LineColorDuplicateException(String color) {
        super(DEFAULT_MESSAGE + color);
    }
}
