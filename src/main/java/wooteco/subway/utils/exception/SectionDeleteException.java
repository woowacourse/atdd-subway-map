package wooteco.subway.utils.exception;

public class SectionDeleteException extends SubwayException {

    public SectionDeleteException() {
        super("[ERROR] 구간을 삭제할 수 없습니다.");
    }
}
