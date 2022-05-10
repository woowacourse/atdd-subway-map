package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.BothUpAndDownStationAlreadyExistsException;
import wooteco.subway.exception.BothUpAndDownStationDoNotExistException;
import wooteco.subway.exception.CanNotInsertSectionException;
import wooteco.subway.exception.OnlyOneSectionException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Section initialSection;
    private Sections sections;

    @BeforeEach
    void setUp() {
        initialSection = new Section(1L, 2L, new Distance(10));
        sections = new Sections(initialSection.getUpStationId(), initialSection.getDownStationId(), initialSection.getDistance());
    }

    @DisplayName("상행 종점, 하행 종점, 거리를 전달받아 구간 목록 생성")
    @Test
    void constructor() {
        // given
        Long upStationId = 1L;
        Long downStationId = 2L;
        Distance distance = new Distance(10);

        // when
        Sections sections = new Sections(upStationId, downStationId, distance);

        // then
        assertThat(sections).isNotNull();
    }

    @DisplayName("이미 존재하는 역과 새롭게 상행 종점이 될 역으로 구성된 구간을 추가")
    @Test
    void addSection_withNewUpStation() {
        // given
        Section newSection = new Section(3L, 1L, new Distance(5));

        // when
        sections.addSection(newSection);

        // then
        assertThat(sections.getValue()).containsAll(List.of(initialSection, newSection));
    }

    @DisplayName("이미 존재하는 역과 새롭게 하행 종점이 될 역으로 구성된 구간을 추가")
    @Test
    void addSection_withNewDownStation() {
        // given
        Section newSection = new Section(2L, 4L, new Distance(5));

        // when
        sections.addSection(newSection);

        // then
        assertThat(sections.getValue()).containsAll(List.of(initialSection, newSection));
    }

    @DisplayName("상행선 기준으로 새로운 구간을 생성할 때 갈래길이 생기지 않는다.")
    @Test
    void addSection_insertingUpStation() {
        // given
        Section newSection = new Section(1L, 3L, new Distance(5));

        // when
        sections.addSection(newSection);
        List<Section> actual = sections.getValue();

        // then
        List<Section> expected = List.of(new Section(1L, 3L, new Distance(5)), new Section(3L, 2L, new Distance(5)));
        assertThat(actual).containsAll(expected);
    }

    @DisplayName("하행선 기준으로 새로운 구간을 생성할 때 갈래길이 생기지 않는다.")
    @Test
    void addSection_insertingDownStation() {
        // given
        Section newSection = new Section(3L, 2L, new Distance(5));

        // when
        sections.addSection(newSection);
        List<Section> actual = sections.getValue();

        // then
        List<Section> expected = List.of(new Section(1L, 3L, new Distance(5)), new Section(3L, 2L, new Distance(5)));
        assertThat(actual).containsAll(expected);
    }

    @DisplayName("구간 삽입시 기존 구간의 길이보다 더 긴 길이의 구간을 삽입할 시 예외가 발생한다.")
    @Test
    void addSection_throwsExceptionOnTryingToInsertLongerSection() {
        // given
        Section newSection = new Section(3L, 2L, new Distance(15));

        // when & then
        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(CanNotInsertSectionException.class);
    }

    @DisplayName("구간 추가시 새로운 구간의 상행역과 하행역이 모두 이미 구간 목록에 등록되어 있다면 예외가 발생한다.")
    @Test
    void addSection_throwsExceptionIfBothUpAndDownStationAlreadyExistsInSections() {
        // given
        Section newSection = new Section(1L, 2L, new Distance(5));

        // when & then
        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(BothUpAndDownStationAlreadyExistsException.class);
    }

    @DisplayName("구간 추가시 새로운 구간의 상행역과 하행역이 모두 구간 목록에 등록되어 있지 않다면 예외가 발생한다.")
    @Test
    void addSection_throwsExceptionIfBothUpAndDownStationDoNotExistInSections() {
        // given
        Section newSection = new Section(3L, 4L, new Distance(5));

        // when & then
        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(BothUpAndDownStationDoNotExistException.class);
    }

    @DisplayName("구간 목록의 상행역을 제거할 수 있다.")
    @Test
    void deleteStation_upStation() {
        // given
        sections.addSection(new Section(2L, 3L, new Distance(10)));
        sections.addSection(new Section(3L, 4L, new Distance(10)));

        // when
        sections.deleteStation(1L);

        // then
        assertThat(sections.getValue()).containsAll(List.of(
                new Section(2L, 3L, new Distance(10)),
                new Section(3L, 4L, new Distance(10))
        ));
    }


    @DisplayName("구간 목록의 하행역을 제거할 수 있다.")
    @Test
    void deleteStation_downStation() {
        // given
        sections.addSection(new Section(2L, 3L, new Distance(10)));
        sections.addSection(new Section(3L, 4L, new Distance(10)));

        // when
        sections.deleteStation(4L);

        // then
        assertThat(sections.getValue()).containsAll(List.of(
                new Section(1L, 2L, new Distance(10)),
                new Section(2L, 3L, new Distance(10))
        ));
    }

    @DisplayName("구간 목록의 중간역을 제거할 수 있다.")
    @Test
    void deleteStation_betweenStation() {
        // given
        sections.addSection(new Section(2L, 3L, new Distance(10)));
        sections.addSection(new Section(3L, 4L, new Distance(10)));

        // when
        sections.deleteStation(3L);

        // then
        assertThat(sections.getValue()).containsAll(List.of(
                new Section(1L, 2L, new Distance(10)),
                new Section(2L, 4L, new Distance(20))
        ));
    }

    @DisplayName("구간이 단 하나인 구간 목록에서 구간 제거를 하면 예외가 발생한다.")
    @Test
    void deleteStation_throwsExceptionIfSectionsSizeIsOne() {
        assertThatThrownBy(() -> sections.deleteStation(1L))
                .isInstanceOf(OnlyOneSectionException.class);
    }
}
