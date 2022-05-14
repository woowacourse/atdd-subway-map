package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.SectionDto;

@JdbcTest
class JdbcSectionDaoTest {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    @Autowired
    public JdbcSectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new JdbcSectionDao(jdbcTemplate);
        this.stationDao = new JdbcStationDao(jdbcTemplate);
        this.lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @DisplayName("구간 저장")
    @Test
    void 구간_저장() {
        Station up = stationDao.save(new Station("합정역"));
        Station down = stationDao.save(new Station("홍대입구역"));
        Section section = new Section(up, down, 1);
        Line line = new Line("2호선", "green", new Sections(section));
        LineDto savedId = lineDao.save(LineDto.from(line));

        SectionDto expect = SectionDto.of(section, savedId.getId());
        SectionDto result = sectionDao.save(expect);

        assertAll(
                () -> assertThat(result.getLineId()).isEqualTo(expect.getLineId()),
                () -> assertThat(result.getUpStationId()).isEqualTo(expect.getUpStationId()),
                () -> assertThat(result.getDownStationId()).isEqualTo(expect.getDownStationId()),
                () -> assertThat(result.getDistance()).isEqualTo(expect.getDistance())
        );
    }

    @DisplayName("노선 id로 구간 조회")
    @Test
    void 노선_id_모든_구간_조회() {
        Station 합정역 = stationDao.save(new Station("합정역"));
        Station 홍대입구역 = stationDao.save(new Station("홍대입구역"));
        Station 신촌역 = stationDao.save(new Station("신촌역"));

        Section included1 = new Section(합정역, 홍대입구역, 1);
        Section included2 = new Section(홍대입구역, 신촌역, 1);

        Line line = new Line("2호선", "green",
                new Sections(new LinkedList<>(List.of(included1, included2))));
        LineDto savedLine = lineDao.save(LineDto.from(line));

        sectionDao.save(SectionDto.of(included1, savedLine.getId()));
        sectionDao.save(SectionDto.of(included2, savedLine.getId()));

        assertThat(sectionDao.findByLineId(savedLine.getId()).size()).isEqualTo(2);
    }
    
    @DisplayName("구간_삭제")
    @Test
    void 구간_삭제() {
        Station up = stationDao.save(new Station("합정역"));
        Station down = stationDao.save(new Station("홍대입구역"));
        Section section = new Section(up, down, 1);
        Line line = new Line("2호선", "green", new Sections(section));
        LineDto savedId = lineDao.save(LineDto.from(line));

        SectionDto savedSection = sectionDao.save(SectionDto.of(section, savedId.getId()));

        sectionDao.delete(savedSection.getId());

        assertThatThrownBy(() -> sectionDao.findById(savedSection.getId()))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}