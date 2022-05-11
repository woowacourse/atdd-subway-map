package wooteco.subway.exception.datanotfound;

import wooteco.subway.exception.ClientException;

public class DataNotFoundException extends ClientException {

    public DataNotFoundException(String message) {
        super(message);
    }
}
