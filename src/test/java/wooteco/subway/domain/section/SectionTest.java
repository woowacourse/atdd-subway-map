package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

@SuppressWarnings("NonAsciiCharacters")
class SectionTest {

    private final Station STATION1 = new Station(1L, "역1");
    private final Station STATION2 = new Station(2L, "역2");
    private final Station STATION3 = new Station(3L, "역3");
    private final Station STATION4 = new Station(4L, "역4");

    @DisplayName("생성자 유효성 검정 테스트")
    @Nested
    class InitTest {

        @Test
        void 동일한_지하철역_두개로_구간을_생성하려는_경우_예외_발생() {
            assertThatThrownBy(() -> new Section(STATION1, STATION1, 10))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 두_역_사이의_거리가_1미만인_구간을_생성하려는_경우_예외_발생() {
            assertThatThrownBy(() -> new Section(STATION1, STATION2, 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("hasStationOf 메서드는 구간에 특정 지하철역이 포함되었는지를 반환")
    @Nested
    class HasStationOfTest {

        @Test
        void 포함된_지하철역인_경우_참() {
            Section section = new Section(STATION1, STATION2, 10);

            boolean actual = section.hasStationOf(STATION1);

            assertThat(actual).isTrue();
        }

        @Test
        void 포함되지_않은_지하철역인_경우_거짓() {
            Section section = new Section(STATION1, STATION2, 10);

            boolean actual = section.hasStationOf(STATION3);

            assertThat(actual).isFalse();
        }
    }

    @Test
    void toStations_메서드는_상행역과_하행역을_순서대로_나열한_리스트를_반환() {
        Section section = new Section(STATION1, STATION2, 10);

        List<Station> actual = section.toStations();
        List<Station> expected = List.of(STATION1, STATION2);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("toConnectedDistance 메서드는 인접한 두 구간 연결한 거리를 반환")
    @Nested
    class ToConnectedDistanceTest {

        @Test
        void 인접한_구간인_경우_두_구간의_거리합을_반환() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION2, STATION3, 5);

            int actual = section1.toConnectedDistance(section2);
            int expected = 10 + 5;

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 두_구간이_인접할_수_없는_경우_예외발생() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION3, STATION4, 5);

            assertThatThrownBy(() -> section1.toConnectedDistance(section2))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("toRemainderDistance 메서드는 자신의 위에 덮어지는 짧은 구간과의 거리 차이를 반환")
    @Nested
    class ToRemainderDistanceTest {

        @Test
        void 상행역_혹은_하행역을_공유하는_구간인_경우_현재_구간에서의_거리_차이를_반환() {
            Section section1 = new Section(STATION1, STATION3, 10);
            Section section2 = new Section(STATION2, STATION3, 2);

            int actual = section1.toRemainderDistance(section2);
            int expected = 10 - 2;

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 두_구간이_겹쳐질_수_없는_경우_예외발생() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION3, STATION4, 5);

            assertThatThrownBy(() -> section1.toRemainderDistance(section2))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 두_구간의_거리가_동일한_경우_예외발생() {
            Section section1 = new Section(STATION1, STATION3, 10);
            Section section2 = new Section(STATION2, STATION3, 10);

            assertThatThrownBy(() -> section1.toRemainderDistance(section2))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 현재_구간보다_더_긴_구간을_대입한_경우_예외발생() {
            Section section1 = new Section(STATION1, STATION3, 10);
            Section section2 = new Section(STATION2, STATION3, 15);

            assertThatThrownBy(() -> section1.toRemainderDistance(section2))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
