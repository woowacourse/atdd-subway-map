package wooteco.subway.domain;

import java.util.Objects;

public class Id {

    private static final Long TEMPORARY_ID = 0L;

    private final Long id;

    public Id(Long id) {
        validateIdPositive(id);
        this.id = id;
    }

    public Id() {
        this.id = TEMPORARY_ID;
    }

    private void validateIdPositive(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("식별자는 양수여야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Id id1 = (Id) o;
        return Objects.equals(id, id1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Id{" + id + '}';
    }
}
