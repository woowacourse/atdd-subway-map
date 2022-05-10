package wooteco.subway.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SectionDaoTest {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;


    public SectionDaoTest(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @BeforeEach
    void set() {
        stationDao.save("강남역");
        stationDao.save("선릉역");
        stationDao.save("판교역");
        stationDao.save("정자역");
    }

    @AfterEach
    void reset() {
        sectionDao.deleteAll();
    }

    @Test
    @DisplayName("최초 구간을 저장한다.")
    void saveInitialSection() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        lineDao.save(lineRequest);

        Section section = sectionDao.saveInitialSection(lineRequest, 1L);
        long actualUpStationId = section.getUpStationId();
        long actualDownStationId = section.getDownStationId();
        int actualDistance = section.getDistance();

        assertThat(actualUpStationId).isEqualTo(1L);
        assertThat(actualDownStationId).isEqualTo(2L);
        assertThat(actualDistance).isEqualTo(10);
    }

    @Test
    @DisplayName("구간을 저장한다.")
    void save() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        Section section = new Section(line.getId(), 1L, 2L, 10);

        Section actual = sectionDao.save(section);
        long actualUpStationId = actual.getUpStationId();
        long actualDownStationId = actual.getDownStationId();
        int actualDistance = actual.getDistance();

        assertThat(actualUpStationId).isEqualTo(1L);
        assertThat(actualDownStationId).isEqualTo(2L);
        assertThat(actualDistance).isEqualTo(10);
    }

    @Test
    @DisplayName("저장할 구간과 상행역이나 하행역이 같은 구간을 반환한다.")
    void findBySameUpOrDownStation() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        Section section = new Section(line.getId(), 1L, 2L, 10);
        Section savedSection = sectionDao.save(section);

        Section newSection = new Section(line.getId(), 1L, 3L, 10);
        Section actual = sectionDao.findBySameUpOrDownStation(newSection).get();

        assertThat(actual).isEqualTo(savedSection);
    }

    @Test
    void findByLine() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), 1L, 2L, 10);
        Section savedSection1 = sectionDao.save(section1);
        Section section2 = new Section(line.getId(), 2L, 3L, 10);
        Section savedSection2 = sectionDao.save(section2);

        List<Section> actual = sectionDao.findByLine(line.getId()).get();

        assertThat(actual).hasSize(2);
        assertThat(actual).contains(savedSection1, savedSection2);
    }

    @Test
    void findByUpStationId() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), 1L, 2L, 10);
        Section savedSection = sectionDao.save(section1);

        Section actual = sectionDao.findByUpStationId(1L, line.getId()).get();

        assertThat(actual).isEqualTo(savedSection);
    }

    @Test
    void findByDownStationId() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), 1L, 2L, 10);
        Section savedSection = sectionDao.save(section1);

        Section actual = sectionDao.findByDownStationId(2L, line.getId()).get();

        assertThat(actual).isEqualTo(savedSection);
    }

    @Test
    void updateDownStation() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), 1L, 3L, 10);
        Section findSection = sectionDao.save(section1);
        Section newSection =  new Section(line.getId(), 2L, 3L, 5);

        sectionDao.updateDownStation(findSection,newSection);
        Section updatedSection = sectionDao.findById(findSection.getId()).get();

        assertThat(updatedSection.getDownStationId()).isEqualTo(2L);
    }

    @Test
    void updateUpStation() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), 1L, 3L, 10);
        Section findSection = sectionDao.save(section1);
        Section newSection =  new Section(line.getId(), 1L, 2L, 5);

        sectionDao.updateUpStation(findSection,newSection);
        Section updatedSection = sectionDao.findById(findSection.getId()).get();

        assertThat(updatedSection.getUpStationId()).isEqualTo(2L);
    }

    @Test
    void updateDistance() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        Section section1 = new Section(line.getId(), 1L, 3L, 10);
        Section findSection = sectionDao.save(section1);
        Section newSection =  new Section(line.getId(), 1L, 2L, 5);

        sectionDao.updateDistance(findSection,newSection);
        Section updatedSection = sectionDao.findById(findSection.getId()).get();

        assertThat(updatedSection.getDistance()).isEqualTo(5);
    }

}
