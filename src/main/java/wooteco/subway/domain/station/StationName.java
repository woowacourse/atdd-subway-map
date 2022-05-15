package wooteco.subway.domain.station;

public class StationName {

    private final String name;

    public StationName(String name) {
        validateNameNotBlank(name);
        this.name = name;
    }

    private void validateNameNotBlank(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("지하철역 이름은 공백이 될 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "StationName{'" + name + "'}";
    }
}
