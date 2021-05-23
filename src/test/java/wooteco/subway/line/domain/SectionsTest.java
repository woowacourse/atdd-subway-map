package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SectionsTest {
    private Sections sections;

    private static final Line FIRST_LINE = new Line(1L, "1호선", "bg-red-600");

    private static final Station FIRST_STATION = new Station(1L, "첫번째역");
    private static final Station SECOND_STATION = new Station(3L, "두번째역");
    private static final Station THIRD_STATION = new Station(2L, "세번째역");
    private static final Station FOURTH_STATION = new Station(5L, "네번째역");
    private static final Station LAST_STATION = new Station(4L, "다섯번째역");

    private static final Section SECOND_SECTION = new Section(FIRST_LINE, SECOND_STATION, THIRD_STATION, 10);
    public static final Section THIRD_SECTION = new Section(FIRST_LINE, THIRD_STATION, FOURTH_STATION, 20);
    public static final Section FIRST_SECTION = new Section(FIRST_LINE, FIRST_STATION, SECOND_STATION, 5);
    public static final Section FOURTH_SECTION = new Section(FIRST_LINE, FOURTH_STATION, LAST_STATION, 7);

    @BeforeEach
    public void init() {
        sections = new Sections(Arrays.asList(
                SECOND_SECTION,
                THIRD_SECTION,
                FIRST_SECTION,
                FOURTH_SECTION
        ));
    }

    @Test
    void 구간_순서대로_지하철_역을_반환한다() {
        // when
        List<Station> orderedStations = sections.getOrderedStations();

        // then
        assertThat(orderedStations).containsExactly(
                FIRST_STATION,
                SECOND_STATION,
                THIRD_STATION,
                FOURTH_STATION,
                LAST_STATION);
    }

    @Test
    void 선택한_두_역_중_해당_노선에서_중복되는_역을_찾는다() {
        // given
        Long upStationId = SECOND_STATION.getId();
        Long downStationId = 6L;

        // when
        Station sameStationsOfSection = sections.findSameStationsOfSection(upStationId, downStationId);

        // then
        assertThat(sameStationsOfSection).isEqualTo(SECOND_STATION);
    }

    @Test
    void 선택한_두_역이_해당_노선에_모두_존재하면_예외처리한다() {
        // given
        Long upStationId = SECOND_STATION.getId();
        Long downStationId = THIRD_STATION.getId();

        // then
        assertThatThrownBy(() -> sections.findSameStationsOfSection(upStationId, downStationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간은 하나의 역만 중복될 수 있습니다.");
    }

    @Test
    void 선택한_두_역_중_해당_노선에_중복되는_역이_없다면_예외처리한다() {
        // given
        Long upStationId = 7L;
        Long downStationId = 8L;

        // then
        assertThatThrownBy(() -> sections.findSameStationsOfSection(upStationId, downStationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간은 하나의 역만 중복될 수 있습니다.");
    }

    @Test
    void 종점역만_남은_경우_예외처리한다() {
        // given
        Sections twoStationsOfSection = new Sections(
                Arrays.asList(
                        new Section(FIRST_LINE, FIRST_STATION, SECOND_STATION, 10)
                )
        );

        // then
        assertThatThrownBy(() -> twoStationsOfSection.validDeletableSection())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종점역만 남은 경우 삭제를 수행할 수 없습니다!");
    }

    @Test
    void 종점역_외에_지하철역이_존재하면_예외처리되지않는다() {
        // given
        Sections twoStationsOfSection = new Sections(
                Arrays.asList(
                        new Section(FIRST_LINE, FIRST_STATION, SECOND_STATION, 10),
                        new Section(FIRST_LINE, SECOND_STATION, THIRD_STATION, 7)
                )
        );

        // then
        assertDoesNotThrow(() -> twoStationsOfSection.validDeletableSection());
    }
}