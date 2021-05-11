package wooteco.subway.exception;

public class NoLineException extends SubwayException {

    public NoLineException() {
        super("존재하지 않는 노선입니다.");
    }
}
