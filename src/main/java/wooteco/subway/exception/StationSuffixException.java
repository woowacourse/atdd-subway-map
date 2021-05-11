package wooteco.subway.exception;

public class StationSuffixException extends SubwayException {
    public StationSuffixException() {
        super("-역으로 끝나는 이름을 입력해주세요.");
    }
}
