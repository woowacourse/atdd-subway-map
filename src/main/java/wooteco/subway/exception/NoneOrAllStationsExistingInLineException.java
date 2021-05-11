package wooteco.subway.exception;

public class NoneOrAllStationsExistingInLineException extends
    SectionException {

    public NoneOrAllStationsExistingInLineException() {
        super("상행역과 하행역 중 하나만 노선 내 현존하는 구간에 포함되어 있어야 합니다.");
    }
}
