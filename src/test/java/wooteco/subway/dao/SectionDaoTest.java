package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    @BeforeEach
    void beforeEach() {
        sectionDao = new SectionDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 노선을 저장할 수 있다.")
    void insert() {
        Section section = sectionDao.insert(new Section(1L, 1L, 2L, 10));

        assertThat(section.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("지하철 구간을 노선 식별자별로 조회할 수 있다.")
    void findByLineId() {
        sectionDao.insert(new Section(1L, 1L, 2L, 10));
        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections.get(0).getDistance()).isEqualTo(10);
    }
}
