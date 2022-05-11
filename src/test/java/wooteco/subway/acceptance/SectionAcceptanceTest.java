package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.acceptance.fixture.SimpleResponse;
import wooteco.subway.acceptance.fixture.SimpleRestAssured;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("구간을 생성한다.")
    public void createSection() {
        // given
        Map<String, String> lineParams =
                Map.of("name", "신분당선", "color", "bg-red-600");
        SimpleRestAssured.post("/lines", lineParams);

        Map<String, String> stationParams1 = Map.of("name", "강남역");
        Map<String, String> stationParams2 = Map.of("name", "역삼역");
        SimpleRestAssured.post("/stations", stationParams1);
        SimpleRestAssured.post("/stations", stationParams2);

        Map<String, String> sectionParams =
                Map.of("upStationId", "1",
                        "downStationId", "2",
                        "distance", "10");
        // when
        final SimpleResponse response = SimpleRestAssured.post("/lines/1/sections", sectionParams);
        // then
        assertThat(response.hasStatus(HttpStatus.OK)).isTrue();
    }

}
