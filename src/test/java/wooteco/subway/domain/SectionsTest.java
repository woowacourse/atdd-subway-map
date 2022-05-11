package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.fixture.StationFixture.AStation;
import static wooteco.subway.fixture.StationFixture.BStation;
import static wooteco.subway.fixture.StationFixture.CStation;
import static wooteco.subway.fixture.StationFixture.DStation;
import static wooteco.subway.fixture.StationFixture.EStation;
import static wooteco.subway.fixture.StationFixture.YStation;
import static wooteco.subway.fixture.StationFixture.ZStation;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wooteco.subway.fixture.SectionsFixture;

class SectionsTest {

    @DisplayName("주어진 sections으로 정렬된 지하철역을 가져온다.")
    @Test
    void getOrderedStations() {
        Sections sections = SectionsFixture.createSections();

        List<Station> stations = sections.getOrderedStations();
        List<Station> expectedStations = new LinkedList<>(List.of(AStation, BStation, CStation, DStation, EStation));

        assertThat(stations).isEqualTo(expectedStations);
    }

    @DisplayName("상행 종점에 추가적으로 구간을 추가할 수 있다.")
    @Test
    void CheckAddSectionInLastUpStation() {
        Sections sections = SectionsFixture.createSections();

        assertDoesNotThrow(() -> sections.checkAddSection(ZStation, AStation, 11));
    }

    @DisplayName("하행 종점에 추가적으로 구간을 추가할 수 있다.")
    @Test
    void CheckAddSectionInLastDownStation() {
        Sections sections = SectionsFixture.createSections();

        assertDoesNotThrow(() -> sections.checkAddSection(EStation, ZStation, 11));
    }

    @DisplayName("등록된 구간 중간에 새로운 구간을 추가할 수 있다.")
    @ParameterizedTest
    @MethodSource("createStations")
    void CheckAddSectionInMiddleStation() {
        Sections sections = SectionsFixture.createSections();

        assertDoesNotThrow(() -> sections.checkAddSection(BStation, ZStation, 9));
    }

    static Stream<Arguments> createStations() {
        return Stream.of(
                Arguments.of(CStation, ZStation),
                Arguments.of(ZStation, CStation)
        );
    }


    @DisplayName("지하철역 2개를 이용해서 구간을 만들려고 할 때 두 지하철역 모두 구간에 등록이 안되어 있다면 예외가 발생한다.")
    @Test
    void CheckAddSectionNotExistStation() {
        Sections sections = SectionsFixture.createSections();

        assertThatThrownBy(() -> sections.checkAddSection(YStation, ZStation, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입력한 상행과 하행 중 노선에 등록된 지하철이 없습니다.");
    }

    @DisplayName("이미 구간으로 등록되어 있는 지하철역 2개를 이용해서 구간을 만들려고 하면 예외가 발생한다.")
    @Test
    void CheckAddSectionAlreadyExistSection() {
        Sections sections = SectionsFixture.createSections();

        assertThatThrownBy(() -> sections.checkAddSection(AStation, CStation, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입력한 상행 하행 구간이 이미 연결되어 있는 구간입니다.");
    }

    @DisplayName("구간으로 등록할 수 있는 지하철역 2개이지만, 구간 사이의 거리가 기존의 구간과 맞지 않아서 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("createStationsWithWrongDistance")
    void CheckAddSectionToLongDistance(Station upStation, Station downStation, int distance) {
        Sections sections = SectionsFixture.createSections();

        assertThatThrownBy(() -> sections.checkAddSection(upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간 사이의 거리가 너무 멉니다.");
    }

    static Stream<Arguments> createStationsWithWrongDistance() {
        return Stream.of(
                Arguments.of(AStation, ZStation, 11),
                Arguments.of(BStation, ZStation, 11),
                Arguments.of(ZStation, EStation, 11),
                Arguments.of(ZStation, BStation, 11)
        );
    }
}
