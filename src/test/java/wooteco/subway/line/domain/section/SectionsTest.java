package wooteco.subway.line.domain.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 2L, new Distance(10)));
        sectionList.add(new Section(1L, 2L, 3L, new Distance(10)));
        sectionList.add(new Section(1L, 3L, 4L, new Distance(10)));

        sections = new Sections(sectionList);
    }

    @DisplayName("Sections 객체 생성 가능하다.")
    @Test
    void create() {
        //given
        List<Section> sectionList = Arrays.asList(
                new Section(1L, 1L, 2L, new Distance(10)),
                new Section(1L, 2L, 3L, new Distance(10))
        );

        //when
        Sections sections = new Sections(sectionList);

        //then
        assertThat(sections).isInstanceOf(Sections.class);
    }

    @DisplayName("새로 등록하려는 구간의 상행역을 기준으로 등록할 수 있다.")
    @Test
    void addableUpSection() {
        //given
        Section 하행_추가_가능한_구간 = new Section(1L, 3L, 5L, new Distance(2));

        //when
        sections.add(하행_추가_가능한_구간);
        Section 예상되는_추가_구간 = new Section(1L, 5L, 4L, new Distance(8));
        Section 예상되는_삭제_구간 = new Section(1L, 3L, 4L, new Distance(10));


        //then
        assertThat(sections.toList()).contains(하행_추가_가능한_구간);
        assertThat(sections.toList()).contains(예상되는_추가_구간);
        assertThat(sections.toList()).doesNotContain(예상되는_삭제_구간);
    }

    @DisplayName("새로 등록하려는 구간의 하행역을 기준으로 등록할 수 있다.")
    @Test
    void addableDownSection() {
        //given
        Section 상행_추가_가능한_구간 = new Section(1L, 6L, 3L, new Distance(2));

        //when
        sections.add(상행_추가_가능한_구간);
        Section 예상되는_추가_구간 = new Section(1L, 2L, 6L, new Distance(8));
        Section 예상되는_삭제_구간 = new Section(1L, 2L, 3L, new Distance(10));

        //then
        assertThat(sections.toList()).contains(상행_추가_가능한_구간);
        assertThat(sections.toList()).contains(예상되는_추가_구간);
        assertThat(sections.toList()).doesNotContain(예상되는_삭제_구간);
    }

    @DisplayName("기존 구간의 길이와 같은 구간을 등록한다.")
    @Test
    void addSameDistanceSection() {
        //given
        Section 추가할_수_없는_구간 = new Section(1L, 3L, 10L, new Distance(10));

        //when then
        assertThatThrownBy(() -> sections.add(추가할_수_없는_구간))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("역과 역 사이 새로운 역을 추가할 때 기존 역 사이의 길이보다 크거나 같으면 등록할 수 없습니다.");

    }

    @DisplayName("상행역 하행역 둘다 노선에 등록되지 않은 구간을 등록한다.")
    @Test
    void addNotExistSectionOfStation() {
        //given
        Section 추가할_수_없는_구간 = new Section(1L, 555L, 666L, new Distance(9));

        //when then
        assertThatThrownBy(() -> sections.add(추가할_수_없는_구간))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("연결할 수 있는 역이 구간내에 없습니다.");
    }

    @DisplayName("상행역 하행역 둘다 이미 노선에 등록된 구간을 등록한다.")
    @Test
    void addAlreadyExistSectionOfStation() {
        //given
        Section 추가할_수_없는_구간 = new Section(1L, 1L, 2L, new Distance(9));

        //when then
        assertThatThrownBy(() -> sections.add(추가할_수_없는_구간))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("상행역과 하행역이 이미 존재합니다.");
    }

    @DisplayName("지하철 중간 역 제거한다.")
    @Test
    void deleteBetweenStation() {
        //given
        Long stationId = 2L;
        int expectedSize = sections.toList().size();

        //when
        sections.delete(stationId);

        //then
        assertThat(sections.toList()).hasSize(expectedSize - 1);
    }

    @DisplayName("지하철 종점 제거한다.")
    @Test
    void deleteEndPoint() {
        //given
        Long stationId = 1L;
        int 종점_삭제_전_SIZE = sections.toList().size();

        //when
        sections.delete(stationId);

        //then
        assertThat(sections.toList()).hasSize(종점_삭제_전_SIZE - 1);
    }

    @DisplayName("구간이 하나 남았을 때 구간을 삭제한다.")
    @Test
    void deleteOneLeftSection() {
        //given
        List<Section> singleSection =
                Collections.singletonList(new Section(1L, 1L, 2L, new Distance(10)));
        Sections 구간_하나만_포함 = new Sections(singleSection);

        //when then
        assertThatThrownBy(() -> 구간_하나만_포함.delete(1L))
                .isInstanceOf(IllegalStateException.class).hasMessage("구간이 하나 이하일 때는 삭제할 수 없습니다.");
    }
}