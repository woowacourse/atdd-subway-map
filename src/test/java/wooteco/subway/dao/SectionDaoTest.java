package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import javax.sql.DataSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;
    private LineDao lineDao;
    private SectionDao sectionDao;

    private Station savedStation1;
    private Station savedStation2;
    private Station savedStation3;
    private Station savedStation4;

    private Line savedLine1;

    private

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate, dataSource);
        lineDao = new LineDao(jdbcTemplate, dataSource);
        sectionDao = new SectionDao(jdbcTemplate, dataSource);

        savedStation1 = stationDao.save(new Station("선릉역"));
        savedStation2 = stationDao.save(new Station("선정릉역"));
        savedStation3 = stationDao.save(new Station("한티역"));
        savedStation4 = stationDao.save(new Station("도곡역"));

        savedLine1 = lineDao.save(new Line("분당선", "yellow"));
    }


    @DisplayName("구간 정보를 등록한다.")
    @Test
    void insert() {
        int distance = 10;
        Section savedSection = sectionDao.insert(new Section(savedLine1.getId(), savedStation1.getId(), savedStation2.getId(), distance));

        assertAll(
                () -> assertThat(savedSection.getLineId()).isEqualTo(savedLine1.getId()),
                () -> assertThat(savedSection.getUpStationId()).isEqualTo(savedStation1.getId()),
                () -> assertThat(savedSection.getDownStationId()).isEqualTo(savedStation2.getId()),
                () -> assertThat(savedSection.getDistance()).isEqualTo(distance)
        );
    }

    @DisplayName("구간 아이디를 통해 구간 정보를 읽어온다.")
    @Test
    void findById() {
        Section savedSection = sectionDao.insert(new Section(savedLine1.getId(), savedStation1.getId(), savedStation2.getId(), 10));
        Section foundSection = sectionDao.findById(savedSection.getId());

        assertThat(savedSection).isEqualTo(foundSection);
    }

    @DisplayName("구간 정보를 업데이트한다.")
    @Test
    void update() {
        Section savedSection = sectionDao.insert(new Section(savedLine1.getId(), savedStation1.getId(), savedStation2.getId(), 10));
        Section changedSection = new Section(savedSection.getId(), savedSection.getLineId(), savedStation3.getId(), savedSection.getDownStationId(), 10);
        sectionDao.update(changedSection);

        Section updatedSection = sectionDao.findById(savedSection.getId());
        assertThat(changedSection).isEqualTo(updatedSection);
    }

    @DisplayName("노선 id에 해당하는 모든 구간 정보를 불러온다.")
    @Test
    void findAll() {
        Section savedSection1 = sectionDao.insert(new Section(savedLine1.getId(), savedStation1.getId(), savedStation2.getId(), 10));
        Section savedSection2 = sectionDao.insert(new Section(savedLine1.getId(), savedStation2.getId(), savedStation3.getId(), 10));

        List<Section> sections = sectionDao.findAllByLineId(savedLine1.getId());

        assertThat(sections).containsExactly(savedSection1, savedSection2);
    }
}
