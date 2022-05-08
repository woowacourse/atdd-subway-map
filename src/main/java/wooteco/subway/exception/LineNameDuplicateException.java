package wooteco.subway.exception;

public class LineNameDuplicateException extends SubwayValidationException {

    private static final String DUPLICATED_MESSAGE = "이미 존재하는 노선 이름입니다 : ";

    public LineNameDuplicateException(String name) {
        super(DUPLICATED_MESSAGE + name);
    }
}
