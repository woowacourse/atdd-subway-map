package wooteco.subway.line.section;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.UnitTest;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@DisplayName("구간 DAO 테스트")
class SectionDaoTest extends UnitTest {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final Station gangnamStation = new Station(1L, "강남역");
    private final Station jamsilStation = new Station(2L, "잠실역");
    private final Station yeoksamStation = new Station(3L, "역삼역");
    private final Station sillimStation = new Station(4L, "신림역");
    private final Line line2 = new Line(1L, "2호선", "green");
    private final Section defaultSection = new Section(1L, 1L, 4L, 10);

    public SectionDaoTest(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @BeforeEach
    void setUp() {
        lineDao.save(line2);
        stationDao.save(gangnamStation);
        stationDao.save(jamsilStation);
        stationDao.save(yeoksamStation);
        stationDao.save(sillimStation);
    }

    @Test
    @DisplayName("구간을 저장 한다")
    void save() {
        //given

        //when
        sectionDao.save(1L, defaultSection);

        //then
        assertThat(sectionDao.findById(1L, 1L))
            .usingRecursiveComparison()
            .isEqualTo(defaultSection);
    }

    @Test
    @DisplayName("LineId를 이용하여 삭제 한다")
    void deleteByLineId() {
        //given
        sectionDao.save(1L, defaultSection);
        Section section = new Section(2L, 4L, 3L, 1);
        sectionDao.save(1L, section);

        //when
        sectionDao.deleteByLineId(1L);

        //then
        assertThat(sectionDao.findByLineId(1L)).hasSize(0);
    }

    @Test
    @DisplayName("Line에 속해있는 구간 조회한다")
    void findByLineId() {
        //given
        sectionDao.save(1L, defaultSection);
        Section section = new Section(2L, 4L, 3L, 1);
        sectionDao.save(1L, section);

        //when
        List<Section> sections = sectionDao.findByLineId(1L);

        //then
        assertThat(sections).hasSize(2)
            .usingRecursiveComparison()
            .isEqualTo(Arrays.asList(defaultSection, section));

    }

    @Test
    @DisplayName("구간 내용을 수정한다")
    void update() {
        //given
        sectionDao.save(1L, defaultSection);
        Section section = new Section(1L, 1L, 2L, 10);

        //when
        sectionDao.update(1L, section);

        //then
        assertThat(sectionDao.findById(1L, 1L))
            .usingRecursiveComparison()
            .isEqualTo(section);
    }

    @Test
    @DisplayName("id를 이용한 구간 조회 한다")
    void findById() {
        //given
        sectionDao.save(1L, defaultSection);

        //when
        Section section = sectionDao.findById(1L, 1L);

        //then
        assertThat(section).usingRecursiveComparison().isEqualTo(section);
    }

    @Test
    @DisplayName("id를 이용하여 구간을 삭제한다")
    void deleteById() {
        //given
        sectionDao.save(1L, defaultSection);
        Section section = new Section(2L, 4L, 3L, 1);
        sectionDao.save(1L, section);

        //when
        sectionDao.deleteById(1L, 2L);

        //then
        assertThat(sectionDao.findByLineId(1L)).hasSize(1)
            .usingRecursiveComparison()
            .isEqualTo(Collections.singletonList(defaultSection));
    }
}