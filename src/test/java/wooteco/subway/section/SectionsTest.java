package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.station.Station;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.DummyData.*;

@DisplayName("지하철 구간 일급 컬렉션 테스트")
class SectionsTest {

    @DisplayName("sorting 테스트")
    @Test
    void sorting() {
        // given
        List<Section> sectionList = Arrays.asList(
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7),
                new Section(1L, 이호선, 왕십리역, 잠실역, 10)
        );

        // when
        Sections sections = new Sections(sectionList);

        // then
        assertThat(sections.getSections()).usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(Arrays.asList(
                        new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                        new Section(2L, 이호선, 잠실역, 강남역, 5),
                        new Section(3L, 이호선, 강남역, 구의역, 7)
                ));
    }

    @DisplayName("구간 추가 - 상행종점")
    @Test
    void addSectionUpEndPoint() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when
        sections.addSection(new Section(3L, 이호선, 건대입구역, 왕십리역, 3));

        // then
        assertThat(sections.getSections()).hasSize(4);
    }

    @DisplayName("구간 추가 - 하행종점")
    @Test
    void addSectionDownEndPoint() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when
        sections.addSection(new Section(3L, 이호선, 구의역, 건대입구역, 3));

        // then
        assertThat(sections.getSections()).hasSize(4);
    }

    @DisplayName("구간 추가 - 중간(A - B가 있을 때 A - C 추가)")
    @Test
    void addSectionMiddlePointCase1() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when
        sections.addSection(new Section(3L, 이호선, 왕십리역, 한양대역, 3));

        // then
        assertThat(sections.getSections()).hasSize(4);
    }

    @DisplayName("구간 추가 - 중간(A - B가 있을 때 C - B 추가)")
    @Test
    void addSectionMiddlePointCase2() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when
        sections.addSection(new Section(3L, 이호선, 한양대역, 강남역, 3));

        // then
        assertThat(sections.getSections()).hasSize(4);
    }

    @DisplayName("구간 추가 - upStation, downStation 둘 다 포함되지 않는 구간 추가시 예외")
    @Test
    void addSectionNotInclude() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when & then
        assertThatThrownBy(() -> sections.addSection(new Section(3L, 이호선, 한양대역, 건대입구역, 5)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("구간 추가 - upStation, downStation 둘 다 포함되는 구간 추가시 예외")
    @Test
    void addSectionBothInclude() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when & then
        assertThatThrownBy(() -> sections.addSection(new Section(3L, 이호선, 왕십리역, 구의역, 5)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("지하철 역이 포함된 구간 삭제 - 상행종점")
    @Test
    void deleteUpEndPoint() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when
        sections.delete(왕십리역);

        // then
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("지하철 역이 포함된 구간 삭제 - 하행종점")
    @Test
    void deleteDownEndPoint() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when
        sections.delete(구의역);

        // then
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("지하철 역이 포함된 구간 삭제 - 중간")
    @Test
    void deleteMiddlePoint() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when
        sections.delete(잠실역);

        // then
        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("노선에 남아있는 구간 개수가 1이하인 경우 예외발생")
    @Test
    void checkRemainSectionSize() {
        // given
        Sections sections = new Sections(Collections.singletonList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10)
        ));

        // when & then
        assertThatThrownBy(() -> sections.delete(왕십리역))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("구간에 포함된 지하철 역 반환")
    @Test
    void getStations() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 이호선, 왕십리역, 잠실역, 10),
                new Section(2L, 이호선, 잠실역, 강남역, 5),
                new Section(3L, 이호선, 강남역, 구의역, 7)
        ));

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations)
                .containsAll(Arrays.asList(왕십리역, 잠실역, 강남역, 구의역));
    }
}