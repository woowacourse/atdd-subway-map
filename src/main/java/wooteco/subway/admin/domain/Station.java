package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class Station {
    @Id
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public Station() {
    }

    public Station(Long id, String name) {
        this.id = id;
        if (name.isEmpty() || name.contains(" ") || name.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("잘못된 역이름입니다.");
        }
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Station(String name) {
        this(null, name);
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
