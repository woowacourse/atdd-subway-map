package wooteco.subway.exception;

public class SectionNotFoundException extends NotFoundException {

    public SectionNotFoundException() {
        super("존재하지 않는 지하철 구간입니다.");
    }
}
