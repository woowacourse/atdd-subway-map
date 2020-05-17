package wooteco.subway.admin.exception;

/**
 *    LineStation에 관한 예외 클래스입니다.
 *
 *    @author HyungJu An
 */
public class LineStationNotFoundException extends RuntimeException {
	public LineStationNotFoundException(final String message) {
		super(message);
	}
}
