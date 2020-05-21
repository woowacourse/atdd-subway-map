package wooteco.subway.admin.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import wooteco.subway.admin.exception.InvalidNameException;
import wooteco.subway.admin.utils.Validator;

public class Station {
    private static final String NOT_CONTAINS_NUMBER_REGEX = "[0-9]+";

    @Id
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public Station() {
    }

    public Station(String name) {
        Validator.validateNotEmpty(name);
        Validator.validateNotContainsBlank(name);
        validateNotContainsNumber(name);
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    private void validateNotContainsNumber(String name) {
        if (name.matches(NOT_CONTAINS_NUMBER_REGEX)) {
            throw new InvalidNameException("이름에 숫자가 포함될 수 없습니다.");
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
