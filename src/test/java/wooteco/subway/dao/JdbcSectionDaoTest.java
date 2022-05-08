package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionDto;

@JdbcTest
class JdbcSectionDaoTest {

    private final SectionDao sectionDao;

    @Autowired
    public JdbcSectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @DisplayName("구간 저장")
    @Test
    void 구간_저장() {
        Section section = new Section(
                new Station(1L,"합정역"),
                new Station(2L,"홍대입구역"), 1);
        Line line = new Line(1L, "2호선", "bg-green-600");

        SectionDto expect = SectionDto.from(section, line);
        SectionDto result = sectionDao.save(expect);

        assertAll(
                () -> assertThat(result.getLineId()).isEqualTo(expect.getLineId()),
                () -> assertThat(result.getUpStationId()).isEqualTo(expect.getUpStationId()),
                () -> assertThat(result.getDownStationId()).isEqualTo(expect.getDownStationId()),
                () -> assertThat(result.getDistance()).isEqualTo(expect.getDistance())
        );
    }
}