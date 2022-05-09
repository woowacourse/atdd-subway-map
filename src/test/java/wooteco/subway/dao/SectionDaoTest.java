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
import wooteco.subway.domain.Station;

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
        final Station station1 = new Station(1L, "아차산역");
        final Station station2 = new Station(2L, "군자역");
        final Line savedLine = lineDao.save(new Line("2호선", "bg-green-600"));
        final Section section = new Section(station1, station2, 10, savedLine.getId());
        final Section savedSection = sectionDao.save(section);

        assertAll(
                () -> assertThat(savedSection.getId()).isNotNull(),
                () -> assertThat(savedSection.getUpStation()).isEqualTo(section.getUpStation()),
                () -> assertThat(savedSection.getDownStation()).isEqualTo(section.getDownStation())
        );
    }
}
