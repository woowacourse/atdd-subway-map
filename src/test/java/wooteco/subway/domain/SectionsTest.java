package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.section.IllegalMergeSectionException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 리스트 관리 기능")
class SectionsTest {

    @DisplayName("[구간 추가 불가] sections 내부에 section이 동일하게 존재하면 예외를 발생한다.")
    @Test
    void isExistedEquallyIn() {
        //given
        Section section = new Section(10, 1L, 1L, 2L);
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);

        Sections sections = new Sections(List.of(existedSection1, existedSection2));

        //when & then
        assertThatThrownBy(() -> sections.validateInsertable(section))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining("추가할 수 없는 구간입니다.");
    }


    @DisplayName("[구간 추가 불가] sections 내부에 section과 겹치는 구간이 있는 section이 존재하면 예외를 발생한다.")
    @Test
    void isExistedLinearlyIn() {
        //given
        Section section = new Section(10, 1L, 1L, 4L);
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);
        Section existedSection3 = new Section(10, 2L, 3L, 4L);

        Sections sections = new Sections(List.of(existedSection1, existedSection2, existedSection3));

        //when & then
        assertThatThrownBy(() -> sections.validateInsertable(section))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining("추가할 수 없는 구간입니다.");
    }


    @DisplayName("[구간 추가 불가] sections 내부에 상,하행역 아무데도 겹치는 구간이 있는 section이면 예외를 발생한다.")
    @Test
    void isNotExistedIn() {
        //given
        Section section = new Section(10, 1L, 6L, 7L);
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);
        Section existedSection3 = new Section(10, 2L, 3L, 4L);

        Sections sections = new Sections(List.of(existedSection1, existedSection2, existedSection3));

        //when & then
        assertThatThrownBy(() -> sections.validateInsertable(section))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining("추가할 수 없는 구간입니다.");
    }

    @DisplayName("[종점으로 추가 가능] sections의 상행 종점역으로 추가가 가능하면 Optional.empty 반환")
    @Test
    void isUpperLastStop() {
        //given
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);
        Sections sections = new Sections(List.of(section1, section2, section3));
        Section newSection = new Section(10, 2L, 4L, 5L);

        //when
        Optional<Section> section = sections.getSectionToDelete(newSection);

        //then
        assertThat(section).isEmpty();
    }

    @DisplayName("[종점으로 추가 가능] sections의 하행 종점역으로 추가가 가능하면 Optional.empty 반환")
    @Test
    void isLowerLastStop() {
        //given
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);
        Sections sections = new Sections(List.of(section1, section2, section3));
        Section newSection = new Section(10, 2L, 6L, 1L);

        //when
        Optional<Section> section = sections.getSectionToDelete(newSection);

        //then
        assertThat(section).isEmpty();
    }

    @DisplayName("[갈래길로 추가 가능] sections의 상행 구간으로 추가가 가능하면 값이 있는 Optional 반환")
    @Test
    void createBetweenUpSection() {
        //given
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);
        Sections sections = new Sections(List.of(section1, section2, section3));
        Section newSection = new Section(4, 2L, 1L, 5L);

        //when
        Optional<Section> section = sections.getSectionToDelete(newSection);

        //then
        assertThat(section).isPresent();
    }


    @DisplayName("[갈래길로 추가 가능] 하행 구간 사이 갈림길이 모든 조건이 충족하면 추가할 수 있다.")
    @Test
    void createBetweenUpSectionSameLine() {
        //given
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);
        Sections sections = new Sections(List.of(section1, section2, section3));
        Section newSection = new Section(4, 2L, 5L, 3L);

        //when
        Optional<Section> section = sections.getSectionToDelete(newSection);

        //then
        assertThat(section).isPresent();
    }

    @DisplayName("[갈래길 길이 조건 미충족] 상행 구간 사이 갈림길이 길이 조건이 충족하지 않으면 예외를 발생한다..")
    @Test
    void createBetweenUpSectionBadDistance() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);
        Sections sections = new Sections(List.of(section1, section2, section3));
        Section newSection = new Section(10, 2L, 3L, 5L);

        assertThatThrownBy(() -> sections.getSectionToDelete(newSection))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining("불가능한 구간의 길이입니다.");
    }

    @DisplayName("[갈래길 길이 조건 미충족]하행 구간 사이 갈림길이 길이 조건이 충족하지 않으면 예외를 발생한다.")
    @Test
    void createBetweenDownSectionBadDistance() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);
        Sections sections = new Sections(List.of(section1, section2, section3));
        Section newSection = new Section(10, 2L, 5L, 3L);

        assertThatThrownBy(() -> sections.getSectionToDelete(newSection))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining("불가능한 구간의 길이입니다.");
    }

