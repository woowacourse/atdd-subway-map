package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@JdbcTest
@Sql("/schema.sql")
class LineServiceTest {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineService lineService;

    @Autowired
    public LineServiceTest(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.stationDao = new StationDao(jdbcTemplate, dataSource);
        this.sectionDao = new SectionDao(jdbcTemplate, dataSource);
        this.lineService = new LineService(
                new LineDao(jdbcTemplate, dataSource),
                sectionDao,
                stationDao);
    }

    @BeforeEach
    void setUp() {
        long 강남역 = stationDao.save(new Station("강남역"));
        long 선릉역 = stationDao.save(new Station("선릉역"));
        lineService.save(new LineRequest("line1", "red", 강남역, 선릉역, 5));

    }

    @Test
    @DisplayName("지하철 노선 추가, 조회, 삭제 테스트")
    void LineCRDTest() {
        List<LineResponse> lines = lineService.findAll();

        assertThat(lines).hasSize(1)
                .extracting("name", "color")
                .containsExactly(tuple("line1", "red"));

        lineService.delete(lines.get(0).getId());
        assertThat(lineService.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("지하철을 추가할 때 노선도 같이 등록된다.")
    void createLineWithSection() {
        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections).hasSize(1)
                .extracting("upStationId", "downStationId", "distance")
                .containsExactly(tuple(1L, 2L, 5));
    }

    @Test
    @DisplayName("중복된 노선 이름 입력 시 예외 발생 테스트")
    void validateDuplicationNameTest() {
        assertThatThrownBy(() -> lineService.save(
                new LineRequest("line1", "yellow", 1L, 2L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 노선 이름입니다.");
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void updateTest() {
        lineService.update(1L, new LineRequest(
                "line2", "yellow", 1L, 2L, 5));

        assertThat(lineService.findById(1L))
                .extracting("name", "color")
                .containsExactly("line2", "yellow");
    }
}
