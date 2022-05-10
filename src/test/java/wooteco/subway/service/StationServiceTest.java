package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@JdbcTest
@Sql("/schema.sql")
class StationServiceTest {

    private final StationService stationService;

    @Autowired
    public StationServiceTest(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.stationService = new StationService(new StationDao(jdbcTemplate, dataSource));
    }

    @Test
    @DisplayName("지하철역 추가, 조회, 삭제 테스트")
    void StationCRDTest() {
        stationService.save(new StationRequest("station1"));
        stationService.save(new StationRequest("station2"));
        stationService.save(new StationRequest("station3"));

        List<StationResponse> stations = stationService.findAll();

        assertThat(stations).hasSize(3)
                .extracting("name")
                .containsExactly("station1", "station2", "station3");

        stationService.delete(stations.get(0).getId());
        stationService.delete(stations.get(1).getId());
        stationService.delete(stations.get(2).getId());

        assertThat(stationService.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("중복된 이름 입력 시 예외 발생 테스트")
    void validateDuplicationNameTest() {
        stationService.save(new StationRequest("station1"));

        assertThatThrownBy(() -> stationService.save(new StationRequest("station1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 역 이름입니다.");
    }
}
