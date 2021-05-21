package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class LineSuffixException extends SubwayException {
    public LineSuffixException() {
        super("-선으로 끝나는 이름을 입력해주세요.");
    }
}
