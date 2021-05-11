package wooteco.subway.exception;

public class LineDuplicationException extends SubwayException {
    public LineDuplicationException() {
        super("이미 존재하는 노선색깔입니다.");
    }
}
