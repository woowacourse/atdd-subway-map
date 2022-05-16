package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    Station 강남 = new Station("강남");
    Station 선릉 = new Station("선릉");
    Station 삼성 = new Station("삼성");
    Station 잠실 = new Station("잠실");
    Line line = new Line("2호선", "green");
    Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(List.of(
                new Section(강남, 선릉, line, 10),
                new Section(선릉, 삼성, line, 10),
                new Section(삼성, 잠실, line, 10)
        ));
    }

    @DisplayName("지하철역을 반환한다.")
    @Test
    void getStations() {
        assertThat(sections.getStations()).containsExactly(강남, 선릉, 삼성, 잠실);
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void add() {
        Station 종합운동장 = new Station("종합운동장");
        Station 구의 = new Station("구의");
        Section section1 = new Section(강남, 종합운동장, line, 5);
        Section section2 = new Section(구의, 잠실, line, 5);

        Sections actual = sections.update(section1).update(section2);

        assertThat(actual.getStations()).contains(강남, 종합운동장, 선릉, 삼성, 구의, 잠실);
    }

    @Nested
    @DisplayName("구간 등록을 할 수 없는 경우")
    class SectionsValidateAddable {

        private Sections sections = new Sections(List.of(
                new Section(강남, 선릉, line, 10),
                new Section(선릉, 삼성, line, 10),
                new Section(삼성, 잠실, line, 10)
        ));

        @DisplayName("상행선과 하행선 둘 다 기존 지하철역에 포함되지 않는다.")
        @Test
        void notContainsUpStationAndDownStation() {
            Station 평택 = new Station("평택");
            Station 천안 = new Station("천안");
            Section section = new Section(평택, 천안, line, 1);
            assertThatThrownBy(() -> sections.update(section))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상행선과 하행선이 노선에 없습니다.");
        }

        @DisplayName("상행성과 하행선 둘 다 기존 지하철역에 포함한다.")
        @Test
        void containsUpStationAndDownStation() {
            Section section = new Section(강남, 선릉, line, 1);
            assertThatThrownBy(() -> sections.update(section))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상행선과 하행선 둘 다 노선에 이미 존재합니다.");
        }

        @DisplayName("상행선끼리 같거나 하행선끼리 같을 때 기존 구간의 거리보다 추가할 거리가 크다.")
        @Test
        void containsSamePositionStationAndLongerDistance() {
            Station 신도림 = new Station("신도림");
            Section section = new Section(강남, 신도림, line, 11);
            assertThatThrownBy(() -> sections.update(section))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 존재하는 구간의 거리보다 거리가 길거나 같습니다.");
        }
    }

    @DisplayName("구간 중 특정 지하철역을 삭제한다.")
    @Test
    void deleteByStation() {
        assertThat(sections.deleteByStation(선릉).value()).hasSize(2);
    }

    @DisplayName("구간이 하나인 노선은 구간을 제거할 수 없다.")
    @Test
    void exceptionWhenLineHasOneSection() {
        Sections sections = new Sections(List.of(new Section(강남, 선릉, line, 10)));
        assertThatThrownBy(() -> sections.deleteByStation(new Station(2L, "제거될 역")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("역을 없애려는 노선은 최소 2개 이상의 구간을 가져야 합니다.");
    }
}
