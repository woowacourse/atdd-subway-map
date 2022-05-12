package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;

@JdbcTest
class LineServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(
                new LineDao(jdbcTemplate),
                new SectionDao(jdbcTemplate),
                new StationDao(jdbcTemplate)
        );
    }

    @Test
    @DisplayName("노선 생성")
    void saveLine() {
        //given
        var upStationId = insertStation("테스트1역");
        var downStationId = insertStation("테스트12역");

        //when
        var lineResponse = lineService.createLine(
                new LineRequest("테스트호선", "테스트색", upStationId, downStationId, 1)
        );

        var upStationResponse = lineResponse.getStations().get(0);
        var downStationResponse = lineResponse.getStations().get(1);

        //then
        assertAll(
                () -> assertThat(upStationResponse.getId()).isEqualTo(upStationId),
                () -> assertThat(downStationResponse.getId()).isEqualTo(downStationId),
                () -> assertThat(lineResponse.getName()).isEqualTo("테스트호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("테스트색")
        );
    }

    private Long insertStation(String name) {
        var keyHolder = new GeneratedKeyHolder();
        var sql = "INSERT INTO station (name) values(?)";

        jdbcTemplate.update(con -> {
            var statement = con.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, name);
            return statement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Test
    @DisplayName("중복 노선 생성시 예외 발생")
    void duplicateLineName() {
        var lineRequest = new LineRequest("테스트호선", "테스트색");
        lineService.createLine(lineRequest);

        assertThatThrownBy(() -> lineService.createLine(lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선 조회")
    void findLine() {
        //given
        var lineRequest = new LineRequest("1호선", "blue");
        var lineResponse = lineService.createLine(lineRequest);

        //when
        var findLineResponse = lineService.findLineInfos(lineResponse.getId());

        //then
        assertAll(
                () -> assertThat(findLineResponse.getId()).isEqualTo(lineResponse.getId()),
                () -> assertThat(findLineResponse.getName()).isEqualTo("1호선"),
                () -> assertThat(findLineResponse.getColor()).isEqualTo("blue")
        );
    }

    @Test
    @DisplayName("노선 조회 실패")
    void findLineFail() {
        assertThatThrownBy(() -> lineService.findLineInfos(-1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("노선 목록 조회")
    void findAllLine() {
        //given
        var lineRequest1 = new LineRequest("1호선", "blue");
        var lineRequest2 = new LineRequest("2호선", "green");
        var lineResponse1 = lineService.createLine(lineRequest1);
        var lineResponse2 = lineService.createLine(lineRequest2);

        //when
        var ids = lineService.findAll().stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        //then
        assertAll(
                () -> assertThat(ids.contains(lineResponse1.getId())).isTrue(),
                () -> assertThat(ids.contains(lineResponse2.getId())).isTrue()
        );
    }

    @Test
    @DisplayName("노선 업데이트 성공")
    void updateLine() {
        //given
        var lineRequest = new LineRequest("1호선", "blue");
        var lineResponse = lineService.createLine(lineRequest);

        //when
        lineService.updateById(lineResponse.getId(), "2호선", "green");
        var lineInfos = lineService.findLineInfos(lineResponse.getId());

        //then
        assertAll(
                () -> assertThat(lineInfos.getName()).isEqualTo("2호선"),
                () -> assertThat(lineInfos.getColor()).isEqualTo("green")
        );
    }

    @Test
    @DisplayName("노선 업데이트 실패")
    void failUpdateLine() {
        var lineRequest1 = new LineRequest("1호선", "blue");
        lineService.createLine(lineRequest1);
        var lineRequest2 = new LineRequest("2호선", "green");
        var lineResponse2 = lineService.createLine(lineRequest2);

        assertAll(
                () -> assertThatThrownBy(() -> lineService.updateById(-1L, "3호선", "orange"))
                        .isInstanceOf(NoSuchElementException.class),
                () -> assertThatThrownBy(() -> lineService.updateById(lineResponse2.getId(), "1호선", "black"))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> lineService.updateById(lineResponse2.getId(), "3호선", "blue"))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @Test
    @DisplayName("노선 삭제")
    void deleteLine() {
        //given
        var lineRequest = new LineRequest("500호선", "테스트색200");
        var lineResponse = lineService.createLine(lineRequest);
        var id = lineResponse.getId();

        //when
        lineService.deleteById(id);
        var actual = lineService.findAll().stream()
                .noneMatch(it -> it.getId().equals(id));

        //then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("없는 노선 삭제요청 시 예외 발생")
    void invalidLine() {
        assertThatThrownBy(() -> lineService.deleteById(-1L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
