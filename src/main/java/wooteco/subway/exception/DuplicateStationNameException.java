package wooteco.subway.exception;

public class DuplicateStationNameException extends DuplicateException {

    private static final String MESSAGE = "중복된 역 이름입니다.";

    public DuplicateStationNameException(Throwable e) {
        super(MESSAGE, e);
    }

}
