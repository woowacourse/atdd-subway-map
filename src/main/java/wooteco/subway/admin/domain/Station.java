package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import wooteco.subway.admin.exception.InvalidStationNameException;

public class Station {
    private static final String NUMBER_REGEX = ".*[0-9].*";
    private static final String BLANK = " ";

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
        if (name == null || name.isEmpty()) {
            throw new InvalidStationNameException("빈 이름");
        }
        if (name.contains(BLANK)) {
            throw new InvalidStationNameException("이름에 공백이 포함");
        }
        if (name.matches(NUMBER_REGEX)) {
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
