package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class Station {
    @Id
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public Station(String name) {
        validateName(name);
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    private void validateName(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("역 이름이 비어있습니다.");
        }
        if (name.contains(" ")) {
            throw new IllegalArgumentException("역 이름에 공백이 포함되어 있습니다.");
        }
        if (name.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("역 이름에 숫자가 포함되어 있습니다.");
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
}
