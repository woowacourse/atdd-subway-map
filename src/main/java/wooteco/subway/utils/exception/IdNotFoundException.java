package wooteco.subway.utils.exception;

public class IdNotFoundException extends SubwayException {

    public static final String NO_ID_MESSAGE = "[ERROR] 해당 ID로 조회할 수 없습니다. error id = ";

    public IdNotFoundException(String message) {
        super(message);
    }
}
