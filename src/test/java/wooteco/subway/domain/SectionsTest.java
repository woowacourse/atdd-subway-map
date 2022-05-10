package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

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
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 역 사이에 새 역을 등록할 경우엔 길이가 원래 있던 길이보다 짧아야합니다.");
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
}