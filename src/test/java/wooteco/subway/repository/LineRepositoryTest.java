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

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class LineRepositoryTest {

    private LineRepository lineRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    private LineDao lineDao;
    private SectionDao sectionDao;
    private StationDao stationDao;
    private long testLindId;
    private long upStationId;
    private long downStationId;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(dataSource);
        sectionDao = new SectionDao(dataSource);
        stationDao = new StationDao(dataSource);
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

        testLindId = lineRepository.save(new Line("1호선", "black"));
        upStationId = stationDao.save(new Station("천호역"));
        downStationId = stationDao.save(new Station("강남역"));
    }

    @DisplayName("id로 노선 1개를 조회한다.")
    @Test
    void findById() {
        Station upStation = stationDao.findById(upStationId).get();
        Station downStation = stationDao.findById(downStationId).get();
        Section section = new Section(upStation, downStation, 10, testLindId);
        long sectionId = sectionDao.save(section);

        Line savedLine = lineRepository.findById(testLindId);
        List<Section> mockSections = Arrays.asList(new Section(sectionId, upStation, downStation, 10, testLindId));
        Line mockLine = new Line(testLindId, "1호선", "black", new Sections(mockSections));

        assertThat(savedLine).isEqualTo(mockLine);
    }

    @DisplayName("전체 노선 목록을 조회한다.")
    @Test
    void findAll() {
        long lastStationId = stationDao.save(new Station("의정부역"));
        Station upStation = stationDao.findById(upStationId).get();
        Station downStation = stationDao.findById(downStationId).get();
        Station lastStation = stationDao.findById(lastStationId).get();

        Section firstSection = new Section(upStation, downStation, 10, testLindId);
        Section secondSection = new Section(downStation, lastStation, 10, testLindId);
        long firstSectionId = sectionDao.save(firstSection);
        long secondSectionId = sectionDao.save(secondSection);

        Line savedLine = lineRepository.findById(testLindId);
        List<Section> mockSections = Arrays.asList(new Section(firstSectionId, upStation, downStation, 10, testLindId),
                new Section(secondSectionId, downStation, lastStation, 10, testLindId));
        Line mockLine = new Line(testLindId, "1호선", "black", new Sections(mockSections));

        assertThat(savedLine).isEqualTo(mockLine);
    }
}
