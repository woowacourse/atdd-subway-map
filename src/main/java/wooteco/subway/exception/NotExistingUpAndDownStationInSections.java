package wooteco.subway.exception;

public class NotExistingUpAndDownStationInSections extends RuntimeException {

    public NotExistingUpAndDownStationInSections() {
        super("구간의 상행역과 하행역 모두 추가하려는 구간 목록에 존재하지 않아 구간을 추가할 수 없습니다.");
    }
}
