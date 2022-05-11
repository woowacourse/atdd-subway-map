package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.dao.FakeSectionDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

class SectionServiceTest {

    private final SectionDao sectionDao = new FakeSectionDao();
    private final SectionService sectionService = new SectionService(sectionDao);

    private Section first;
    private Section second;

    @BeforeEach
    void setUp() {
        first = new Section(1L, 2L, 3L, 8);
        second = new Section(1L, 3L, 4L, 8);
        sectionDao.save(first);
        sectionDao.save(second);
    }

    @DisplayName("새로운 구간을 기존 노선의 앞 혹은 뒤에 등록할 수 있다.")
    @ParameterizedTest
    @CsvSource({"4, 5", "1, 2"})
    void saveBackOrForth(Long upStationId, Long downStationId) {
        int distance = 2;
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        sectionService.connectNewSection(1L, sectionRequest);
        Section newSection = new Section(3L, 1L, upStationId, downStationId, distance);

        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections).contains(first, second, newSection);
    }

    @DisplayName("새로운 구간을 같은 상행선을 가진 구간의 사이에 등록할 수 있다.")
    @Test
    void addBetweenBasedOnUpStation() {
        Long upStationId = 2L;
        Long downStationId = 5L;
        int distance = 2;
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        sectionService.connectNewSection(1L, sectionRequest);
        Section newSection = new Section(3L, 1L, upStationId, downStationId, distance);
        Section changedSection = new Section(1L, 1L, 5L, 3L, 6);

        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections).contains(changedSection, second, newSection);
    }

    @DisplayName("새로운 구간을 같은 하행선을 가진 구간의 사이에 등록할 수 있다.")
    @Test
    void addBetweenBasedOnDownStation() {
        Long upStationId = 5L;
        Long downStationId = 4L;
        int distance = 2;
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        sectionService.connectNewSection(1L, sectionRequest);
        Section newSection = new Section(3L, 1L, upStationId, downStationId, distance);
        Section changedSection = new Section(1L, 1L, 3L, 5L, 6);

        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections).contains(first, changedSection, newSection);
    }

    @DisplayName("상행역을 삭제할 수 있다.")
    @Test
    void deleteForth() {
        sectionService.deleteStation(1L, 2L);

        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections).contains(second)
                .hasSize(1);
    }

    @DisplayName("하행역을 삭제할 수 있다.")
    @Test
    void deleteBank() {
        sectionService.deleteStation(1L, 4L);

        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections).contains(first)
                .hasSize(1);
    }

    @DisplayName("구간의 사이에 있는 역을 삭제할 수 있다.")
    @Test
    void deleteBetween() {
        sectionService.deleteStation(1L, 3L);

        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections).contains(new Section(1L, 2L, 4L, 16))
                .hasSize(1);
    }
}
