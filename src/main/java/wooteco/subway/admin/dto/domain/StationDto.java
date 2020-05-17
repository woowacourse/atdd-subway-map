package wooteco.subway.admin.dto.domain;

import wooteco.subway.admin.domain.Station;

import java.time.LocalDateTime;

public class StationDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public StationDto(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public StationDto(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.createdAt = station.getCreatedAt();
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

    public static class StationDtoBuilder {
        private Long id;
        private String name;
        private LocalDateTime createdAt;

        public StationDtoBuilder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StationDtoBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public StationDtoBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public StationDto build() {
            return new StationDto(this.id, this.name, this.createdAt);
        }
    }
}
