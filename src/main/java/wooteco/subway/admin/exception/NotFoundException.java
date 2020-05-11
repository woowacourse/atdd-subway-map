package wooteco.subway.admin.exception;

public class NotFoundException extends IllegalArgumentException {
    public NotFoundException(Long id) {
        super(id + "존재하지 않는 id입니다.");
    }
}
