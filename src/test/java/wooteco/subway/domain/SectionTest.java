package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SectionTest {

    @DisplayName("상행역과 하행역 중 특정 역이 있는지 확인한다.")
    @ParameterizedTest
    @CsvSource({"1,true", "2,true", "3,false"})
    void 역이_존재하는지_확인(Long stationId, boolean expected) {
        Section section = new Section(1L, 1L, 2L, 7);

        boolean result = section.existsStation(stationId);

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("상행역이 서로 같은지 비교한다.")
    @Test
    void 상행역_비교() {
        Section section = new Section(1L, 1L, 2L, 7);
        Section otherSection = new Section(1L, 1L, 4L, 4);

        boolean result = section.isSameUpStation(otherSection);

        assertThat(result).isTrue();
    }

    @DisplayName("하행역이 서로 같은지 비교한다.")
    @Test
    void 하행역_비교() {
        Section section = new Section(1L, 1L, 2L, 7);
        Section otherSection = new Section(1L, 3L, 2L, 4);

        boolean result = section.isSameDownStation(otherSection);

        assertThat(result).isTrue();
    }

    @DisplayName("상행역과 하행역이 모두 같은지 비교한다.")
    @Test
    void 상행역_하행역_비교() {
        Section section = new Section(1L, 1L, 2L, 7);
        Section otherSection = new Section(1L, 1L, 2L, 4);

        boolean result = section.isSameUpAndDownStation(otherSection);

        assertThat(result).isTrue();
    }

    @DisplayName("연결 가능한지 여부를 반환한다.")
    @Test
    void 연결_가능_여부() {
        Section section = new Section(1L, 1L, 3L, 7);
        Section otherSection = new Section(1L, 3L, 2L, 4);

        boolean result = section.isConnect(otherSection);

        assertThat(result).isTrue();
    }

    @DisplayName("상행역과 길이를 변경한다.")
    @Test
    void 상행역_길이_변경() {
        Section section = new Section(1L, 1L, 3L, 7);
        Section otherSection = new Section(1L, 1L, 2L, 4);

        section.changeUpStation(otherSection);

        assertAll(
                () -> assertThat(section.getUpStationId()).isEqualTo(2L),
                () -> assertThat(section.getDistance()).isEqualTo(3)
        );
    }

    @DisplayName("하행역과 길이를 변경한다.")
    @Test
    void 하행역_길이_변경() {
        Section section = new Section(1L, 1L, 3L, 7);
        Section otherSection = new Section(1L, 2L, 3L, 4);

        section.changeDownStation(otherSection);

        assertAll(
                () -> assertThat(section.getDownStationId()).isEqualTo(2L),
                () -> assertThat(section.getDistance()).isEqualTo(3)
        );
    }
}
