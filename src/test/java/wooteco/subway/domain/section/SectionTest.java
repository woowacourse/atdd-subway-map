package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.testutils.Fixture.SECTION_LINE_1_STATION_1_2_10;
import static wooteco.subway.testutils.Fixture.SECTION_LINE_1_STATION_1_3_22;
import static wooteco.subway.testutils.Fixture.SECTION_LINE_1_STATION_2_3_12;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("구간을 생성한다.")
    @Test
    void create_success() {
        //given & when
        final Long lineId = 1L;
        final int distance = 1;

        //then
        assertDoesNotThrow(() -> new Section(1L, lineId, 1L, 2L, distance));
    }

    @DisplayName("구간 생성시, 상행 종점과 하행 종점이 같으면 예외를 발생한다.")
    @Test
    void create_fail_same_station() {
        //given & when
        final Long lineId = 1L;
        final int distance = 1;

        //then
        assertThatThrownBy(() -> new Section(1L, lineId, 1L, 1L, distance))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 상행 종점과 하행 종점이 같을 수 없습니다.");
    }

    @DisplayName("구간 생성시, 부적절한 거리가 입력되면 예외를 발생한다.")
    @Test
    void create_fail_invalid_distance() {
        //given & when
        final Long lineId = 1L;
        final int distance = 0;

        //then
        assertThatThrownBy(() -> new Section(1L, lineId, 1L, 2L, distance))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 부적절한 거리가 입력되었습니다. 0보다 큰 거리를 입력해주세요.");
    }

    @DisplayName("기존 구간에 대해 중간역을 포함한 추가 구간(up to middle)을 통해 새로운 구간(middle to down)을 생성한다.")
    @Test
    void createMiddleToDownSection() {
        //given & when
        final Section middleToDownSection = SECTION_LINE_1_STATION_1_2_10.createMiddleToDownSection(
            SECTION_LINE_1_STATION_1_3_22);

        //then
        assertAll(
            () -> assertThat(middleToDownSection.getUpStationId()).isEqualTo(
                SECTION_LINE_1_STATION_1_2_10.getDownStationId()),
            () -> assertThat(middleToDownSection.getDownStationId()).isEqualTo(
                SECTION_LINE_1_STATION_1_3_22.getDownStationId())
        );
    }

    @DisplayName("기존 구간에 대해 중간역을 포함한 추가 구간(middle to down)을 통해 새로운 구간(up to middle)을 생성한다.")
    @Test
    void createUpToMiddleSection() {
        //given & when
        final Section upToMiddleSection = SECTION_LINE_1_STATION_2_3_12.createUpToMiddleSection(
            SECTION_LINE_1_STATION_1_3_22);

        //then
        assertAll(
            () -> assertThat(upToMiddleSection.getUpStationId()).isEqualTo(
                SECTION_LINE_1_STATION_1_3_22.getUpStationId()),
            () -> assertThat(upToMiddleSection.getDownStationId()).isEqualTo(
                SECTION_LINE_1_STATION_2_3_12.getUpStationId())
        );
    }

    @DisplayName("중간역을 포함하던 두 구간을 이어 새로운 구간을 생성한다.")
    @Test
    void createUpToDownSection() {
        //given & when
        final Section newSection = SECTION_LINE_1_STATION_1_2_10.createUpToDownSection(
            SECTION_LINE_1_STATION_2_3_12);

        //then
        assertAll(
            () -> assertThat(newSection.getUpStationId()).isEqualTo(SECTION_LINE_1_STATION_1_2_10.getUpStationId()),
            () -> assertThat(newSection.getDownStationId()).isEqualTo(SECTION_LINE_1_STATION_2_3_12.getDownStationId())
        );
    }
}
