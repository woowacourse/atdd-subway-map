package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
        Line line = lineDao.save(new Line("2호선", "초록색"));
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("역삼역"));

        Section section = new Section(upStation, downStation, 1);

        Section persistSection = sectionDao.save(line, section);

        assertThat(persistSection).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(section);
    }

    @DisplayName("노선을 받아서 구간을 조회한다.")
    @Test
    void findByLineId() {
        Line line = lineDao.save(new Line("2호선", "초록색"));
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("역삼역"));
        Section section = new Section(upStation, downStation, 1);
        sectionDao.save(line, section);

        Section persistSection = sectionDao.findAllByLine(line).get(0);

        assertThat(persistSection).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(section);
    }

    @DisplayName("노선 구간 중 해당하는 역을 포함하는 구간을 모두 제거한다.")
    @Test
    void delete() {
        Line line = lineDao.save(new Line("2호선", "초록색"));
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("역삼역"));
        Station station3 = stationDao.save(new Station("선릉역"));
        Section section1 = new Section(station1, station2, 1);
        Section section2 = new Section(station2, station3, 1);
        sectionDao.save(line, section1);
        sectionDao.save(line, section2);

        sectionDao.deleteByLineAndStation(line, station3);

        List<Section> sections = sectionDao.findAllByLine(line);

        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.get(0)).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(section1);
    }

    @DisplayName("노선에 이미 등록되어 있는 구간인지 확인한다.")
    @Test
    void checkExist() {
        Line line = lineDao.save(new Line("2호선", "초록색"));
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("역삼역"));
        Section section = new Section(upStation, downStation, 1);
        sectionDao.save(line, section);

        boolean actual = sectionDao.exists(line, section);

        assertThat(actual).isTrue();
    }

}
