package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Section;

@JdbcTest
public class SectionDaoTest {

    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("지하철 구간을 저장하고 찾는다.")
    @Test
    void save() {
        Section section = new Section(1L, 1L, 2L, 10);
        sectionDao.save(section);

        assertThat(sectionDao.findAllByLineId(1L)).hasSize(1);
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void delete() {
        Section section = new Section(1L, 1L, 2L, 10);
        sectionDao.save(section);

        sectionDao.delete(1L, 1L);

        assertThat(sectionDao.findAllByLineId(1L)).hasSize(0);
    }
}
