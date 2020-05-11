package wooteco.subway.admin.domain.vo;

public class StationName extends Name{

    public StationName(String name) {
        super(name);
        validate(name);
    }

    private void validate(String name) {
        if (name.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Station.name은 숫자가 포함 될 수 없습니다.");
        }
    }
}
