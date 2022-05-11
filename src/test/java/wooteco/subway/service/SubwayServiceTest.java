package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.entity.SectionEntity;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class SubwayServiceTest {

    @Autowired
    private SubwayService subwayService;
    @Autowired
    private SectionDao sectionDao;

    @DisplayName("station을 저장하고 response를 반환한다")
    @Test
    void saveStation() {
        StationRequest stationRequest = new StationRequest("강남역");

        StationResponse stationResponse = subwayService.saveStation(stationRequest);
        assertThat(stationResponse.getId()).isEqualTo(1L);
        assertThat(stationResponse.getName()).isEqualTo("강남역");
    }

    @DisplayName("stations을 모두 가져와 stationResponse들을 반환한다.")
    @Test
    void getStations() {
        addStation("강남역");
        addStation("잠실역");

        List<StationResponse> responses = subwayService.getStations();

        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0).getName()).isEqualTo("강남역");
        assertThat(responses.get(1).getName()).isEqualTo("잠실역");
    }

    @DisplayName("특정 station을 삭제한다.")
    @Test
    void deleteStation() {
        addStation("강남역");
        addStation("잠실역");

        subwayService.deleteStation(1L);
        List<StationResponse> responses = subwayService.getStations();

        assertThat(responses.size()).isEqualTo(1);
        assertThat(responses.get(0).getName()).isEqualTo("잠실역");
    }

    @DisplayName("line을 추가한다")
    @Test
    void addLine() {
        addStation("강남역");
        addStation("잠실역");

        LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 2L, 3);
        LineResponse response = subwayService.addLine(lineRequest);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("2호선");
        assertThat(response.getColor()).isEqualTo("초록색");
        assertThat(response.getStations().size()).isEqualTo(2);
    }

    @DisplayName("line을 업데이트한다.")
    @Test
    void updateLine() {
        addLine("2호선", "초록색");

        LineRequest lineRequest = new LineRequest("3호선", "빨간색", null, null, 0);
        subwayService.updateLine(1L, lineRequest);

        LineResponse response = subwayService.getLine(1L);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("3호선");
        assertThat(response.getColor()).isEqualTo("빨간색");
    }

    @DisplayName("모든 line을 가져온다.")
    @Test
    void getLines() {
        addLine("2호선", "초록색");

        List<LineResponse> responses = subwayService.getLines();

        assertThat(responses.size()).isEqualTo(1);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getName()).isEqualTo("2호선");
        assertThat(responses.get(0).getColor()).isEqualTo("초록색");
    }

    @DisplayName("특정 line을 가져온다.")
    @Test
    void getLine() {
        addLine("2호선", "초록색");

        LineResponse response = subwayService.getLine(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("2호선");
        assertThat(response.getColor()).isEqualTo("초록색");
    }

    @DisplayName("특정 line을 삭제한다.")
    @Test
    void deleteLine() {
        addLine("2호선", "초록색");

        subwayService.deleteLine(1L);
        assertThatThrownBy(() -> subwayService.getLine(1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("section을 추가한다")
    @Test
    void addSection() {
        addSection("선릉역");

        List<SectionEntity> sectionEntities = sectionDao.findByLineId(1L);
        assertThat(sectionEntities.size()).isEqualTo(2);
    }

    @DisplayName("section을 삭제한다")
    @Test
    void deleteSection() {
        addSection("선릉역");

        subwayService.deleteSection(1L, 3L);
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(1L);

        assertThat(sectionEntities.size()).isEqualTo(1);
    }

    private void addStation(String name) {
        StationRequest stationRequest1 = new StationRequest(name);
        subwayService.saveStation(stationRequest1);
    }
    private void addLine(String name, String color) {
        addStation("강남역");
        addStation("잠실역");
        LineRequest lineRequest = new LineRequest(name, color, 1L, 2L, 3);
        subwayService.addLine(lineRequest);
    }

    private void addSection(String name) {
        addLine("2호선", "초록색");
        addStation(name);
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 2);
        subwayService.addSection(1L, sectionRequest);
    }
}
