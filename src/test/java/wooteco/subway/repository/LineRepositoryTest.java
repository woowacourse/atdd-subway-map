package wooteco.subway.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class LineRepositoryTest {

    private LineRepository lineRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;
    private SectionDao sectionDao;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
        sectionDao = new SectionDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
        lineRepository = new LineRepository(lineDao, sectionDao, stationDao);
        String sectionSchemaQuery = "create table if not exists SECTION ( id bigint auto_increment not null, " +
                "line_id bigint not null, up_station_id bigint not null, down_station_id bigint not null, " +
                "distance int, primary key(id) )";
        String lineSchemaQuery = "create table if not exists LINE (id bigint auto_increment not null, name varchar(255) " +
                "not null unique, color varchar(20) not null, primary key(id))";
        String stationSchemaQuery = "create table if not exists STATION ( id bigint auto_increment not null, nam varchar(255) " +
                "not null unique, primary key(id))";
        jdbcTemplate.execute(lineSchemaQuery);
        jdbcTemplate.execute(sectionSchemaQuery);
        jdbcTemplate.execute(stationSchemaQuery);
    }

    @DisplayName("id로 노선을 조회한다.")
    @Test
    void findById() {
        long upStationId = stationDao.save(new Station("천호역"));
        long downStationId = stationDao.save(new Station("강남역"));
        Station upStation = stationDao.findById(upStationId).get();
        Station downStation = stationDao.findById(downStationId).get();
        Line line = new Line("1호선", "black");
        long id = lineRepository.save(line);
        Section section = new Section(upStation, downStation, 10, id);
        long sectionId = sectionDao.save(section);


        Line savedLine = lineRepository.findById(id);
        List<Section> comparedSections = Arrays.asList(new Section(sectionId, upStation, downStation, 10, id));
        Line comparingLine = new Line(id, "1호선", "black", new Sections(comparedSections));
        assertThat(savedLine).isEqualTo(comparingLine);
    }
}
