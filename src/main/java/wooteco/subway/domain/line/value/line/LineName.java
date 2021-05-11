package wooteco.subway.domain.line.value.line;

import java.util.Objects;

public final class LineName {

    private final String name;

    public LineName(String name) {
        validateLineNameSize(name);
        this.name = name;
    }

    private void validateLineNameSize(String name) {
        if(name.isEmpty()) {
            throw new IllegalArgumentException("글자 수는 0보다 커야 합니다.");
        }
    }

    public String asString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineName lineName1 = (LineName) o;
        return Objects.equals(name, lineName1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
