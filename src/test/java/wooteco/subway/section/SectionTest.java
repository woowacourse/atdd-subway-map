package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.DuplicateStationException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.domain.Station;

class SectionTest {
    private final Station UP_STATION = new Station( 1L, "동묘앞역");
    private final Station DOWN_STATION = new Station(2L, "신설동역");
    private final Line LINE = new Line(1L, "1호선", "파란");
    private final int DISTANCE = 10;
    private final Section SECTION = new Section(LINE, UP_STATION, DOWN_STATION, DISTANCE);


    @Test
    @DisplayName("중복된 지하철역이 입력되었을 시 예외처리")
    public void validateDuplicatedStation() {
        // given
        Station upStation = new Station( 1L, "신설동역");
        Station downStation = new Station(1L, "신설동역");
        Line line = new Line("1호선", "파란");
        int distance = 1;

        // when

        // then
        assertThatThrownBy(() -> new Section(line, upStation, downStation, distance))
            .isInstanceOf(DuplicateStationException.class);
    }

    @Test
    @DisplayName("상행선 일치 테스트")
    public void isSameUpStation() {
        //given
        Station upStation = new Station( 1L,"동묘앞역");
        Station downStation = new Station(3L, "동대문역");
        Line line = new Line("1호선", "파란");
        int distance = 1;

        //when
        Section newSection = new Section(line, upStation, downStation, distance);

        //then
        assertThat(newSection.isSameUpStation(SECTION)).isTrue();

    }

    @Test
    @DisplayName("하행선 일치 테스트")
    public void isSameDownStation() {
        //given
        Station upStation = new Station( 3L,"동대문역");
        Station downStation = new Station(2L,"신설동역");
        Line line = new Line(1L, "1호선", "파란");
        int distance = 1;

        //when
        Section newSection = new Section(line, upStation, downStation, distance);

        //then
        assertThat(newSection.isSameDownStation(SECTION)).isTrue();
    }

    @Test
    @DisplayName("상행선 포함 테스트")
    public void isIncludeUpStation() {
        //given
        Station upStation = new Station( 2L,"신설동역");
        Station downStation = new Station(4L,"구로디지털단지역");
        Line line = new Line(1L, "1호선", "파란");
        int distance = 1;

        //when
        Section newSection = new Section(line, upStation, downStation, distance);

        //then
        assertThat(SECTION.isIncludeUpStation(newSection)).isTrue();
    }

    @Test
    @DisplayName("하행선 포함 테스트")
    public void isIncludeDownStation() {
        //given
        Station upStation = new Station( 4L, "구로디지털단지역");
        Station downStation = new Station(2L, "신설동역");
        Line line = new Line(1L, "1호선", "파란");
        int distance = 1;

        //when
        Section newSection = new Section(line, upStation, downStation, distance);

        //then
        assertThat(SECTION.isIncludeDownStation(newSection)).isTrue();
    }

    @Test
    @DisplayName("section 추가를 위한 section 분할 테스트")
    public void sectionForInsert() {
        //given
        Station downStation = new Station(3L, "봉천역");
        Line line = new Line(1L, "1호선", "파란");
        int distance = 5;
        Section newSection = new Section(line, UP_STATION, downStation, distance);

        //when
        Section dividedSectionForInsert = SECTION.dividedSectionForSave(newSection);

        //then
        assertThat(dividedSectionForInsert.getDistance().value()).isEqualTo(5);
        assertThat(dividedSectionForInsert.getDownStation().getName()).isEqualTo(DOWN_STATION.getName());
        assertThat(dividedSectionForInsert.getUpStation().getName()).isEqualTo(downStation.getName());
    }

    @Test
    @DisplayName("section 삭제를 위한 통합 section 제작 테스트")
    public void sectionForDelete() {
        //given
        Station downStation = new Station( 3L, "구로디지털단지역");
        Station upStation = new Station(2L, "신설동역");
        Line line = new Line(1L, "1호선", "파란");
        int distance = 10;
        Section newSection = new Section(line, upStation, downStation, distance);

        //when
        Section combinedSectionForDelete = SECTION.assembledSectionForDelete(newSection);

        //then
        assertThat(combinedSectionForDelete.getDistance().value()).isEqualTo(20);
        assertThat(combinedSectionForDelete.getUpStation().getName()).isEqualTo(UP_STATION.getName());
        assertThat(combinedSectionForDelete.getDownStation().getName()).isEqualTo(downStation.getName());
    }
}