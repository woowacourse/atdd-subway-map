package wooteco.subway.admin.domain.exception;

public class NotFoundLineException extends IllegalArgumentException{
    public NotFoundLineException(){
        super("해당 아이디의 노선이 존재하지 않습니다");
    }
}
