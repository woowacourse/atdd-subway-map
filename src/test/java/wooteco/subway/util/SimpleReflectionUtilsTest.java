package wooteco.subway.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.station.Station;

class SimpleReflectionUtilsTest {

    @Test
    @DisplayName("ID를 주입한다.")
    public void injectId() {
        // given
        Station station = new Station("hello");
        // when
        SimpleReflectionUtils.injectId(station, 3L);
        // then
        assertThat(station.getId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("어노테이션을 기반으로 ID를 주입한다.")
    public void injectIdByAnnotation() {
        // given
        class TestClass {
            @Id
            private Long hello;
        }
        final TestClass test = new TestClass();

        // when
        SimpleReflectionUtils.injectId(test, 10L);
        // then
        assertThat(test.hello).isEqualTo(10L);
    }
}