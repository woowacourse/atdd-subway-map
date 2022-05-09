package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("지하철 노선 구간을 저장한다.")
    @Test
    void save() {
        Section section = new Section(10, 1L, 1L, 3L);
        Section savedSection = sectionDao.save(section);

        assertThat(section.getDistance()).isEqualTo(savedSection.getDistance());
    }
}
