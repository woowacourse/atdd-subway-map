package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 리스트 관리 기능")
class SectionsTest {

    @DisplayName("노선 전체에서 종점 역 아이디를 찾아낸다.")
    @Test
    void findLastStopId() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        List<Long> lastStopStationIds = sections.getLastStationIds();

        assertThat(lastStopStationIds.size()).isEqualTo(2);
        assertThat(lastStopStationIds).isEqualTo(List.of(1L, 4L));
    }

    @DisplayName("노선 전체에서 상행 역 아이디가 존재하는 구간을 찾는다.")
    @Test
    void getExistedUpStationSection() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Optional<Section> upStationIds = sections.getExistedUpStationSection(1L);

        assertThat(upStationIds).isPresent();
    }

    @DisplayName("노선 전체에서 상행 역 아이디가 존재하는 구간이 없으면 Empty Optional 을 반환한다.")
    @Test
    void getExistedUpStationSectionReturnEmptyOptional() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Optional<Section> upStationIds = sections.getExistedUpStationSection(4L);

        assertThat(upStationIds).isEmpty();
    }

    @DisplayName("노선 전체에서 하행 역 아이디가 존재하는 구간을 찾는다.")
    @Test
    void getExistedDownStationSection() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Optional<Section> downStationSection = sections.getExistedDownStationSection(2L);

        assertThat(downStationSection).isPresent();
    }

    @DisplayName("노선 전체에서 하행 역 아이디가 존재하는 구간이 없으면 Empty Optional 을 반환한다.")
    @Test
    void getExistedDownStationSectionReturnEmptyOptional() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Optional<Section> downStationSection = sections.getExistedDownStationSection(1L);

        assertThat(downStationSection).isEmpty();
    }

    @DisplayName("해당 역이 종점인지 확인한다.")
    @ParameterizedTest
    @CsvSource({"1,true", "2,false"})
    void isLastStation(Long stationId, boolean result) {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        boolean actual = sections.isLastStation(stationId);

        assertThat(actual).isEqualTo(result);
    }

}
