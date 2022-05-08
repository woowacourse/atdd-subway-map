package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    private final long lineId = 1L;

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
        Section section = new Section(lineId, 1L, 2L, 10);
        Long id = sectionDao.save(section);
        assertThat(sectionDao.findById(id))
                .isEqualTo(section);
    }

    @DisplayName("해당 지하철 노선 id의 지하철 구간들을 조회한다.")
    @Test
    void findAllByLineId() {
        //given
        Section section = new Section(lineId, 1L, 2L, 10);
        Section section2 = new Section(lineId, 2L, 3L, 10);

        sectionDao.save(section);
        sectionDao.save(section2);

        //when
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        //then
        assertThat(sections)
                .containsOnly(section, section2);
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void delete() {
        //given
        Section section = new Section(lineId, 1L, 2L, 10);
        Long id = sectionDao.save(section);
        assertThat(sectionDao.findById(id))
                .isNotNull();

        //when
        sectionDao.delete(id);

        //then
        assertThatThrownBy(() -> sectionDao.findById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
