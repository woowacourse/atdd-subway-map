package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import wooteco.subway.admin.exception.InvalidStationNameException;

import java.time.LocalDateTime;

public class Station {
    @Id
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public Station() {
    }

    public Station(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    private void validateName(String name) {
        if (name == null || name.equals("")) {
            throw new InvalidStationNameException("빈 이름");
        }
        if (name.contains(" ")) {
            throw new InvalidStationNameException("이름에 공백이 포함");
        }
        if (name.matches(".*[0-9].*")) {
            throw new InvalidStationNameException("이름에 숫자가 포함");
        }
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
