package wooteco.subway.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SectionDaoTest {

    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;

    private Station station1;
    private Station station2;
    private Station station3;

    public SectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void set() {
        sectionDao = new SectionDao(jdbcTemplate.getDataSource());
        stationDao = new StationDao(jdbcTemplate.getDataSource());
        lineDao = new LineDao(jdbcTemplate.getDataSource());
        station1 = stationDao.save("강남역");
        station2 = stationDao.save("선릉역");
        station3 =  stationDao.save("판교역");
    }

    @Test
    @DisplayName("최초 구간을 저장한다.")
    void saveInitialSection() {
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        lineDao.save(lineRequest);

        Section section = sectionDao.saveInitialSection(lineRequest, 1L);
        long actualUpStationId = section.getUpStationId();
        long actualDownStationId = section.getDownStationId();
        int actualDistance = section.getDistance();

        assertThat(actualUpStationId).isEqualTo(station1.getId());
        assertThat(actualDownStationId).isEqualTo(station2.getId());
        assertThat(actualDistance).isEqualTo(10);
    }

    @Test
    @DisplayName("구간을 저장한다.")
    void save() {
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        Section section = new Section(line.getId(), station1.getId(), station2.getId(), 10);

        Section actual = sectionDao.save(section);
        long actualUpStationId = actual.getUpStationId();
        long actualDownStationId = actual.getDownStationId();
        int actualDistance = actual.getDistance();

        assertThat(actualUpStationId).isEqualTo(station1.getId());
        assertThat(actualDownStationId).isEqualTo(station2.getId());
        assertThat(actualDistance).isEqualTo(10);
    }

    @Test
    @DisplayName("저장할 구간과 상행역이나 하행역이 같은 구간을 반환한다.")
    void findBySameUpOrDownStation() {
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        Section section = new Section(line.getId(), station1.getId(), station2.getId(), 10);
        Section savedSection = sectionDao.save(section);

        Section newSection = new Section(line.getId(), station1.getId(), station3.getId(), 10);
        Section actual = sectionDao.findBySameUpOrDownStation(newSection).get();

        assertThat(actual).isEqualTo(savedSection);
    }

    @Test
    @DisplayName("노선내의 구간을 검색한다.")
    void findByLine() {
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), station1.getId(), station2.getId(), 10);
        Section savedSection1 = sectionDao.save(section1);
        Section section2 = new Section(line.getId(), station2.getId(), station3.getId(), 10);
        Section savedSection2 = sectionDao.save(section2);

        List<Section> actual = sectionDao.findByLine(line.getId()).get();

        assertThat(actual).hasSize(2);
        assertThat(actual).contains(savedSection1, savedSection2);
    }

    @Test
    @DisplayName("상행역 ID로 구간을 검색한다.")
    void findByUpStationId() {
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), station1.getId(), station2.getId(), 10);
        Section savedSection = sectionDao.save(section1);

        Section actual = sectionDao.findByUpStationId(station1.getId(), line.getId()).get();

        assertThat(actual).isEqualTo(savedSection);
    }

    @Test
    @DisplayName("하행역 ID로 구간을 검색한다.")
    void findByDownStationId() {
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), station1.getId(), station2.getId(), 10);
        Section savedSection = sectionDao.save(section1);

        Section actual = sectionDao.findByDownStationId(station2.getId(), line.getId()).get();

        assertThat(actual).isEqualTo(savedSection);
    }

    @Test
    @DisplayName("하행역을 변경한다.")
    void updateDownStation() {
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), station1.getId(), station3.getId(), 10);
        Section findSection = sectionDao.save(section1);
        Section newSection =  new Section(line.getId(), station2.getId(), station3.getId(), 5);

        sectionDao.updateDownStation(findSection,newSection);
        Section updatedSection = sectionDao.findById(findSection.getId()).get();

        assertThat(updatedSection.getDownStationId()).isEqualTo(station2.getId());
    }

    @Test
    @DisplayName("상행역을 변경한다.")
    void updateUpStation() {
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), station1.getId(), station3.getId(), 10);
        Section findSection = sectionDao.save(section1);
        Section newSection =  new Section(line.getId(), station1.getId(), station2.getId(), 5);

        sectionDao.updateUpStation(findSection,newSection);
        Section updatedSection = sectionDao.findById(findSection.getId()).get();

        assertThat(updatedSection.getUpStationId()).isEqualTo(station2.getId());
    }

    @Test
    @DisplayName("거리를 변경한다.")
    void updateDistance() {
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), station1.getId(), station3.getId(), 10);
        Section findSection = sectionDao.save(section1);
        Section newSection =  new Section(line.getId(), station1.getId(), station2.getId(), 5);

        sectionDao.updateDistance(findSection,newSection);
        Section updatedSection = sectionDao.findById(findSection.getId()).get();

        assertThat(updatedSection.getDistance()).isEqualTo(5);
    }

}