//    @DisplayName("노선 전체에서 종점 역 아이디를 찾아낸다.")
//    @Test
//    void findLastStopId() {
//        Section section1 = new Section(10, 2L, 1L, 3L);
//        Section section2 = new Section(10, 2L, 3L, 2L);
//        Section section3 = new Section(10, 2L, 2L, 4L);
//
//        Sections sections = new Sections(List.of(section1, section2, section3));
//
//        List<Long> lastStopStationIds = sections.getLastStationIds();
//
//        assertThat(lastStopStationIds.size()).isEqualTo(2);
//        assertThat(lastStopStationIds).isEqualTo(List.of(1L, 4L));
//    }

    @DisplayName("노선 전체에서 상행 역 아이디가 존재하는 구간을 찾는다.")
    @Test
    void getExistedUpStationSection() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Section upStationSection = new Section(10, 2L, 1L, 2L);
        Sections sections = new Sections(List.of(section1, section2, section3));

        Optional<Section> upStationIds = sections.getExistingUpStationSection(upStationSection);

        assertThat(upStationIds).isPresent();
    }

    @DisplayName("노선 전체에서 상행 역 아이디가 존재하는 구간이 없으면 Empty Optional 을 반환한다.")
    @Test
    void getExistedUpStationSectionReturnEmptyOptional() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Section upStationSection = new Section(10, 2L, 4L, 5L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Optional<Section> upStationIds = sections.getExistingUpStationSection(upStationSection);

        assertThat(upStationIds).isEmpty();
    }

    @DisplayName("노선 전체에서 하행 역 아이디가 존재하는 구간을 찾는다.")
    @Test
    void getExistedDownStationSection() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Section downStationSection = new Section(10, 2L, 3L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Optional<Section> downStationSectionOptional = sections.getExistingDownStationSection(downStationSection);

        assertThat(downStationSectionOptional).isPresent();
    }

    @DisplayName("노선 전체에서 하행 역 아이디가 존재하는 구간이 없으면 Empty Optional 을 반환한다.")
    @Test
    void getExistedDownStationSectionReturnEmptyOptional() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);
        Section givenDownStationSection = new Section(10, 2L, 3L, 5L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Optional<Section> downStationSection = sections.getExistingDownStationSection(givenDownStationSection);

        assertThat(downStationSection).isEmpty();
    }

//    @DisplayName("해당 역이 종점인지 확인한다.")
//    @ParameterizedTest
//    @CsvSource({"1,true", "2,false"})
//    void isLastStation(Long stationId, boolean result) {
//        Section section1 = new Section(10, 2L, 1L, 3L);
//        Section section2 = new Section(10, 2L, 3L, 2L);
//        Section section3 = new Section(10, 2L, 2L, 4L);
//
//        Sections sections = new Sections(List.of(section1, section2, section3));
//
//        boolean actual = sections.isLastStation(stationId);
//
//        assertThat(actual).isEqualTo(result);
//    }

    @DisplayName("역과 관련된 구간을 모두 반환한다.")
    @Test
    void getSectionsByStationId() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Sections actual = sections.getByStationId(2L);

        assertThat(actual.getSections().size()).isEqualTo(2);
        assertThat(actual.getSections()).isEqualTo(List.of(section2, section3));
    }

    @DisplayName("세 개 이상의 구간은 합칠 수 없다.")
    @Test
    void cannotMergeMoreThanThreeSections() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        assertThatThrownBy(sections::mergeSections)
                .isInstanceOf(IllegalMergeSectionException.class);
    }

    @DisplayName("두 개의 구간을 합친다.")
    @Test
    void mergeTwoSections() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);

        Sections sections = new Sections(List.of(section2, section1));

        Section section = sections.mergeSections();

        assertAll(() -> {
            assertThat(section.getDistance()).isEqualTo(20);
            assertThat(section.getUpStationId()).isEqualTo(1L);
            assertThat(section.getDownStationId()).isEqualTo(2L);
            assertThat(section.getLineId()).isEqualTo(2);
        });
    }
}
