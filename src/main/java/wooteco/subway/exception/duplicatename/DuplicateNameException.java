package wooteco.subway.exception.duplicatename;

import wooteco.subway.exception.ClientException;

public class DuplicateNameException extends ClientException {

    public DuplicateNameException(String message) {
        super(message);
    }
}
