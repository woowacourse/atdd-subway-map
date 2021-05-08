package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("구간 기능 테스트")
class SectionTest {
    private final Station GANGNAM_STATION = new Station(1L, "강남역");
    private final Station YEOKSAM_STATION = new Station(2L, "역삼역");
    private final Station JAMSIL_STATION = new Station(3L, "잠실역");
    private final Station SADANG_STATION = new Station(4L, "사당역");
    private final long TEN_DISTANCE = 10;
    private final long FIVE_DISTANCE = 5;

    @DisplayName("역이 있는지 확인하는 기능")
    @Test
    void ifExistTest() {
        //given
        Section section = new Section(GANGNAM_STATION, YEOKSAM_STATION, TEN_DISTANCE);

        //when
        //then
        assertThat(section.isExist(GANGNAM_STATION)).isTrue();
    }

    @DisplayName("다음 구간과 합치는 기능")
    @Test
    void mergeWithDownSectionTest() {
        //given
        Section upSection = new Section(GANGNAM_STATION, YEOKSAM_STATION, TEN_DISTANCE);
        Section downSection = new Section(YEOKSAM_STATION, JAMSIL_STATION, FIVE_DISTANCE);

        //when
        Section mergedSection = upSection.mergeWithDownSection(downSection);
        //then
        assertThat(mergedSection.getUpStation()).isEqualTo(GANGNAM_STATION);
        assertThat(mergedSection.getUpStation()).isEqualTo(JAMSIL_STATION);
        assertThat(mergedSection.getDistance()).isEqualTo(TEN_DISTANCE + FIVE_DISTANCE);
    }

    @DisplayName("구간을 합칠 때 구간이 연결되어있지 않으면 예외")
    @Test
    void whenNotSequentialSection() {
        //given
        Section upSection = new Section(GANGNAM_STATION, YEOKSAM_STATION, TEN_DISTANCE);
        Section downSection = new Section(SADANG_STATION, JAMSIL_STATION, FIVE_DISTANCE);

        //when
        //then
        assertThatThrownBy(() -> upSection.mergeWithDownSection(downSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이어진 구간이 아닙니다.");
    }
}