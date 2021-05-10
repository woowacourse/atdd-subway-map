package wooteco.subway.section.service;

public class UnavailableSectionDeleteException extends SectionException {
    private static final String MESSAGE = "구간은 최소 1개가 필요합니다. 해당 구간을 삭제할 수 없습니다.";

    public UnavailableSectionDeleteException() {
        super(MESSAGE);
    }
}
