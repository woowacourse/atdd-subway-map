package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {
    private Section section1;
    private Section section2;

    @BeforeEach
    void setUp() {
        section1 = new Section(1L, 1L, new Station(1L), new Station(2L),8);
        section2 = new Section(2L, 1L, new Station(1L), new Station(3L),10);
    }

    @Test
    @DisplayName("section 객체 생성확인")
    void create() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(0L, 1L, 1L, 2L, 8);

        assertThat(section1).isEqualTo(section2);
    }

    @Test
    @DisplayName("거리가 더 작은지 비교한다.")
    void lessDistanceThan() {
        assertThat(section1.lessDistanceThan(section2)).isTrue();
    }

    @Test
    @DisplayName("거리를 뺀 값을 구한다..")
    void subtractDistance() {
        assertThat(section2.subtractDistance(section1)).isEqualTo(2);
    }
}