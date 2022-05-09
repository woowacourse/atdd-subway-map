package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

public class SectionDaoImplTest extends DaoImplTest {

    private SectionDaoImpl sectionDaoImpl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDaoImpl = new SectionDaoImpl(jdbcTemplate);
    }

    @DisplayName("구간 정보를 저장한다.")
    @Test
    void save() {
        Section section = new Section(1L, 1L, 2L, 12);
        Section newSection = sectionDaoImpl.save(section);

        assertThat(newSection.getLineId()).isEqualTo(1L);
        assertThat(newSection.getUpStationId()).isEqualTo(1L);
        assertThat(newSection.getDownStationId()).isEqualTo(2L);
        assertThat(newSection.getDistance()).isEqualTo(12);
    }
}
