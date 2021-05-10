package wooteco.subway.domain.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SectionsTest {

    private Section firstSection;
    private Section secondSection;
    private Section thirdSection;
    private Section fourthSection;

    @BeforeEach
    void setUp() {
        Station firstStation = new Station(1L, "천호역");
        Station secondStation = new Station(2L, "강남역");
        Station thirdStation = new Station(3L, "회기역");
        Station fourthStation = new Station(4L, "의정부역");
        Station fifthStation = new Station(5L, "태릉역");
        firstSection = new Section(firstStation, secondStation, 10, 1L);
        secondSection = new Section(secondStation, thirdStation, 10, 1L);
        thirdSection = new Section(thirdStation, fourthStation, 10, 1L);
        fourthSection = new Section(fourthStation, fifthStation, 10, 1L);
    }

    @DisplayName("구간들을 정렬하고 구간들에 포함된 중복이 없는 역을 순서대로 반환한다.")
    @Test
    void convertIntoStations() {
        List<Section> unsortedSections = Arrays.asList(secondSection, fourthSection, firstSection, thirdSection);

        Sections sections = new Sections(unsortedSections);
        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(new Station(1L, "천호역"),
                new Station(2L, "강남역"), new Station(3L, "회기역"),
                new Station(4L, "의정부역"), new Station(5L, "태릉역"));
    }

    @DisplayName("내부 구간들들을 하나로 연결한다.")
    @Test
    void append() {
        List<Section> unsortedSections = Arrays.asList(secondSection, fourthSection, firstSection, thirdSection);

        Sections sections = new Sections(unsortedSections);
        Section appendedSection = sections.append();

        assertThat(appendedSection).isEqualTo(new Section(new Station(1L, "천호역"),
                new Station(5L, "태릉역"), 40, 1L));
    }

    @DisplayName("canExtendEndSection 메서드는")
    @Nested
    class Describe_canExtendEndSection {

        @DisplayName("추가하려는 구간이 상행 신규 종점 등록인지 확인한다.")
        @Test
        void isUpEndStationExtension() {
            Station firstStation = new Station("천호역");
            Station secondStation = new Station("강릉역");
            List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L));

            Sections sections = new Sections(sectionList);
            Station newFirstStation = new Station("회기역");
            Section targetSection = new Section(newFirstStation, firstStation, 151, 1L);
            boolean isEndStationExtension = sections.canExtendEndSection(targetSection);

            assertThat(isEndStationExtension).isTrue();
        }

        @DisplayName("추가하려는 구간이 하행 신규 종점 등록인지 확인한다.")
        @Test
        void isDownEndStationExtension() {
            Station firstStation = new Station("천호역");
            Station secondStation = new Station("강릉역");
            List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L));

            Sections sections = new Sections(sectionList);
            Station lastStation = new Station("회기역");
            Section targetSection = new Section(secondStation, lastStation, 151, 1L);
            boolean isEndStationExtension = sections.canExtendEndSection(targetSection);

            assertThat(isEndStationExtension).isTrue();
        }

        @DisplayName("추가하려는 구간이 신규 종점 등록이 아니면 false를 반환한다.")
        @Test
        void isNotEndStationExtension() {
            Station firstStation = new Station("천호역");
            Station secondStation = new Station("강릉역");
            Station lastStation = new Station("회기역");
            List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L),
                    new Section(secondStation, lastStation, 5, 1L));

            Sections sections = new Sections(sectionList);
            Station targetStation = new Station("매화역");
            Section targetSection = new Section(secondStation, targetStation, 151, 1L);
            boolean isEndStationExtension = sections.canExtendEndSection(targetSection);

            assertThat(isEndStationExtension).isFalse();
        }
    }

    @DisplayName("canDeleteEndSection 메서드는")
    @Nested
    class Describe_canDeleteEndSection {

        @DisplayName("현재 등록된 구간이 1이면 종점을 삭제할 수 없어 false를 반환한다.")
        @Test
        void cannotDeleteWhenSizeIsOne() {
            Station firstStation = new Station("천호역");
            Station secondStation = new Station("강릉역");
            List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L));

            Sections sections = new Sections(sectionList);
            Sections targetSections = new Sections(sectionList);

            assertThat(sections.canDeleteEndSection(targetSections)).isFalse();
        }

        @DisplayName("인자로 들어온 Sections 구간이 1개가 아니면 삭제할 수 없어 false를 반환한다.")
        @Test
        void cannotDeleteWhenParameterIsNotOne() {
            Station firstStation = new Station("천호역");
            Station secondStation = new Station("강릉역");
            Station lastStation = new Station("회기역");
            List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L),
                    new Section(secondStation, lastStation, 5, 1L));

            Sections sections = new Sections(sectionList);
            Sections targetSections = new Sections(sectionList);

            assertThat(sections.canDeleteEndSection(targetSections)).isFalse();
        }

        @DisplayName("인자로 들어온 Sections의 구간이 현재 등록된 구간들의 종점 구간과 일치해야 true를 반환한다.")
        @Test
        void canDelete() {

            Station firstStation = new Station("천호역");
            Station secondStation = new Station("강릉역");
            Station lastStation = new Station("회기역");
            List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L),
                    new Section(secondStation, lastStation, 5, 1L));

            Sections sections = new Sections(sectionList);
            Sections targetSections = new Sections(Arrays.asList(new Section(firstStation, secondStation, 5, 1L)));

            assertThat(sections.canDeleteEndSection(targetSections)).isTrue();
        }
    }

    @Nested
    @DisplayName("splitLongerSectionAfterAdding 메서드는")
    class Describe_splitLongerSectionAfterAdding {

        @Nested
        @DisplayName("추가하려는 구간의 상 하행 역 모두가 기존 구간들에 등록되어 있을 때")
        class Context_bothStationsExisting {

            @DisplayName("구간을 추가하 못하고 예외가 발생한다.")
            @Test
            void cannotAdd() {
                Station firstStation = new Station("천호역");
                Station secondStation = new Station("강릉역");
                Station lastStation = new Station("회기역");
                List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L),
                        new Section(secondStation, lastStation, 4, 1L));

                Sections sections = new Sections(sectionList);
                Section section = new Section(firstStation, lastStation, 4, 1L);

                assertThatCode(() -> sections.splitLongerSectionAfterAdding(section))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.INVALID_SECTION.getMessage());
            }
        }

        @Nested
        @DisplayName("추가하려는 구간의 상 하행 역 모두가 기존 구간들에 등록되어 있지 않을 때")
        class Context_bothStationNotExisting {

            @DisplayName("구간을 추가하지 못하고 예외가 발생한다.")
            @Test
            void cannotAdd() {
                Station firstStation = new Station("천호역");
                Station secondStation = new Station("강릉역");
                Station lastStation = new Station("회기역");
                List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L),
                        new Section(secondStation, lastStation, 4, 1L));

                Sections sections = new Sections(sectionList);
                Section section = new Section(new Station("매봉역"), new Station("군자역"), 4, 1L);

                assertThatCode(() -> sections.splitLongerSectionAfterAdding(section))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.INVALID_SECTION.getMessage());
            }
        }

        @Nested
        @DisplayName("추가하려는 구간의 상행 역이 현재 구간 목록들에 존재하는 경우")
        class Context_whenUpStationExists {

            @DisplayName("반환되는 구간은 추가하려는 짧은 구간의 하행역을 상행 역으로, 하행 역을 긴 구간의 하행 역으로 한다.")
            @Test
            void splitWhenUpStationExists() {
                Station firstStation = new Station("천호역");
                Station secondStation = new Station("강릉역");
                Station lastStation = new Station("회기역");
                List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L),
                        new Section(secondStation, lastStation, 4, 1L));

                Station newStation = new Station("매봉역");
                Sections sections = new Sections(sectionList);
                Section section = new Section(secondStation, newStation, 2, 1L);
                Section splitSection = sections.splitLongerSectionAfterAdding(section);
                Section compareSection = new Section(newStation, lastStation, 4 - 2, 1L);
                assertThat(splitSection).isEqualTo(compareSection);
            }
        }

        @Nested
        @DisplayName("추가하려는 구간의 하행 역이 현재 구간 목록들에 존재하는 경우")
        class Context_whenDownStationExists {

            @DisplayName("반환되는 구간은 추가하려는 짧은 구간의 상행역을 하행 역으로, 하행 역을 긴 구간의 행 역으로 한다.")
            @Test
            void splitWhenUpStationExists() {
                Station firstStation = new Station("천호역");
                Station secondStation = new Station("강릉역");
                Station lastStation = new Station("회기역");
                List<Section> sectionList = Arrays.asList(new Section(firstStation, secondStation, 5, 1L),
                        new Section(secondStation, lastStation, 4, 1L));

                Station newStation = new Station("매봉역");
                Sections sections = new Sections(sectionList);
                Section section = new Section(newStation, secondStation, 2, 1L);
                Section splitSection = sections.splitLongerSectionAfterAdding(section);
                Section compareSection = new Section(firstStation, newStation, 5 - 2, 1L);
                assertThat(splitSection).isEqualTo(compareSection);
            }
        }
    }
}
