package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.ui.dto.SectionRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("구간 전체 조회")
    @Test
    void findAll() {
        // given

        // when
        List<Section> sections = sectionDao.findAll();

        // then
        assertThat(sections.size()).isEqualTo(1);
    }

    @DisplayName("구간을 노선 id로 검색")
    @Test
    void findByLineId() {
        // given

        // when
        List<Section> sections = sectionDao.findByLineId(1L);

        // then
        assertThat(sections.size()).isEqualTo(1);
    }

    @DisplayName("구간 역 id로 조회")
    @Test
    void findByStationId() {
        // given

        // when
        List<Section> sections = sectionDao.findByStationId(2L);

        // then
        assertThat(sections.size()).isEqualTo(1);
    }

    @DisplayName("구간 역 id와 노선 id로 조회")
    @Test
    void findByLineIdAndStationId() {
        // given

        // when
        List<Section> sections = sectionDao.findByLineIdAndStationId(1L, 2L);

        // then
        assertThat(sections.size()).isEqualTo(1);
    }

    @DisplayName("구간 생성")
    @Test
    void save() {
        // given
        SectionRequest section = new SectionRequest(1L, 2L, 10);

        // when
        Long id = sectionDao.save(section.toEntity(1L));

        // then
        assertThat(id).isEqualTo(2L);
    }

    @DisplayName("구간 수정")
    @Test
    void update() {
        // given
        Long id = 1L;
        Section updateSection = new Section(id, 1L, 2L, 1L, 30);

        // when
        sectionDao.update(updateSection);

        // then
        Section section = sectionDao.findByLineId(1L)
                .stream()
                .filter(it -> id.equals(it.getId()))
                .findAny()
                .orElseThrow();

        assertThat(updateSection).isEqualTo(section);
    }

    @DisplayName("구간 노선 id로 삭제")
    @Test
    void deleteByLineId() {
        // given
        Long lineId = 1L;

        // when
        sectionDao.deleteByLineId(lineId);

        // then
        assertThat(sectionDao.findByLineId(lineId).size()).isEqualTo(0);
    }

    @DisplayName("구간 id로 삭제")
    @Test
    void deleteById() {
        // given
        Long id = 1L;

        // when
        sectionDao.deleteById(id);

        // then
        List<Section> sameSectionIds = sectionDao.findAll()
                .stream()
                .filter(section -> id.equals(section.getId()))
                .collect(Collectors.toList());

        assertThat(sameSectionIds.size()).isEqualTo(0);
    }
}