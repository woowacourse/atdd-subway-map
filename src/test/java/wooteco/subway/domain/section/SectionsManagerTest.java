package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

@SuppressWarnings("NonAsciiCharacters")
class SectionsManagerTest {

    private final Station STATION1 = new Station(1L, "역1");
    private final Station STATION2 = new Station(2L, "역2");
    private final Station STATION3 = new Station(3L, "역3");
    private final Station STATION4 = new Station(4L, "역4");
    private final Station STATION5 = new Station(5L, "역5");

    @DisplayName("save 메서드는 특정 구간을 추가한 후 기존 구간들을 재조정한 일급컬렉션을 반환")
    @Nested
    class SaveTest {

        @Test
        void 기존_상행종점에_새로운_상행_종점을_연결하는_경우_재조정_작업_없이_추가_후_반환() {
            Section existingSection = new Section(STATION2, STATION3, 10);
            Section newUpperSection = new Section(STATION1, STATION2, 10);
            SectionsManager sectionsManager = new SectionsManager(List.of(existingSection));

            Sections actual = sectionsManager.save(newUpperSection);
            Sections expected = new Sections(List.of(newUpperSection, existingSection));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 기존_하행종점에_새로운_하행_종점을_연결하는_경우_재조정_작업_없이_추가_후_반환() {
            Section existingSection = new Section(STATION1, STATION2, 10);
            Section newLowerSection = new Section(STATION2, STATION3, 10);
            SectionsManager sectionsManager = new SectionsManager(List.of(existingSection));

            Sections actual = sectionsManager.save(newLowerSection);
            Sections expected = new Sections(List.of(existingSection, newLowerSection));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 기존_구간_사이에_구간_추가시_덮어써지는_구간의_거리_재조정_후_반환() {
            Section existingSection = new Section(STATION1, STATION3, 10);
            SectionsManager sectionsManager = new SectionsManager(List.of(existingSection));
            Section inBetweenSection = new Section(STATION1, STATION2, 3);

            Sections actual = sectionsManager.save(inBetweenSection);
            Sections expected = new Sections(List.of(
                    new Section(STATION1, STATION2, 3),
                    new Section(STATION2, STATION3, 7)));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 구간들에_등록되지_않은_지하철역들로_구성된_구간_추가_시도시_예외발생() {
            SectionsManager sectionsManager = new SectionsManager(List.of(new Section(STATION1, STATION5, 10)));
            Section noneRegisteredStationsSection = new Section(STATION2, STATION3, 5);

            assertThatThrownBy(() -> sectionsManager.save(noneRegisteredStationsSection))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 이미_등록된_지하철역들로만_구성된_구간_추가_시도시_예외발생() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION2, STATION3, 10);
            SectionsManager sectionsManager = new SectionsManager(List.of(section1, section2));
            Section bothRegisteredStationsSection = new Section(STATION1, STATION3, 15);

            assertThatThrownBy(() -> sectionsManager.save(bothRegisteredStationsSection))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("거리 유효성 검증")
        @Nested
        class DistanceValidationTest {

            @Test
            void 상행종점에_새로운_상행_종점을_연결하는_경우_거리는_1이상이면_무조건_허용() {
                Section existingSection = new Section(STATION2, STATION3, 10);
                Section newUpperSection = new Section(STATION1, STATION2, 9999999);
                SectionsManager sectionsManager = new SectionsManager(List.of(existingSection));

                assertThatNoException()
                        .isThrownBy(() -> sectionsManager.save(newUpperSection));
            }

            @Test
            void 하행종점에_새로운_하행_종점을_연결하는_경우_거리는_1이상이면_무조건_허용() {
                Section existingSection = new Section(STATION1, STATION2, 10);
                Section newLowerSection = new Section(STATION2, STATION3, 9999999);
                SectionsManager sectionsManager = new SectionsManager(List.of(existingSection));

                assertThatNoException()
                        .isThrownBy(() -> sectionsManager.save(newLowerSection));
            }

            @Test
            void 기존_구간_위에_다른_구간_등록시_기존_구간과_거리가_동일하면_예외() {
                Section existingSection = new Section(STATION1, STATION3, 10);
                Section newCoveringSection = new Section(STATION1, STATION2, 10);
                SectionsManager sectionsManager = new SectionsManager(List.of(existingSection));

                assertThatThrownBy(() -> sectionsManager.save(newCoveringSection))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void 기존_구간_위에_다른_구간_등록시_기존_구간보다_거리가_크면_예외() {
                Section existingSection = new Section(STATION1, STATION3, 10);
                Section newCoveringSection = new Section(STATION1, STATION2, 11);
                SectionsManager sectionsManager = new SectionsManager(List.of(existingSection));

                assertThatThrownBy(() -> sectionsManager.save(newCoveringSection))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @DisplayName("delete 메서드는 특정 지하철역을 제거한 후 구간들을 재조정한 일급컬렉션을 반환")
    @Nested
    class DeleteTest {

        @Test
        void 상행_종점_제거시_재조정_작업_없이_연결된_구간만_제거() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION2, STATION3, 10);
            SectionsManager sectionsManager = new SectionsManager(List.of(section1, section2));

            Sections actual = sectionsManager.delete(STATION1);
            Sections expected = new Sections(List.of(section2));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 하행_종점_제거시_재조정_작업_없이_연결된_구간만_제거() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION2, STATION3, 10);
            SectionsManager sectionsManager = new SectionsManager(List.of(section1, section2));

            Sections actual = sectionsManager.delete(STATION3);
            Sections expected = new Sections(List.of(section1));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 구간들_중앙의_지하철역_제거시_인접한_두_구간을_합친_후_반환() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION2, STATION3, 5);
            SectionsManager sectionsManager = new SectionsManager(List.of(section1, section2));

            Sections actual = sectionsManager.delete(STATION2);
            Sections expected = new Sections(List.of(
                    new Section(STATION1, STATION3, 15)));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 구간들에_등록되지_않은_지하철역_제거_시도시_예외발생() {
            Section section1 = new Section(STATION1, STATION2, 10);
            Section section2 = new Section(STATION2, STATION3, 5);
            SectionsManager sectionsManager = new SectionsManager(List.of(section1, section2));

            assertThatThrownBy(() -> sectionsManager.delete(STATION5))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 마지막_구간_제거_시도시_예외발생() {
            Section section1 = new Section(STATION1, STATION2, 10);
            SectionsManager sectionsManager = new SectionsManager(List.of(section1));

            assertThatThrownBy(() -> sectionsManager.delete(STATION1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("extractNewSections 메서드는 수정된 구간 정보를 받아, 새로 생겨난 구간들의 리스트를 반환")
    @Nested
    class ExtractNewSectionsTest {

        private final Section SECTION1 = new Section(STATION1, STATION2, 10);
        private final Section SECTION2 = new Section(STATION2, STATION4, 10);
        private final Section SECTION3 = new Section(STATION4, STATION5, 10);

        @Test
        void 현재_존재하는_구간들_중_인자로_들어온_구간들에는_않는_구간들의_리스트를_반환() {
            SectionsManager sectionsManager = new SectionsManager(List.of(SECTION1, SECTION2));
            Section newSection1 = new Section(STATION2, STATION3, 5);
            Section newSection2 = new Section(STATION3, STATION4, 5);
            Sections updatedSections = new Sections(List.of(SECTION1, newSection1, newSection2));

            List<Section> actual = sectionsManager.extractNewSections(updatedSections);
            List<Section> expected = List.of(newSection1, newSection2);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 새로_생겨난_구간들이_없는_경우_빈_리스트_반환() {
            SectionsManager sectionsManager = new SectionsManager(List.of(SECTION1, SECTION2, SECTION3));
            Sections updatedSections = new Sections(List.of(SECTION1, SECTION2));

            List<Section> actual = sectionsManager.extractNewSections(updatedSections);
            List<Section> expected = List.of();

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 현재와_동일한_구간들이_들어오더라도_예외는_발생하지_않으며_빈_리스트_반환() {
            SectionsManager sectionsManager = new SectionsManager(List.of(SECTION1, SECTION2, SECTION3));
            Sections updatedSections = new Sections(List.of(SECTION1, SECTION2, SECTION3));

            List<Section> actual = sectionsManager.extractNewSections(updatedSections);
            List<Section> expected = List.of();

            assertThat(actual).isEqualTo(expected);
        }
    }

    @DisplayName("extractDeletedSections 메서드는 수정된 구간 정보를 받아, 제거된 구간들의 리스트를 반환")
    @Nested
    class ExtractDeletedSectionsTest {

        private final Section SECTION1 = new Section(STATION1, STATION2, 10);
        private final Section SECTION2 = new Section(STATION2, STATION3, 10);
        private final Section SECTION3 = new Section(STATION3, STATION4, 10);

        @Test
        void 인자로_들어온_구간들에는_존재하지_않는_구간들의_리스트를_반환() {
            SectionsManager sectionsManager = new SectionsManager(List.of(SECTION1, SECTION2, SECTION3));
            Sections updatedSections = new Sections(List.of(
                    new Section(STATION1, STATION3, 20), SECTION3));

            List<Section> actual = sectionsManager.extractDeletedSections(updatedSections);
            List<Section> expected = List.of(SECTION1, SECTION2);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 이전에_존재하던_구간들이_그대로인_경우_빈_리스트_반환() {
            SectionsManager sectionsManager = new SectionsManager(List.of(SECTION1, SECTION2));
            Sections updatedSections = new Sections(List.of(SECTION1, SECTION2, SECTION3));

            List<Section> actual = sectionsManager.extractDeletedSections(updatedSections);
            List<Section> expected = List.of();

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 현재와_동일한_구간들이_들어오더라도_예외는_발생하지_않으며_빈_리스트_반환() {
            SectionsManager sectionsManager = new SectionsManager(List.of(SECTION1, SECTION2, SECTION3));
            Sections updatedSections = new Sections(List.of(SECTION1, SECTION2, SECTION3));

            List<Section> actual = sectionsManager.extractDeletedSections(updatedSections);
            List<Section> expected = List.of();

            assertThat(actual).isEqualTo(expected);
        }
    }
}
