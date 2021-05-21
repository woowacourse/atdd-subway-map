package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.StationDao;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({LineDao.class, SectionDao.class, StationDao.class})
class LineDaoTest {
    private static final String LINENAME1 = "2호선";
    private static final String LINENAME2 = "9호선";
    private static final String COLOR1 = "초록색";
    private static final String COLOR2 = "남색";

    @Autowired
    private LineDao lineDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("SET foreign_key_checks=0;");
        jdbcTemplate.execute("truncate table LINE");
        jdbcTemplate.execute("alter table LINE alter column ID restart with 1");
        jdbcTemplate.execute("SET foreign_key_checks=1;");
        jdbcTemplate.execute("truncate table SECTION");
        jdbcTemplate.execute("alter table SECTION alter column ID restart with 1");
        jdbcTemplate.execute("truncate table STATION");
        jdbcTemplate.execute("alter table STATION alter column ID restart with 1");
        jdbcTemplate.update("insert into LINE(name, color) values (?, ?)", LINENAME1, COLOR1);
    }

    @Test
    @DisplayName("노선 생성 확인")
    public void createLine() {
        Line savedLine = lineDao.save(LINENAME2, COLOR2);
        assertThat(savedLine.getName()).isEqualTo(LINENAME2);
        assertThat(savedLine.getColor()).isEqualTo(COLOR2);
    }

    @Test
    @DisplayName("ID로 노선 검색")
    void findById() {
        stationDao.save("가양역");
        stationDao.save("증미역");
        Section section = sectionDao.save(1L, 1L, 2L, 10);
        Line findLine = lineDao.findById(1L).get();
        assertThat(findLine.getId()).isEqualTo(1L);
        assertThat(findLine.getSections()).containsExactlyInAnyOrder(section);
        assertThat(findLine.getName()).isEqualTo(LINENAME1);
        assertThat(findLine.getColor()).isEqualTo(COLOR1);
    }

    @Test
    @DisplayName("이름으로 노선 검색")
    void findByName() {
        stationDao.save("가양역");
        stationDao.save("증미역");
        Section section = sectionDao.save(1L, 1L, 2L, 10);
        Line findLine = lineDao.findByName(LINENAME1).get();
        assertThat(findLine.getId()).isEqualTo(1L);
        assertThat(findLine.getSections()).containsExactlyInAnyOrder(section);
        assertThat(findLine.getName()).isEqualTo(LINENAME1);
        assertThat(findLine.getColor()).isEqualTo(COLOR1);
    }

    @Test
    @DisplayName("모든 노선 검색")
    void findAll() {
        stationDao.save("가양역");
        stationDao.save("증미역");
        sectionDao.save(1L, 1L, 2L, 10);
        Line findLine = lineDao.findByName(LINENAME1).get();

        lineDao.save(LINENAME2, COLOR2);
        stationDao.save("등촌역");
        stationDao.save("염창역");
        sectionDao.save(2L, 3L, 4L, 10);
        Line findLine2 = lineDao.findByName(LINENAME2).get();
        List<Line> lines = lineDao.findAll();

        assertThat(lines).containsExactlyInAnyOrderElementsOf(Arrays.asList(findLine, findLine2));
    }

    @Test
    @DisplayName("노선 정보 수정")
    void update() {
        Line findLine = lineDao.findByName(LINENAME1).get();
        lineDao.update(findLine.getId(), LINENAME2, COLOR2);
        Line updatedLine = lineDao.findById(findLine.getId()).get();

        assertThat(LINENAME1).isEqualTo(findLine.getName());
        assertThat(LINENAME2).isEqualTo(updatedLine.getName());
    }

    @Test
    @DisplayName("노선 정보 삭제")
    void delete() {
        Line savedLine = lineDao.save(LINENAME2, COLOR2);
        assertThat(lineDao.findByName(savedLine.getName()).isPresent()).isTrue();

        lineDao.delete(savedLine.getId());
        assertThat(lineDao.findByName(savedLine.getName()).isPresent()).isFalse();
    }
}