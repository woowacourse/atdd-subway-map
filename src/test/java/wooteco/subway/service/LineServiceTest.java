package wooteco.subway.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.StationJdbcDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.exception.ClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationJdbcDao stationJdbcDao;

    @AfterEach
    void finish() {
        List<LineResponse> lines = lineService.findAll();
        for (LineResponse line : lines) {
            lineService.deleteLine(line.getId());
        }
    }

    @DisplayName("노선 저장")
    @Test
    void save() {
        Station firstStation = stationJdbcDao.save(new StationRequest("역삼역"));
        Station secondStation = stationJdbcDao.save(new StationRequest("삼성역"));

        LineRequest line = new LineRequest("4호선", "green", firstStation.getId(), secondStation.getId(), 10);
        LineResponse newLine = lineService.createLine(line);

        assertThat(line.getName()).isEqualTo(newLine.getName());
    }

    @DisplayName("중복된 노선 저장시 예외")
    @Test
    void duplicateLine() {
        Station firstStation = stationJdbcDao.save(new StationRequest("역삼역"));
        Station secondStation = stationJdbcDao.save(new StationRequest("삼성역"));

        LineRequest line = new LineRequest("3호선", "red", firstStation.getId(), secondStation.getId(), 10);
        LineRequest dupLine = new LineRequest("3호선", "red", firstStation.getId(), secondStation.getId(), 10);
        lineService.createLine(line);

        assertThatThrownBy(() -> lineService.createLine(dupLine))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("이미 등록된 지하철노선입니다.");
    }


    @DisplayName("노선 정보 전체 조회")
    @Test
    void findAll() {
        Station firstStation = stationJdbcDao.save(new StationRequest("역삼역"));
        Station secondStation = stationJdbcDao.save(new StationRequest("삼성역"));

        lineService.createLine(new LineRequest("5호선", "red", firstStation.getId(), secondStation.getId(), 10));
        lineService.createLine(new LineRequest("7호선", "red", firstStation.getId(), secondStation.getId(), 10));

        List<LineResponse> lines = lineService.findAll();
        lines.stream()
                .filter(line -> line.getName().equals("5호선"))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("노선 정보가 없습니다."));
    }

    @Test
    @DisplayName("노선 정보 삭제")
    void delete() {
        Station firstStation = stationJdbcDao.save(new StationRequest("역삼역"));
        Station secondStation = stationJdbcDao.save(new StationRequest("삼성역"));

        LineRequest line = new LineRequest("4호선", "red", firstStation.getId(),
                secondStation.getId(), 10);
        LineResponse newLine = lineService.createLine(line);

        assertThat(lineService.deleteLine(newLine.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("노선 정보 업데이트")
    void update() {
        Station firstStation = stationJdbcDao.save(new StationRequest("역삼역"));
        Station secondStation = stationJdbcDao.save(new StationRequest("삼성역"));

        LineRequest line = new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10);
        LineResponse newLine = lineService.createLine(line);

        assertThat(lineService.updateLine(newLine.getId(), new LineRequest("7호선", "red", firstStation.getId(),
                secondStation.getId(), 10))).isEqualTo(1);
    }

    @Test
    @DisplayName("노선 정보 업데이트 - 이미 존재하는 노선으로 업데이트")
    void updateDuplicateLine() {
        Station firstStation = stationJdbcDao.save(new StationRequest("역삼역"));
        Station secondStation = stationJdbcDao.save(new StationRequest("삼성역"));

        LineRequest line = new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10);;
        lineService.createLine(line);

        LineRequest secondLine = new LineRequest("8호선", "red", firstStation.getId(), secondStation.getId(), 10);;
        LineResponse secondNewLine = lineService.createLine(secondLine);

        assertThatThrownBy(() -> lineService.updateLine(secondNewLine.getId(),
                new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10)))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("등록된 지하철노선으로 변경할 수 없습니다.");
    }

    @Test
    @DisplayName("노선 정보 조회")
    void find() {
        Station firstStation = stationJdbcDao.save(new StationRequest("역삼역"));
        Station secondStation = stationJdbcDao.save(new StationRequest("삼성역"));

        LineRequest line = new LineRequest("4호선", "red", firstStation.getId(), secondStation.getId(), 10);
        LineResponse newLine = lineService.createLine(line);

        assertThat(lineService.findById(newLine.getId()).getName()).isEqualTo(line.getName());
    }
}
