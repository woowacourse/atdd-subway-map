package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.NonexistentSectionStationException;
import wooteco.subway.exception.section.SectionLengthExcessException;

public class SectionServiceTest {

    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        sectionService = new SectionService(new FakeSectionDao());
        sectionService.save(new Section(1L, 1L, 2L, 10));
    }

    @DisplayName("새로운 구간을 추가한다.")
    @Test
    void save() {
        Section newSection = new Section(1L, 2L, 3L, 5);

        assertThat(sectionService.save(newSection).getUpStationId()).isEqualTo(2L);
    }

    @DisplayName("새로 등록할 구간의 하행역이 이미 노선의 상행 종착역 으로 등록되어 있으면, 상행 종점으로 등록한다.")
    @Test
    void save_upStation() {
        Section newSection = new Section(1L, 3L, 1L, 5);

        assertThat(sectionService.save(newSection).getUpStationId()).isEqualTo(3L);
    }

    @DisplayName("새로 등록할 구간의 상행역이 이미 노선의 하행 종착역으로 등록되어 있으면, 하행 종점으로 등록한다.")
    @Test
    void save_downStation() {
        Section newSection = new Section(1L, 2L, 3L, 5);

        assertThat(sectionService.save(newSection).getDownStationId()).isEqualTo(3L);
    }

    @DisplayName("존재하는 상행역이 같으면, 기존 구간의 상핵역을 새로 등록할 구간의 하행역으로 변경한다.")
    @Test
    void save_changeExistingSectionUpStation() {
        Section newSection = new Section(1L, 1L, 4L, 3);

        sectionService.save(newSection);

        List<Section> sections = sectionService.findByLineId(1L);

        Section targetSection = sections.stream()
                .filter(section -> section.getId().equals(1L))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        assertThat(targetSection.getUpStationId()).isEqualTo(4L);
    }

    @DisplayName("존재하는 하행역이 같으면, 기존 구간의 하행역을 새로 등록할 구간의 상행역으로 변경한다.")
    @Test
    void save_changeExistingSectionDownStation() {
        Section newSection = new Section(1L, 4L, 2L, 3);

        sectionService.save(newSection);

        List<Section> sections = sectionService.findByLineId(1L);

        Section targetSection = sections.stream()
                .filter(section -> section.getId().equals(1L))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        assertThat(targetSection.getDownStationId()).isEqualTo(4L);
    }

    @DisplayName("새로운 구간의 길이가 기존 역 사이 길이보다 크거나 같으면 예외가 발생한다.")
    @Test
    void save_distanceException() {
        Section newSection = new Section(1L, 4L, 2L, 10);

        assertThatThrownBy(() -> sectionService.save(newSection))
                .isInstanceOf(SectionLengthExcessException.class);
    }

    @DisplayName("새로운 구간의 상행역과 하행역이 이미 노선에 등록되어 있는 경우, 추가할 수 없다. A-B")
    @Test
    void save_duplicateSection1() {
        Section newSection = new Section(1L, 1L, 2L, 10);

        assertThatThrownBy(() -> sectionService.save(newSection))
                .isInstanceOf(DuplicatedSectionException.class);
    }

    @DisplayName("새로운 구간의 상행역과 하행역이 이미 노선에 등록되어 있는 경우, 추가할 수 없다. A-B-C인 경우, A-C 등록")
    @Test
    void save_duplicateSection2() {
        sectionService.save(new Section(1L, 2L, 3L, 10));
        Section newSection = new Section(1L, 1L, 3L, 10);

        assertThatThrownBy(() -> sectionService.save(newSection))
                .isInstanceOf(DuplicatedSectionException.class);
    }

    @DisplayName("새로운 구간의 상행역과 하행역이 이미 노선에 등록되어 있는 경우, 추가할 수 없다. A-B인 경우, B-A 등록")
    @Test
    void save_duplicateSection3() {
        Section newSection = new Section(1L, 2L, 1L, 10);

        assertThatThrownBy(() -> sectionService.save(newSection))
                .isInstanceOf(DuplicatedSectionException.class);
    }

    @DisplayName("상행역과 하행 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다.")
    @Test
    void save_unExistingException() {
        Section newSection = new Section(1L, 5L, 6L, 10);

        assertThatThrownBy(() -> sectionService.save(newSection))
                .isInstanceOf(NonexistentSectionStationException.class);
    }

    @DisplayName("lineId에 해당되는 모든 구간들을 반환한다.")
    @Test
    void findByLineId() {
        Section newSection = new Section(1L, 2L, 3L, 5);
        sectionService.save(newSection);

        Sections sections = new Sections(sectionService.findByLineId(1L), 1L);

        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("lineId에 해당되는 구간들을 변경한다.")
    @Test
    void update() {
        Section newSection = new Section(1L, 2L, 3L, 5);
        sectionService.save(newSection);

        Sections sections = new Sections(sectionService.findByLineId(1L), 1L);

        assertThat(sections.getSections()).hasSize(2);
    }

    @DisplayName("lineId에 해당되는 station id들을 반환한다.")
    @Test
    void findStationIdsByLineId() {
        Section newSection = new Section(1L, 2L, 3L, 5);
        sectionService.save(newSection);

        List<Long> stationIds = sectionService.findStationIdsByLineId(1L);

        assertThat(stationIds).containsExactly(1L, 2L, 3L);
    }

    @DisplayName("종점이 상행선인 경우, 종점의 하행선이 종점이 된다.")
    @Test
    void remove_upStation() {
        sectionService.save(new Section(1L, 2L, 3L, 5));
        sectionService.save(new Section(1L, 3L, 4L, 10));

        sectionService.remove(1L, 1L);

        List<Long> stationIds = sectionService.findStationIdsByLineId(1L);

        assertThat(stationIds).containsExactly(2L, 3L, 4L);
    }

    @DisplayName("종점이 하행선인 경우, 종점의 상행선이 종점이 된다.")
    @Test
    void remove_downStation() {
        sectionService.save(new Section(1L, 2L, 3L, 5));
        sectionService.save(new Section(1L, 3L, 4L, 10));

        sectionService.remove(1L, 4L);

        List<Long> stationIds = sectionService.findStationIdsByLineId(1L);

        assertThat(stationIds).containsExactly(1L, 2L, 3L);
    }

    @DisplayName("A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 된다.")
    @Test
    void remove_middleStation() {
        sectionService.save(new Section(1L, 2L, 3L, 5));
        sectionService.save(new Section(1L, 3L, 4L, 10));

        sectionService.remove(1L, 2L);

        List<Long> stationIds = sectionService.findStationIdsByLineId(1L);
        Section newSection = sectionService.findByLineId(1L)
                .stream()
                .filter(it -> it.isSameUpStationId(1L))
                .findFirst()
                .orElseThrow();

        assertThat(stationIds).containsExactly(1L, 3L, 4L);
        assertThat(newSection.getDistance()).isEqualTo(15);
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거하려고 하면 예외가 발생한다.")
    @Test
    void remove_onlySection() {
        assertThatThrownBy(() -> sectionService.remove(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("입력받은 지하철 역이 노선에 존재하지 않는 경우 예외가 발생한다.")
    @Test
    void remove_notExist() {
        assertThatThrownBy(() -> sectionService.remove(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
