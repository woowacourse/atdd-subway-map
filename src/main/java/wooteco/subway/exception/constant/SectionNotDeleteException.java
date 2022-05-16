package wooteco.subway.exception.constant;

public class SectionNotDeleteException extends IllegalArgumentException {

    public static final String MESSAGE = "구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.";

    public SectionNotDeleteException() {
        super(MESSAGE);
    }
}
