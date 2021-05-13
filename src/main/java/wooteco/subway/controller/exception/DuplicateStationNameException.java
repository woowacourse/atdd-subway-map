package wooteco.subway.controller.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateStationNameException extends DuplicateKeyException {
    private static String msg = "이미 존재하는 역의 이름입니다. 중복되는 역 이름 : ";

    public DuplicateStationNameException(String name) {
        super(msg + name);
    }
}
