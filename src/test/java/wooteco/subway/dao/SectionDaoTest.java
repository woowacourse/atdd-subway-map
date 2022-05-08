package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixtures.SECTION;
import static wooteco.subway.Fixtures.SECTION_2;
import static wooteco.subway.Fixtures.getSection;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
class SectionDaoTest {
    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("지하철 구간을 저장한다.")
    @Test
    void save() {
        Long id = sectionDao.save(SECTION);
        assertThat(sectionDao.findById(id))
                .isEqualTo(getSection(id, SECTION));
    }

    @DisplayName("해당 지하철 노선 id의 지하철 구간들을 조회한다.")
    @Test
    void findAllByLineId() {
        //given
        Long id = sectionDao.save(SECTION);
        Long id2 = sectionDao.save(SECTION_2);

        //when
        List<Section> sections = sectionDao.findAllByLineId(SECTION.getLineId());

        //then
        assertThat(sections)
                .containsOnly(getSection(id, SECTION), getSection(id2, SECTION_2));
    }

    @DisplayName("해당 지하철 노선 upStationId의 지하철 구간들을 조회한다.")
    @Test
    void findByUpStationId() {
        Long id = sectionDao.save(SECTION);
        Section section = sectionDao.findByUpStationId(SECTION.getLineId(),SECTION.getUpStationId());
        assertThat(section)
                .isEqualTo(getSection(id,SECTION));
    }

    @DisplayName("해당 지하철 노선 downStationId의 지하철 구간들을 조회한다.")
    @Test
    void findByDownStationId() {
        Long id = sectionDao.save(SECTION);
        Section section = sectionDao.findByDownStationId(SECTION.getLineId(),SECTION.getDownStationId());
        assertThat(section)
                .isEqualTo(getSection(id,SECTION));
    }

    @DisplayName("해당 지하철 노선 upStationId의 지하철 구간이 있는지 확인한다.")
    @Test
    void hasUpStationId() {
        sectionDao.save(SECTION);
        assertThat(sectionDao.hasUpStationId(SECTION))
                .isTrue();
        assertThat(sectionDao.hasUpStationId(SECTION_2))
                .isFalse();
    }

    @DisplayName("해당 지하철 노선 downStationId 지하철 구간이 있는지 확인한다.")
    @Test
    void hasDownStationId() {
        sectionDao.save(SECTION);
        assertThat(sectionDao.hasDownStationId(SECTION))
                .isTrue();
        assertThat(sectionDao.hasDownStationId(SECTION_2))
                .isFalse();
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void delete() {
        //given
        Long id = sectionDao.save(SECTION);
        assertThat(sectionDao.findById(id))
                .isNotNull();

        //when
        sectionDao.delete(id);

        //then
        assertThatThrownBy(() -> sectionDao.findById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
