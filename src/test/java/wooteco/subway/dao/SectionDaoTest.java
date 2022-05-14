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
@SuppressWarnings("NonAsciiCharacters")
public class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    private SectionDao sectionDao;

    private Station 선릉역;
    private Station 선정릉역;
    private Station 한티역;
    private Line 분당선;
    private Section savedSection;

    @BeforeEach
    void setUp() {
        StationDao stationDao = new StationDao(jdbcTemplate, dataSource);
        LineDao lineDao = new LineDao(jdbcTemplate, dataSource);
        sectionDao = new SectionDao(jdbcTemplate, dataSource);

        선릉역 = stationDao.insert(new Station("선릉역"));
        선정릉역 = stationDao.insert(new Station("선정릉역"));
        한티역 = stationDao.insert(new Station("한티역"));

        분당선 = lineDao.insert(new Line("분당선", "yellow"));
        savedSection = sectionDao.insert(new Section(분당선.getId(), 선릉역.getId(), 선정릉역.getId(), 10));
    }


    @DisplayName("구간 정보를 등록한다.")
    @Test
    void insert() {
        int distance = 10;
        Section newSavedSection = sectionDao.insert(new Section(분당선.getId(), 선정릉역.getId(), 한티역.getId(), distance));

        assertAll(
                () -> assertThat(newSavedSection.getLineId()).isEqualTo(분당선.getId()),
                () -> assertThat(newSavedSection.getUpStationId()).isEqualTo(선정릉역.getId()),
                () -> assertThat(newSavedSection.getDownStationId()).isEqualTo(한티역.getId()),
                () -> assertThat(newSavedSection.getDistance()).isEqualTo(distance)
        );
    }

    @DisplayName("구간 아이디를 통해 구간 정보를 읽어온다.")
    @Test
    void findById() {
        Section foundSection = sectionDao.findById(savedSection.getId());

        assertThat(savedSection).isEqualTo(foundSection);
    }

    @DisplayName("노선 id에 해당하는 모든 구간 정보를 불러온다.")
    @Test
    void findAllByLineId() {
        Section newSavedSection = sectionDao.insert(new Section(분당선.getId(), 선정릉역.getId(), 한티역.getId(), 10));

        List<Section> sections = sectionDao.findAllByLineId(분당선.getId());

        assertThat(sections).containsExactly(savedSection, newSavedSection);
    }

    @DisplayName("구간 정보를 업데이트한다.")
    @Test
    void update() {
        Section changedSection = new Section(savedSection.getId(), savedSection.getLineId(), 한티역.getId(), savedSection.getDownStationId(), 10);
        sectionDao.update(changedSection);

        Section updatedSection = sectionDao.findById(savedSection.getId());
        assertThat(changedSection).isEqualTo(updatedSection);
    }

    @DisplayName("구간 정보를 삭제한다.")
    @Test
    void delete() {
        sectionDao.deleteById(savedSection.getId());

        List<Section> sections = sectionDao.findAllByLineId(분당선.getId());
        assertThat(sections).doesNotContain(savedSection);
    }
}
