package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {
    private final Station upTermination = new Station(1L, "상행종점역");
    private final Station downTermination = new Station(2L, "하행종점역");
    private Section originalSection;

    @BeforeEach
    void setUp() {
        originalSection = new Section(upTermination, downTermination, 10);
    }

    @DisplayName("하행 종점이 같은 구간을 검사한다.")
    @Test
    void hasSameDownStationWith_true() {
        Section section = new Section(upTermination, downTermination, 3);
        assertThat(originalSection.hasSameDownStationWith(section)).isTrue();
    }

    @DisplayName("추가하는 구간과 양 방향 종점이 같은 구간이 노선에 존재하면 예외가 발생한다.")
    @Test
    void add_same_section() {
        Section section = new Section(upTermination, downTermination, 3);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> originalSection.splitRightBy(section))
                .withMessageContaining("양 방향 종점이 같아");
    }

    @DisplayName("추가하는 구간이 기존의 구간보다 길이가 길면 예외가 발생한다.")
    @Test
    void add_longer() {
        Station station = new Station(3L, "새로운역");
        Section section = new Section(upTermination, station, 11);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> originalSection.splitRightBy(section))
                .withMessageContaining("거리가 길어");
    }
}
