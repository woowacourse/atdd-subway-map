package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(dataSource);
        stationDao = new StationDao(dataSource);
        sectionDao = new SectionDao(dataSource);
    }

    @DisplayName("노선과 구간 정보를 받아서 저장한다.")
    @Test
    void save() {
        Line line = lineDao.save(new Line("2호선", "green"));
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("역삼역"));

        Section section = new Section(upStation, downStation, 1);

        Section persistSection = sectionDao.save(line, section);

        assertThat(persistSection).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(section);
    }

}
