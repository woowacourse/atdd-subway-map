package wooteco.subway.exception;

public class NoSuchStationInLineException extends NoSuchException {
    private static final String MESSAGE = "역이 노선에 존재하지 않습니다.";

    public NoSuchStationInLineException() {
        super(MESSAGE);
    }
}
