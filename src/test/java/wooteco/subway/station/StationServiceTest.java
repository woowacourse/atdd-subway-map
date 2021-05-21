package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.LineDao;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.exception.StationDeleteException;
import wooteco.subway.station.exception.StationExistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class StationServiceTest {

    @Autowired
    private StationService stationService;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationRequest stationRequest = new StationRequest("잠실역");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("truncate table STATION");
        jdbcTemplate.execute("alter table STATION alter column ID restart with 1");
    }

    @Test
    @DisplayName("역 정상 생성 테스트")
    void createStation() {
        Station savedStation = stationService.create(stationRequest);
        assertThat(savedStation.getName()).isEqualTo("잠실역");
    }

    @Test
    @DisplayName("역 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        stationService.create(stationRequest);
        assertThatThrownBy(() -> stationService.create(stationRequest))
                .isInstanceOf(StationExistenceException.class);
    }

    @Test
    @DisplayName("역 삭제 테스트")
    public void deleteStation() {
        Station savedStation = stationService.create(stationRequest);
        assertThat(stationService.findAll().size()).isEqualTo(1);

        stationService.delete(savedStation.getId());
        assertThat(stationService.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않은 역 삭제 테스트")
    public void deleteNotExistingStation() {
        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(StationDeleteException.class);
    }

    @Test
    @DisplayName("구간에 등록되어 있는 역 삭제")
    public void deleteWhenStationRegisteredInSection() {
        stationDao.save("가양역");
        stationDao.save("증미역");
        lineDao.save("9호선", "황토색");
        sectionDao.save(1L, 1L, 2L, 10);

        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(StationDeleteException.class);
    }
}