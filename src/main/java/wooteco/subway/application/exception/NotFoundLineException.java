package wooteco.subway.application.exception;

public class NotFoundLineException extends NotFoundException {

    public NotFoundLineException(long id) {
        super(String.format("%d와 동일한 ID의 노선을 찾을 수 없습니다.", id));
    }
}
