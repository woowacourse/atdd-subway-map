package wooteco.subway.exception.badrequest;

public class NoRowAffectedException extends BadRequest {

    public NoRowAffectedException(String msg) {
        super(msg);
    }

    public NoRowAffectedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
