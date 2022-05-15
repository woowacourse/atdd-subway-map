package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class SectionsTest {

    private final Station STATION1 = new Station(1L, "역1");
    private final Station STATION2 = new Station(2L, "역2");
    private final Station STATION3 = new Station(3L, "역3");
    private final Station STATION4 = new Station(4L, "역4");
    private final Station STATION5 = new Station(5L, "역5");

    @DisplayName("생성자는 상행종점부터 하행종점까지 지하철역들 순서로 구간들을 정렬한 일급컬렉션을 생성")
    @Nested
    class InitTest {

        @Test
        void 인자로_들어오는_구간들의_순서와_무관하게_언제나_동일한_순서로_구간들을_정렬하여_인스턴스_생성() {
            Section section1 = new Section(STATION1, STATION3, 10);
            Section section2 = new Section(STATION3, STATION4, 10);
            Section section3 = new Section(STATION4, STATION5, 10);
            List<Section> sectionsInRandomOrder1 = List.of(section2, section3, section1);
            List<Section> sectionsInRandomOrder2 = List.of(section1, section3, section2);

            Sections actual = new Sections(sectionsInRandomOrder1);
            Sections expected = new Sections(sectionsInRandomOrder2);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 빈_구간_리스트로_생성하려는_경우_예외_발생() {
            assertThatThrownBy(() -> new Sections(List.of()))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        void 연결되지_않은_구간들의_리스트가_들어오면_예외발생() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION3, STATION4, 10);
            List<Section> disConnectedSections = List.of(section1, section2);

            assertThatThrownBy(() -> new Sections(disConnectedSections))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("isNewEndSection 메서드는 특정 구간을 해당 노선의 종점에 연결할 수 있는지의 여부를 반환")
    @Nested
    class IsNewEndSectionTest {

        @Test
        void 해당_구간을_상행_종점의_끝에_연결할_수_있는_경우_참_반환() {
            Section validNewUpperEndSection = new Section(STATION1, STATION2, 10);
            Section section1 = new Section(STATION2, STATION3, 10);
            Section section2 = new Section(STATION3, STATION4, 10);
            Sections sections = new Sections(List.of(section1, section2));

            boolean actual = sections.isNewEndSection(validNewUpperEndSection);

            assertThat(actual).isTrue();
        }

        @Test
        void 해당_구간을_하행_종점의_끝에_연결할_수_있는_경우_참_반환() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION2, STATION3, 10);
            Section validNewLowerEndSection = new Section(STATION3, STATION4, 10);
            Sections sections = new Sections(List.of(section1, section2));

            boolean actual = sections.isNewEndSection(validNewLowerEndSection);

            assertThat(actual).isTrue();
        }

        @Test
        void 노선의_긑에_등록할_수_없는_구간인_경우_거짓_반환() {
            Section section1 = new Section(STATION1, STATION3, 10);
            Section section2 = new Section(STATION3, STATION5, 10);
            Sections sections = new Sections(List.of(section1, section2));
            Section inBetweenSection = new Section(STATION2, STATION3, 5);

            boolean actual = sections.isNewEndSection(inBetweenSection);

            assertThat(actual).isFalse();
        }
    }

    @DisplayName("findUpperSectionOfStation 메서드는 특정 지하철역이 하행역으로 등록된 상행 노선을 조회한다")
    @Nested
    class FindUpperSectionOfStationTest {

        @Test
        void 특정_지하철이_등록된_상행_노선이_존재하면_조회하여_반환() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION2, STATION3, 10);
            Sections sections = new Sections(List.of(section1, section2));

            Section actual = sections.findUpperSectionOfStation(STATION2);

            assertThat(actual).isEqualTo(section1);
        }

        @Test
        void 데이터가_존재하지_않는_경우_예외발생() {
            Sections sections = new Sections(List.of(
                    new Section(STATION1, STATION2, 10)));

            assertThatThrownBy(() -> sections.findUpperSectionOfStation(STATION1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("findLowerSectionOfStation 메서드는 특정 지하철역이 상행역으로 등록된 하행 노선을 조회한다")
    @Nested
    class FindLowerSectionOfStationTest {

        @Test
        void 특정_지하철이_등록된_하행_노선이_존재하면_조회하여_반환() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION2, STATION3, 10);
            Sections sections = new Sections(List.of(section1, section2));

            Section actual = sections.findLowerSectionOfStation(STATION2);

            assertThat(actual).isEqualTo(section2);
        }

        @Test
        void 데이터가_존재하지_않는_경우_예외발생() {
            Sections sections = new Sections(List.of(
                    new Section(STATION1, STATION2, 10)));

            assertThatThrownBy(() -> sections.findLowerSectionOfStation(STATION2))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("isRegistered 메서드는 특정 지하철역이 노선에 등록되었는지를 체크하여 반환")
    @Nested
    class IsRegisteredTest {

        @Test
        void 등록된_지하철역인_경우_참_반환() {
            Sections sections = new Sections(List.of(
                    new Section(STATION1, STATION3, 10)));

            boolean actual = sections.isRegistered(STATION3);

            assertThat(actual).isTrue();
        }

        @Test
        void 등록되지_않은_지하철역인_경우_거짓_반환() {
            Sections sections = new Sections(List.of(
                    new Section(STATION1, STATION3, 10)));

            boolean actual = sections.checkMiddleStation(STATION2);

            assertThat(actual).isFalse();
        }
    }

    @DisplayName("checkMiddleStation 메서드는 구간들 사이에 존재하는 지하철역인지의 여부를 반환")
    @Nested
    class CheckMiddleStationTest {

        @Test
        void 등록되었으나_종점은_아닌_지하철역인_경우_참_반환() {
            Sections sections = new Sections(List.of(
                    new Section(STATION1, STATION3, 10),
                    new Section(STATION3, STATION4, 10)));

           boolean actual = sections.checkMiddleStation(STATION3);

            assertThat(actual).isTrue();
        }

        @Test
        void 종점으로_등록된_경우_거짓_반환() {
            Sections sections = new Sections(List.of(
                    new Section(STATION1, STATION3, 10)));

            boolean actual = sections.checkMiddleStation(STATION1);

            assertThat(actual).isFalse();
        }

        @Test
        void 등록되지_않은_경우_예외가_발생하지_않고_거짓_반환() {
            Sections sections = new Sections(List.of(
                    new Section(STATION1, STATION3, 10)));

            boolean actual = sections.checkMiddleStation(STATION2);

            assertThat(actual).isFalse();
        }
    }

    @Test
    void toSortedStations_메서드는_상행종점부터_하행종점까지_정렬된_지하철역들의_리스트를_반환() {
        Sections sections = new Sections(List.of(
                new Section(STATION1, STATION3, 10),
                new Section(STATION3, STATION4, 10),
                new Section(STATION4, STATION5, 10)));

        List<Station> actual = sections.toSortedStations();
        List<Station> expected = List.of(STATION1, STATION3, STATION4, STATION5);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toSortedList_메서드는_상행종점부터_하행종점까지_정렬된_구간들의_리스트를_반환() {
        Section section1 = new Section(STATION1, STATION3, 10);
        Section section2 = new Section(STATION3, STATION4, 10);
        Section section3 = new Section(STATION4, STATION5, 10);
        List<Section> sectionsInRandomOrder = List.of(section3, section1, section2);
        Sections sections = new Sections(sectionsInRandomOrder);

        List<Section> actual = sections.toSortedList();
        List<Section> expected = List.of(section1, section2, section3);

        assertThat(actual).isEqualTo(expected);
    }
}
