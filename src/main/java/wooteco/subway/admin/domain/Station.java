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

    public Station(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = LocalDateTime.now();
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
