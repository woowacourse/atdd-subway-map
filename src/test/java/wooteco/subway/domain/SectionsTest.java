package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateSectionException;
import wooteco.subway.exception.InvalidSectionCreateRequestException;

class SectionsTest {

    @DisplayName("구간 순서대로 역들을 정렬해서 반환한다.")
    @Test
    void getSortedStations() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station1, station2, 1);
        Section section2 = new Section(station2, station3, 1);

        Sections sections = new Sections(List.of(section1, section2));

        List<Station> stations = sections.getSortedStations();

        assertThat(stations).containsSequence(station1, station2, station3);
    }

    @DisplayName("추가하려는 구간의 시,종점이 모두 기존 구간들에 없을 경우 예외를 반환한다.")
    @Test
    void cannotFindUpStationAndDownStation() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station1, station2, 1);
        Section section2 = new Section(station2, station3, 1);

        Sections sections = new Sections(List.of(section1, section2));

        Section invalidNewSection = new Section(new Station("삼성역"), new Station("종합운동장역"), 1);

        assertThatThrownBy(() -> sections.addSection(invalidNewSection))
                .isInstanceOf(InvalidSectionCreateRequestException.class)
                .hasMessage("구간 시작 역과 끝 역이 모두 노선에 존재하지 않아 추가할 수 없습니다.");
    }

    @DisplayName("추가하려는 구간이 이미 존재할 경우 예외를 반환한다.")
    @Test
    void duplicateSection() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station1, station2, 1);
        Section section2 = new Section(station2, station3, 1);

        Sections sections = new Sections(List.of(section1, section2));

        Section invalidNewSection = new Section(station1, station2, 1);

        assertThatThrownBy(() -> sections.addSection(invalidNewSection))
                .isInstanceOf(DuplicateSectionException.class)
                .hasMessage("이미 존재하는 구간입니다.");
    }

    @DisplayName("추가하려는 구간이 이미 존재하는 구간들 중 해당 구간을 포함하는 구간의 길이보다 길거나 같으면 예외를 반환한다.")
    @Test
    void longerThanOrEqualToIncludingSectionDistance() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station1, station3, 1);

        Sections sections = new Sections(List.of(section1));

        Section invalidNewSection = new Section(station1, station2, 1);

        assertThatThrownBy(() -> sections.addSection(invalidNewSection))
                .isInstanceOf(InvalidSectionCreateRequestException.class)
                .hasMessage("새로 추가하려는 중간 구간은 해당 구간을 포함하는 기존 구간보다 길거나 같은 길이일 수 없습니다.");
    }

    @DisplayName("시점을 연장하는 구간을 추가한다.")
    @Test
    void addStartSection() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station2, station3, 1);
        Sections sections = new Sections(List.of(section1));

        Section newSection = new Section(station1, station2, 1);

        sections.addSection(newSection);

        assertThat(sections.getValues()).contains(newSection);
    }

    @DisplayName("종점을 연장하는 구간을 추가한다.")
    @Test
    void addEndSection() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station1, station2, 1);
        Sections sections = new Sections(List.of(section1));

        Section newSection = new Section(station2, station3, 1);

        sections.addSection(newSection);

        assertThat(sections.getValues()).contains(newSection);
    }

    @DisplayName("중간 구간을 추가한다.")
    @Test
    void addMiddle() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station1, station3, 3);
        Sections sections = new Sections(List.of(section1));

        Section newSection = new Section(station1, station2, 1);
        sections.addSection(newSection);

        List<Section> values = sections.getValues();
        assertThat(values).hasSize(2)
                .extracting(Section::getUpStation, Section::getDownStation, Section::getDistance)
                .containsOnly(
                        tuple(station1, station2, newSection.getDistance()),
                        tuple(station2, station3, 2)
                );
        assertThat(values).doesNotContain(section1);
    }

    @DisplayName("구간들에 존재하지 않는 역에 대해 구간 삭제 요청이 들어오면 예외를 반환한다.")
    @Test
    void invalidDeleteRequest() {
        Sections sections = new Sections(new ArrayList<>());

        assertThatThrownBy(() -> sections.deleteSectionByStation(new Station("강남역")))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("요청하는 역을 포함하는 구간이 없습니다.");
    }

    @DisplayName("시점이나 종점 역에 대한 구간 삭제 요청이 들어오면 그대로 삭제한다.")
    @Test
    void deleteStartOrEnd() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station1, station2, 1);
        Section section2 = new Section(station2, station3, 1);

        Sections sections = new Sections(List.of(section1, section2));

        sections.deleteSectionByStation(station1);

        assertThat(sections.getValues()).hasSize(1)
                .extracting(Section::getUpStation, Section::getDownStation, Section::getDistance)
                .containsOnly(
                        tuple(station2, station3, 1)
                );
    }

    @DisplayName("중간에 있는 역에 대해 구간 삭제 요청이 들어올 경우 해당 역을 포함하는 구간들을 제거하고 구간들을 이어붙인다.")
    @Test
    void deleteMiddle() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station1, station2, 1);
        Section section2 = new Section(station2, station3, 1);

        Sections sections = new Sections(List.of(section1, section2));

        sections.deleteSectionByStation(station2);

        assertThat(sections.getValues()).hasSize(1)
                .extracting(Section::getUpStation, Section::getDownStation, Section::getDistance)
                .containsOnly(
                        tuple(station1, station3, 2)
                );
    }
}
