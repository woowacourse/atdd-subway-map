package wooteco.subway.admin.station.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.LocalDateTime;

public class Station {
    @Id
    private Long id;
    private String name;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Station(String name) {
        this.name = name;
    }

    @PersistenceConstructor
    public Station(final Long id, final String name, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void throwAlreadyExistNameException() {
        throw new IllegalArgumentException(String.format("%s 이미 존재하는 역 이름입니다.", this.name));
    }

    public boolean isSameId(final Long stationId) {
        return this.id.equals(stationId);
    }
}
