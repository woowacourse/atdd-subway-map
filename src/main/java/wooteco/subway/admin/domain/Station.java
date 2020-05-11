package wooteco.subway.admin.domain;

import java.util.Objects;
import org.springframework.data.annotation.Id;

public class Station {

    @Id
    private Long id;
    private String name;

    public Station(String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(String name) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("Station.name은 null이 올 수 없습니다.");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Station.name은 빈 값이 올 수 없습니다.");
        }
        if (name.matches(".*[ ].*")) {
            throw new IllegalArgumentException("Station.name은 공백이 포함 될 수 없습니다.");
        }
        if (name.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Station.name은 숫자가 포함 될 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
