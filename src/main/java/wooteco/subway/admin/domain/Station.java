package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import wooteco.subway.admin.exception.InvalidDataException;

import java.time.LocalDateTime;
import java.util.Optional;

public class Station {
    @Id
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public Station() {
    }

    public Station(String name) {
        this.name = validateName(name);
        this.createdAt = LocalDateTime.now();
    }

    public Station(Long id, String name) {
        this(name);
        this.id = id;
    }

    private static String validateName(String name) {
        return Optional.ofNullable(name)
                .map(String::trim)
                .filter(it -> !it.isEmpty())
                .orElseThrow(() -> new InvalidDataException("역 이름은 반드시 있어야 합니다!"));
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
