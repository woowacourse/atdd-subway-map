package wooteco.subway.admin.service.exceptions;

public class NotFoundException extends RuntimeException {
	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Long id) {
		super("데이터를 찾을 수 없습니다. id: " + id);
	}
}
