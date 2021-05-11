package wooteco.subway.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateLineNameException extends DuplicateKeyException {
    private static String msg = "이미 존재하는 노선의 이름입니다. 중복되는 노선 이름 : ";

    public DuplicateLineNameException(String name) {
        super(msg + name);
    }
}
