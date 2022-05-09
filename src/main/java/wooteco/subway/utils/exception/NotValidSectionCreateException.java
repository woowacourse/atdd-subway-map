package wooteco.subway.utils.exception;

public class NotValidSectionCreateException extends SubwayException{

    public NotValidSectionCreateException(String message) {
        super("[ERROR] 구간을 생성할 수 없습니다. cause: " + message);
    }
}
