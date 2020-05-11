package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;

public class Station {
    private static final String BLANK = " ";
    @Id
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public Station() {
    }

    public Station(String name) {
        validateNotEmpty(name);
        validateNotContainsBlank(name);
        validateNotContainsNumber(name);
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    private void validateNotEmpty(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
        }
    }

    private void validateNotContainsBlank(String name) {
        if (name.contains(BLANK)) {
            throw new IllegalArgumentException("이름은 공백이 포함될 수 없습니다.");
        }
    }

    private void validateNotContainsNumber(String name) {
        if (name.matches("[0-9]+")) {
            throw new IllegalArgumentException("이름에 숫자가 포함될 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Station station = (Station)o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdAt);
    }
}
