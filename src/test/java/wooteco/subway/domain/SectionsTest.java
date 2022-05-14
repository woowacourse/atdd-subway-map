package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.MinimumSectionDistanceException;
import wooteco.subway.exception.NoElementSectionsException;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.exception.ResisterSectionException;

class SectionsTest {
    
    @Test
    @DisplayName("비어있는 구간 리스트로 Sections 객체 생성시 예외가 발생한다.")
    void emptyListSections() {
        assertThatThrownBy(() -> new Sections(new LinkedList<>()))
                .isInstanceOf(NoElementSectionsException.class)
                .hasMessage("[ERROR] 최소 한 개의 구간이 있어야 Sections 객체를 생성할 수 있습니다.");
    }

    @Test
    @DisplayName("구간 일급 컬렉션에 새 구간을 추가한다. - 맨 뒤")
    void addSectionBack() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);
        sections.addSection(station2, station3, 10);

        final SectionsUpdateResult sectionsUpdateResult = sections.addSection(station3, station4, 5);

        final Section section2 = new Section(2L, station2, station3, 10);
        final Section section3 = new Section(3L, station3, station4, 5);

        assertAll(
                () -> assertThat(sections.getValue()).isEqualTo(List.of(section1, section2, section3)),
                () -> assertThat(sectionsUpdateResult.getAddedSections().get(0)).isEqualTo(section3)
        );
    }

    @Test
    @DisplayName("구간 일급 컬렉션에 새 구간을 추가한다. - 맨 앞")
    void addSectionFront() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station2, station3, 10);
        final Sections sections = new Sections(section1);
        sections.addSection(station3, station4, 10);

        final SectionsUpdateResult sectionsUpdateResult = sections.addSection(station1, station2, 5);

        final Section section2 = new Section(2L, station3, station4, 10);
        final Section section3 = new Section(3L, station1, station2, 5);

        assertAll(
                () -> assertThat(sections.getValue()).isEqualTo(List.of(section3, section1, section2)),
                () -> assertThat(sectionsUpdateResult.getAddedSections().get(0)).isEqualTo(section3)
        );
    }

    @Test
    @DisplayName("구간 일급 컬렉션에 새 구간을 추가한다. - 상행기준 중간삽입")
    void addSectionUpDirectionInsert() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);
        sections.addSection(station2, station4, 10);

        final SectionsUpdateResult sectionsUpdateResult = sections.addSection(station2, station3, 5);

        final Section section2 = new Section(2L, station2, station3, 5);
        final Section section3 = new Section(3L, station3, station4, 5);

        final Section deleted = new Section(null, station2, station4, 10);

        assertAll(
                () -> assertThat(sections.getValue()).isEqualTo(List.of(section1, section2, section3)),
                () -> assertThat(sectionsUpdateResult.getAddedSections().get(0)).isEqualTo(section2),
                () -> assertThat(sectionsUpdateResult.getAddedSections().get(1)).isEqualTo(section3),
                () -> assertThat(sectionsUpdateResult.getDeletedSections().get(0)).isEqualTo(deleted)
        );
    }

    @Test
    @DisplayName("구간 일급 컬렉션에 새 구간을 추가한다. - 하행기준 중간삽입")
    void addSectionDownDirectionInsert() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);
        sections.addSection(station2, station4, 10);

        final SectionsUpdateResult sectionsUpdateResult = sections.addSection(station3, station4, 5);

        final Section section2 = new Section(2L, station2, station3, 5);
        final Section section3 = new Section(3L, station3, station4, 5);
        final Section deleted = new Section(null, station2, station4, 10);

        assertAll(
                () -> assertThat(sections.getValue()).isEqualTo(List.of(section1, section2, section3)),
                () -> assertThat(sectionsUpdateResult.getAddedSections().get(0)).isEqualTo(section2),
                () -> assertThat(sectionsUpdateResult.getAddedSections().get(1)).isEqualTo(section3),
                () -> assertThat(sectionsUpdateResult.getDeletedSections().get(0)).isEqualTo(deleted)
        );
    }

    @Test
    @DisplayName("역 사이에 새 역을 삽입할 시 원래 구간보다 길거나 같은 길이를 입력하면 예외가 발생한다.")
    void addSectionInsertDistanceException() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);

        sections.addSection(station2, station4, 10);
        assertThatThrownBy(() -> sections.addSection(station3, station4, 10))
                .isInstanceOf(MinimumSectionDistanceException.class)
                .hasMessage("[ERROR] 구간 사이의 길이는 최소 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("역 사이에 새 역을 삽입할 시 입력된 구간의 두 지하철역이 둘 다 기존 구간 리스트에 존재하지 않을 경우 예외가 발생한다.")
    void addSectionNotExistStations() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);

        assertThatThrownBy(() -> sections.addSection(station3, station4, 10))
                .isInstanceOf(ResisterSectionException.class)
                .hasMessage("[ERROR] 등록하려는 구간의 상행선 또는 하행선 중 한개는 노선에 존재해야합니다.");
    }

    @Test
    @DisplayName("역 사이에 새 역을 삽입할 시 입력된 구간의 두 지하철역이 둘 다 기존 구간 리스트에 존재할 경우 예외가 발생한다.")
    void addSectionAllExistStations() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);

        assertThatThrownBy(() -> sections.addSection(station1, station2, 5))
                .isInstanceOf(ResisterSectionException.class)
                .hasMessage("[ERROR] 등록하려는 구간의 상행선 또는 하행선 중 한개만 노선에 존재해야합니다.");
    }

    @Test
    @DisplayName("구간 일급 컬렉션에서 지하철역을 삭제한다. - 맨 뒤")
    void removeStationBack() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);
        sections.addSection(station2, station3, 10);
        sections.addSection(station3, station4, 5);

        final SectionsUpdateResult sectionsUpdateResult = sections.removeStation(station4);

        final Section section2 = new Section(2L, station2, station3, 10);
        final Section section3 = new Section(3L, station3, station4, 5);
        assertAll(
                () -> assertThat(sections.getValue()).isEqualTo(List.of(section1, section2)),
                () -> assertThat(sectionsUpdateResult.getDeletedSections().get(0)).isEqualTo(section3)
        );
    }

    @Test
    @DisplayName("구간 일급 컬렉션에서 지하철역을 삭제한다. - 맨 앞")
    void removeStationFront() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);
        sections.addSection(station2, station3, 10);
        sections.addSection(station3, station4, 5);

        final SectionsUpdateResult sectionsUpdateResult = sections.removeStation(station1);

        final Section section2 = new Section(2L, station2, station3, 10);
        final Section section3 = new Section(3L, station3, station4, 5);
        assertAll(
                () -> assertThat(sections.getValue()).isEqualTo(List.of(section2, section3)),
                () -> assertThat(sectionsUpdateResult.getDeletedSections().get(0)).isEqualTo(section1)
        );
    }

    @Test
    @DisplayName("구간 일급 컬렉션에서 지하철역을 삭제한다. - 사이")
    void removeStationBetween() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);
        sections.addSection(station2, station3, 10);
        sections.addSection(station3, station4, 5);

        final SectionsUpdateResult sectionsUpdateResult = sections.removeStation(station2);

        final Section section2 = new Section(2L, station1, station3, 20);
        final Section section3 = new Section(3L, station3, station4, 5);
        final Section deleted = new Section(null, station2, station3, 10);

        assertAll(
                () -> assertThat(sections.getValue()).isEqualTo(List.of(section2, section3)),
                () -> assertThat(sectionsUpdateResult.getDeletedSections().get(0)).isEqualTo(section1),
                () -> assertThat(sectionsUpdateResult.getDeletedSections().get(1)).isEqualTo(deleted),
                () -> assertThat(sectionsUpdateResult.getAddedSections().get(0)).isEqualTo(section2)
        );
    }
    
    @Test
    @DisplayName("구간 일급 컬렉션에서 지하철역 삭제시 지하철역이 노선에 존재하지 않으면 예외가 발생한다.")
    void removeNotExistStation() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);
        sections.addSection(station2, station3, 10);

        assertThatThrownBy(() -> sections.removeStation(station4))
                .isInstanceOf(NotFoundStationException.class)
                .hasMessage("[ERROR] 해당 지하철역이 존재하지 않습니다.");
    }
}