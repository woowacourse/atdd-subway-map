package wooteco.subway.exception;

public class LineSuffixException extends SubwayException {
    public LineSuffixException() {
        super("-선으로 끝나는 이름을 입력해주세요.");
    }
}
