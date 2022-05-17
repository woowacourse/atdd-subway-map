package wooteco.subway.domain;

import java.util.Arrays;
import java.util.function.BiPredicate;

public enum Direction {
    UP((section, inputSection) -> section.getUpStationId().equals(inputSection.getDownStationId())),
    DOWN(((section, inputSection) -> section.getDownStationId().equals(inputSection.getUpStationId()))),
    BETWEEN_UP((section, inputSection) -> (section.getUpStationId().equals(inputSection.getUpStationId()))),
    BETWEEN_DOWN((section, inputSection) -> section.getDownStationId().equals(inputSection.getDownStationId()));

    private final BiPredicate<Section, Section> biPredicate;

    Direction(BiPredicate<Section, Section> biPredicate) {
        this.biPredicate = biPredicate;
    }

    public static Direction findDirection(Section section, Section inputSection) {
        return Arrays.stream(Direction.values())
                .filter(value -> value.biPredicate.test(section, inputSection))
                .findAny()
                .orElseThrow();
    }
}
