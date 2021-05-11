package wooteco.subway.exception;

public class SectionDuplicationException extends SubwayException {
    public SectionDuplicationException() {
        super("상행역과 하행역은 중복되지 않게 입력해주세요.");
    }
}
