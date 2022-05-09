package wooteco.subway.utils.exceptions;

public class LineNotFoundException extends RuntimeException {

    public LineNotFoundException(Long id) {
        super(String.format("ID: %d 라인을 찾는데 실패하였습니다.", id));
    }
}
