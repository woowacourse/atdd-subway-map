package wooteco.subway.utils.exception;

public class NameDuplicatedException extends SubwayException {

    public static final String NAME_DUPLICATE_MESSAGE = "[ERROR] 이미 존재하는 이름입니다. error name = ";

    public NameDuplicatedException(String message) {
        super(message);
    }
}
