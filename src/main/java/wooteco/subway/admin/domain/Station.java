package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import wooteco.subway.admin.domain.exception.BlankNotAllowedInStationNameException;
import wooteco.subway.admin.domain.exception.NumberNotAllowedInStationNameException;
import wooteco.subway.admin.domain.exception.RequireStationNameException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

public class Station {
    private static final String NUMBER_REGEX = "[0-9]+";
    private static final String BLANK_SPACE = " ";

    @Id
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    private Station() {
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Station(String name) {
        validate(name);
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    private void validate(String stationName) {
        if (Objects.isNull(stationName) || stationName.isEmpty()) {
            throw new RequireStationNameException();
        }
        if (Pattern.compile(NUMBER_REGEX).matcher(stationName).find()) {
            throw new NumberNotAllowedInStationNameException();
        }
        if (stationName.contains(BLANK_SPACE)) {
            throw new BlankNotAllowedInStationNameException();
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
