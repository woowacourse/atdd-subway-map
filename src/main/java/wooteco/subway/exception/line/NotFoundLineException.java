package wooteco.subway.exception.line;

import wooteco.subway.exception.NotFoundException;

public class NotFoundLineException extends NotFoundException {

    public NotFoundLineException() {
        super("해당 노선이 존재하지 않습니다.");
    }
}
