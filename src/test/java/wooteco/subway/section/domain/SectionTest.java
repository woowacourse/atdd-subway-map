package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.section.exception.SectionHasSameStationsException;
import wooteco.subway.section.exception.SectionNotSequentialException;
import wooteco.subway.station.fixture.StationFixture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("구간 기능 테스트")
class SectionTest {
    private final long TEN_DISTANCE = 10;
    private final long FIVE_DISTANCE = 5;

    @DisplayName("역이 있는지 확인하는 기능")
    @Test
    void ifExistTest() {
        //given
        Section section = new Section(StationFixture.GANGNAM_STATION, StationFixture.YEOKSAM_STATION, TEN_DISTANCE);

        //when
        //then
        assertThat(section.isExist(StationFixture.GANGNAM_STATION)).isTrue();
    }

    @DisplayName("다음 구간과 합치는 기능")
    @Test
    void mergeWithDownSectionTest() {
        //given
        Section upSection = new Section(StationFixture.GANGNAM_STATION, StationFixture.YEOKSAM_STATION, TEN_DISTANCE);
        Section downSection = new Section(StationFixture.YEOKSAM_STATION, StationFixture.JAMSIL_STATION, FIVE_DISTANCE);

        //when
        Section mergedSection = downSection.mergeWithSequentialSection(upSection);

        //then
        assertThat(mergedSection.getUpStation()).isEqualTo(StationFixture.GANGNAM_STATION);
        assertThat(mergedSection.getDownStation()).isEqualTo(StationFixture.JAMSIL_STATION);
        assertThat(mergedSection.getDistance()).isEqualTo(TEN_DISTANCE + FIVE_DISTANCE);
    }

    @DisplayName("상행역을 업데이트 하는 기능")
    @Test
    void updateUpStationTest() {
        //given
        Section section = new Section(StationFixture.GANGNAM_STATION, StationFixture.YEOKSAM_STATION, TEN_DISTANCE);

        //when
        Section updatedSection = section.updateUpStation(StationFixture.JAMSIL_STATION, FIVE_DISTANCE);
        //then
        assertThat(updatedSection.getUpStation()).isEqualTo(StationFixture.JAMSIL_STATION);
        assertThat(updatedSection.getDownStation()).isEqualTo(StationFixture.YEOKSAM_STATION);
        assertThat(updatedSection.getDistance()).isEqualTo(section.getDistance() - FIVE_DISTANCE);
    }

    @DisplayName("하행역을 업데이트 하는 기능")
    @Test
    void updateDownStationTest() {
        //given
        Section section = new Section(StationFixture.GANGNAM_STATION, StationFixture.YEOKSAM_STATION, TEN_DISTANCE);

        //when
        Section updatedSection = section.updateDownStation(StationFixture.JAMSIL_STATION, FIVE_DISTANCE);

        //then
        assertThat(updatedSection.getUpStation()).isEqualTo(StationFixture.GANGNAM_STATION);
        assertThat(updatedSection.getDownStation()).isEqualTo(StationFixture.JAMSIL_STATION);
        assertThat(updatedSection.getDistance()).isEqualTo(section.getDistance() - FIVE_DISTANCE);
    }

    @DisplayName("구간을 합칠 때 구간이 연결되어있지 않으면 예외")
    @Test
    void whenNotSequentialSection() {
        //given
        Section upSection = new Section(StationFixture.GANGNAM_STATION, StationFixture.YEOKSAM_STATION, TEN_DISTANCE);
        Section downSection = new Section(StationFixture.SADANG_STATION, StationFixture.JAMSIL_STATION, FIVE_DISTANCE);

        //when
        //then
        assertThatThrownBy(() -> upSection.mergeWithSequentialSection(downSection))
                .isInstanceOf(SectionNotSequentialException.class)
                .hasMessageContaining("이어진 구간이 아닙니다.");
    }

    @DisplayName("노선에 상행역과 하행역이 같으면 예외")
    @Test
    void whenSameUpStationAndDownStation() {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Section(StationFixture.GANGNAM_STATION, StationFixture.GANGNAM_STATION, TEN_DISTANCE))
                .isInstanceOf(SectionHasSameStationsException.class);
    }
}