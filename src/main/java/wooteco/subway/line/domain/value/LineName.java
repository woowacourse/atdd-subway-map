package wooteco.subway.line.domain.value;

import java.util.Objects;

public final class LineName {

    private final String lineName;

    public LineName(String lineName) {
        this.lineName = lineName;
    }

    public String asString() {
        return lineName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineName lineName1 = (LineName) o;
        return Objects.equals(lineName, lineName1.lineName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineName);
    }

}
