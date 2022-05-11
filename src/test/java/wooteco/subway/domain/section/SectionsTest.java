package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.station.Station;

class SectionsTest {

    private static final Station STATION1 = new Station(1L, "강남역");
    private static final Station STATION2 = new Station(2L, "역삼역");
    private static final Station STATION3 = new Station(3L, "선릉역");
    private static final Station STATION4 = new Station(4L, "삼성역");

    private Sections sections;

    @BeforeEach
    void setUp() {
        this.sections = new Sections(List.of(
                new Section(1L, STATION2, STATION3, 5),
                new Section(2L, STATION1, STATION2, 5)
        ));
    }


    @DisplayName("구간을 정렬한다.")
    @Test
    void orderSections() {
        assertThat(sections.getStations()).containsExactly(STATION1, STATION2, STATION3);
    }

    @DisplayName("상행 종점을 추가한다.")
    @Test
    void appendUpStation() {
        sections.append(new Section(3L, STATION4, STATION1, 1));
        assertThat(sections.getStations()).containsExactly(STATION4, STATION1, STATION2, STATION3);
    }

    @DisplayName("하행 종점을 추가한다.")
    @Test
    void appendDownStation() {
        sections.append(new Section(3L, STATION3, STATION4, 1));
        assertThat(sections.getStations()).containsExactly(STATION1, STATION2, STATION3, STATION4);
    }

    @DisplayName("상행역이 동일한 구간 사이에 구간을 추가한다.")
    @Test
    void appendBetweenSectionsWithSameUpStation() {
        sections.append(new Section(3L, STATION1, STATION4, 1));
        assertThat(sections.getStations()).containsExactly(STATION1, STATION4, STATION2, STATION3);
    }

    @DisplayName("하행역이 동일한 구간 사이에 구간을 추가한다.")
    @Test
    void appendBetweenSectionsWithSameDownStation() {
        sections.append(new Section(3L, STATION4, STATION2, 1));
        assertThat(sections.getStations()).containsExactly(STATION1, STATION4, STATION2, STATION3);
    }

    @DisplayName("기존 구간의 길이보다 크거나 같은 새로운 구간을 추가한다.")
    @Test
    void appendLongestSection() {
        assertThatThrownBy(() -> sections.append(new Section(3L, STATION1, STATION4, 99)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기존 구간의 길이보다 크거나 같습니다.");
    }

    @DisplayName("상행역과 하행역 모두 포함하고 있으면 추가할 수 없다.")
    @ParameterizedTest
    @MethodSource("providerForAppendUpStationAndDownStationBOthExist")
    void appendUpStationAndDownStationBothExist(Section section) {
        assertThatThrownBy(() -> sections.append(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역이 이미 존재하는 구간입니다.");
    }

    private static Stream<Arguments> providerForAppendUpStationAndDownStationBOthExist() {
        return Stream.of(
                Arguments.of(new Section(3L, STATION1, STATION2, 1)),
                Arguments.of(new Section(3L, STATION2, STATION1, 1))
        );
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다.")
    @Test
    void appendUpStationAndDownStationNeitherExist() {
        Station nonRegisteredStation1 = new Station(10L, "광교역");
        Station nonRegisteredStation2 = new Station(11L, "광교중앙역");
        assertThatThrownBy(() -> sections.append(new Section(3L, nonRegisteredStation1, nonRegisteredStation2, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역이 존재하지 않는 구간입니다.");
    }
}