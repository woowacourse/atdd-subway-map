package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.SectionDto;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@Sql("/testSchema.sql")
public class SectionRepositoryTest {

    @Autowired
    private SectionRepository sectionRepository;

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    @Autowired
    public SectionRepositoryTest(JdbcTemplate jdbcTemplate) {
        this.lineDao = new JdbcLineDao(jdbcTemplate);
        this.sectionDao = new JdbcSectionDao(jdbcTemplate);
        this.stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @DisplayName("구간 저장")
    @Test
    void 구간_저장() {
        Station A = stationDao.save(new Station("A역"));
        Station B = stationDao.save(new Station("B역"));
        LineDto line = lineDao.save(new LineDto("A호선", "red"));
        Section section = new Section(A, B, 10);

        Section result = sectionRepository.save(line.getId(),
                new SectionRequest(section.getUp().getId(), section.getDown().getId(), section.getDistance()));

        assertAll(
                () -> assertThat(result.getUp()).isEqualTo(A),
                () -> assertThat(result.getDown()).isEqualTo(B),
                () -> assertThat(result.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("단일 구간 조회")
    @Test
    void 구간_조회() {
        Station A = stationDao.save(new Station("A역"));
        Station B = stationDao.save(new Station("B역"));
        LineDto line = lineDao.save(new LineDto("A호선", "red"));
        SectionDto saved = sectionDao.save(new SectionDto(line.getId(), A.getId(), B.getId(), 10));

        Section result = sectionRepository.findById(saved.getId());

        assertAll(
                () -> assertThat(result.getUp()).isEqualTo(A),
                () -> assertThat(result.getDown()).isEqualTo(B),
                () -> assertThat(result.getDistance()).isEqualTo(10)
        );
    }
}
