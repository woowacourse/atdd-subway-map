package wooteco.subway.admin.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import wooteco.subway.admin.controller.exception.InvalidStationFieldException;

import java.time.LocalDateTime;

public class Station {
    @Id
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public Station() {
    }

    public Station(String name) {
        validateName(name);
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    private void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new InvalidStationFieldException("역 이름이 입력되지 않았습니다.");
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
