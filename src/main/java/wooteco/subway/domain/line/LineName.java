package wooteco.subway.domain.line;

public class LineName {

    private final String name;

    public LineName(String name) {
        validateNameNotBlank(name);
        this.name = name;
    }

    private void validateNameNotBlank(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("지하철노선 이름은 공백이 될 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "LineName{'" + name + "'}";
    }
}
