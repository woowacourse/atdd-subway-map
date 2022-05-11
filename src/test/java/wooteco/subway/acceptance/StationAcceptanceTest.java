package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import wooteco.subway.service.dto.StationResponse;
import wooteco.subway.ui.dto.StationRequest;
import wooteco.subway.utils.RestAssuredUtil;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private Long savedId1;
    private Long savedId2;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        jdbcTemplate.update("delete from STATION", new EmptySqlParameterSource());

        savedId1 = insertData("강남역");
        savedId2 = insertData("역삼역");
    }

    private Long insertData(String name) {
        String insertSql = "insert into STATION (name) values (:name)";
        SqlParameterSource source = new MapSqlParameterSource("name", name);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertSql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest stationRequest = new StationRequest("선릉역");

        // when
        ExtractableResponse<Response> response = RestAssuredUtil.post("/stations", stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        ExtractableResponse<Response> response = RestAssuredUtil.post("/stations", stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given

        // when
        ExtractableResponse<Response> response = RestAssuredUtil.get("/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> stationIds = generateStationIds(response);

        assertThat(stationIds).containsAll(List.of(savedId1, savedId2));
    }

    private List<Long> generateStationIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest stationRequest = new StationRequest("잠실역");
        ExtractableResponse<Response> createResponse = RestAssuredUtil.post("/stations", stationRequest);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssuredUtil.delete(uri, new HashMap<>());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
