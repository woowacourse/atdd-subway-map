package wooteco.subway.exception;

public class InvalidSectionDistanceException extends SectionException {

    public InvalidSectionDistanceException() {
        super("새 구간의 길이는 원래 있는 구간의 길이보다 작아야 합니다.");
    }
}
