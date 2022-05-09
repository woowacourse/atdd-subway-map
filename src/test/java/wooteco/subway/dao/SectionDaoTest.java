package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@JdbcTest
@Transactional
class SectionDaoTest {

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    @Autowired
    private SectionDaoTest(final NamedParameterJdbcTemplate namedParameterJdbcTemplate, final JdbcTemplate jdbcTemplate) {
        this.sectionDao = new SectionDao(namedParameterJdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    void save() {
        final Line savedLine = lineDao.save(new Line("2호선", "bg-green-600"));
        final Section section = new Section(1L, 2L, 10, savedLine.getId());
        final Section savedSection = sectionDao.save(section);

        assertAll(
                () -> assertThat(savedSection.getId()).isNotNull(),
                () -> assertThat(savedSection.getUpStationId()).isEqualTo(section.getUpStationId()),
                () -> assertThat(savedSection.getDownStationId()).isEqualTo(section.getDownStationId())
        );
    }
}
