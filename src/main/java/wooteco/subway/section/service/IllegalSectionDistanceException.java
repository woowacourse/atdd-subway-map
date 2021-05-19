package wooteco.subway.section.service;

public class IllegalSectionDistanceException extends SectionException {
    private static final String MESSAGE = "구간의 길이는 변경될 구간의 길이보다 크거나 같을 수 없습니다.";

    public IllegalSectionDistanceException() {
        super(MESSAGE);
    }
}
