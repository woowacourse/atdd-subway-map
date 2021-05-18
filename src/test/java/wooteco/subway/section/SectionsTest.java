package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.exception.station.NotFoundStationException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;

class SectionsTest {

    private final Line TWO_LINE = new Line(2L, "2호선", "초록색");
    private final Station GURO_STATION = new Station(1L, "구로역");
    private final Station SINDAEBANG_STATION = new Station(2L, "신대방역");
    private final Station SINLIM_STATION = new Station(3L, "신림역");
    private final Station BONGCHEON_STATION = new Station(4L, "봉천역");
    private final Station DAELIM_STATION = new Station(5L, "대림역");
    private final int STANDARD_DISTANCE = 10;
    private final Sections SECTIONS = new Sections(Arrays.asList(
        new Section(TWO_LINE, GURO_STATION, SINDAEBANG_STATION, STANDARD_DISTANCE),
        new Section(TWO_LINE, SINDAEBANG_STATION, SINLIM_STATION, STANDARD_DISTANCE),
        new Section(TWO_LINE, SINLIM_STATION, BONGCHEON_STATION, STANDARD_DISTANCE),
        new Section(TWO_LINE, DAELIM_STATION, GURO_STATION, STANDARD_DISTANCE)
    ));

    @Test
    @DisplayName("역 정렬 메서드")
    void sortStation() {
        //given

        //when
        Deque<Long> sortedId = SECTIONS.sortedStationIds();

        //then
        assertThat(sortedId.getFirst()).isEqualTo(5);
        assertThat(sortedId.getLast()).isEqualTo(4);
    }

    @Test
    @DisplayName("역 끝 체크 메서드")
    void checkEndStation() {
        //given
        long daelimID = 5L;
        long guroID = 1L;

        //when
        boolean guroCase = SECTIONS.isBothEndStation(guroID);
        boolean daelimCase = SECTIONS.isBothEndStation(daelimID);

        //then
        assertThat(guroCase).isFalse();
        assertThat(daelimCase).isTrue();
    }

    @Test
    @DisplayName("끝 구간 테스트")
    void checkEndSection() {
        //given
        long daelimID = 5L;
        long guroID = 1L;
        Station sindomrimStation = new Station(6L, "신도림역");

        //when
        boolean daeLimGuroCase = SECTIONS
            .isBothEndSection(new Section(TWO_LINE, DAELIM_STATION, GURO_STATION, 10));
        boolean sindorimDaelimCase = SECTIONS
            .isBothEndSection(new Section(TWO_LINE, sindomrimStation, DAELIM_STATION, 10));

        //then
        assertThat(sindorimDaelimCase).isTrue();
        assertThat(daeLimGuroCase).isFalse();
    }

    @Test
    @DisplayName("삽입 구간 불가능 테스트")
    void availableInsertSection() {
        //given
        long daelimID = 5L;
        long guroID = 1L;
        Section section = new Section(TWO_LINE, DAELIM_STATION, BONGCHEON_STATION, 10);

        //when

        //then
        assertThatThrownBy(() -> SECTIONS.insertAvailable(section))
            .isInstanceOf(InvalidSectionOnLineException.class);

    }

    @Test
    @DisplayName("역 존재 유무 테스트")
    void checkStationTest() {
        //given
        Station station = new Station(9L, "잠실역");

        //when

        //then
        assertThatThrownBy(() -> SECTIONS.validateExistStation(station))
            .isInstanceOf(NotFoundStationException.class);

    }

    @Test
    @DisplayName("역 삭제 실패 테스트")
    void failDeleteTest() {
        //given
        Station startStation = new Station(1L, "잠실새내역");
        Station endStation = new Station(2L, "잠실역");
        Section section = new Section(1L, TWO_LINE, startStation, endStation, 10);
        List<Section> rawSections = new ArrayList<>();
        Sections sections = new Sections(rawSections);
        //when

        //then
        assertThatThrownBy(() -> sections.validateDeletableCount())
            .isInstanceOf(IllegalStateException.class);

    }

    @Test
    @DisplayName("겹치는 구간 있는지 찾는 테스트")
    void checkOverlappedSection() {
        //given
        Station startStation = new Station(11L, "시청역");
        Station endStation = new Station(12L, "신당역");
        Station sindorimStation = new Station(10L, "신도림역");
        Section section = new Section(TWO_LINE, startStation, endStation, 10);
        Section targetSection = new Section(TWO_LINE, sindorimStation, DAELIM_STATION, 10);

        //when
        Section responseSection = SECTIONS.findByOverlappedStation(targetSection);

        //then
        assertThatThrownBy(() -> SECTIONS.findByOverlappedStation(section))
            .isInstanceOf(InvalidSectionOnLineException.class);
        assertThat(responseSection.getUpStation().getName()).isEqualTo(DAELIM_STATION.getName());
    }

    @Test
    @DisplayName("역사이 구간 추가시 해당 역 찾는 테스트")
    void checkIntervalSection() {
        //given
        Station startStation = new Station(11L, "까치산역");
        Section section = new Section(TWO_LINE, GURO_STATION, startStation, 5);

        //when
        Section responseSection = SECTIONS.sectionForInterval(section);

        //then
        assertThat(responseSection.getUpStation().getName()).isEqualTo(GURO_STATION.getName());
    }
}