package wooteco.subway.exception;

public class UniqueSectionDeleteException extends SectionException {

    public UniqueSectionDeleteException() {
        super("구간이 1개인 경우 역을 삭제할 수 없습니다.");
    }
}
