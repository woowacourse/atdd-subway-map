package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.testutils.Fixture.LINE_REQUEST_2호선_STATION_1_3;
import static wooteco.subway.testutils.Fixture.LINE_REQUEST_분당선_STATION_1_3;
import static wooteco.subway.testutils.Fixture.LINE_REQUEST_신분당선_STATION_1_2;
import static wooteco.subway.testutils.Fixture.LINE_REQUEST_중앙선_STATION_1_3;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.LineNotFoundException;

@JdbcTest
class LineServiceTest {

    @Autowired
    private DataSource dataSource;

    private LineDao lineDao;
    private SectionDao sectionDao;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        this.lineDao = new JdbcLineDao(dataSource);
        this.sectionDao = new JdbcSectionDao(dataSource);
        this.lineService = new LineService(lineDao, sectionDao);
    }

    @DisplayName("새로운 호선을 생성한다")
    @Test
    void create() {
        //given
        final Line line = lineService.create(LINE_REQUEST_신분당선_STATION_1_2);

        //when & then
        assertThat(line.getId()).isNotNull();
    }

    @DisplayName("호선을 중복 생성하면 예외가 발생한다.")
    @Test
    void create_duplicate() {
        //given
        lineService.create(LINE_REQUEST_신분당선_STATION_1_2);

        //when & then
        assertThatThrownBy(() -> lineService.create(LINE_REQUEST_신분당선_STATION_1_2))
            .isInstanceOf(LineDuplicateException.class)
            .hasMessage("[ERROR] 이미 존재하는 노선입니다.");
    }


    @DisplayName("모든 호선들을 조회할 수 있다.")
    @Test
    void findAll() {
        //given
        lineService.create(LINE_REQUEST_신분당선_STATION_1_2);
        lineService.create(LINE_REQUEST_분당선_STATION_1_3);

        //then
        final List<Line> lines = lineService.findAll();

        //when
        assertThat(lines).hasSize(2);
    }

    @DisplayName("특정 노선을 조회할 수 있다.")
    @Test
    void findById() {
        //given
        final Line expected = lineService.create(LINE_REQUEST_중앙선_STATION_1_3);

        //when
        final Line actual = lineService.findById(expected.getId());

        //then
        assertThat(actual.getId()).isEqualTo(expected.getId());
    }

    @DisplayName("특정 노선을 조회시, 없는 노선을 조회 요청하면 예외를 발생시킨다.")
    @Test
    void findById_fail() {
        //given & when &then
        assertThatThrownBy(() -> lineService.findById(-1L))
            .isInstanceOf(LineNotFoundException.class)
            .hasMessage("[ERROR] 해당 노선이 없습니다.");
    }

    @DisplayName("특정 노선을 수정할 수 있다.")
    @Test
    void update() {
        //given
        final Line line = lineService.create(LINE_REQUEST_신분당선_STATION_1_2);
        final LineRequest lineRequest = new LineRequest("돌범선", "WHITE");

        //when
        lineService.update(line.getId(), lineRequest);

        //then
        assertThat(lineService.findById(line.getId()).getName()).isEqualTo("돌범선");
    }

    @DisplayName("특정 노선을 수정시, 없는 노선을 수정 요청하면 예외를 발생시킨다.")
    @Test
    void update_fail_invalid_id() {
        //given
        final LineRequest lineRequest = new LineRequest("돌범선", "WHITE");

        //when & then
        assertThatThrownBy(() -> lineService.update(-1L, lineRequest))
            .isInstanceOf(LineNotFoundException.class)
            .hasMessage("[ERROR] 해당 노선이 없습니다.");
    }

    @DisplayName("특정 노선을 수정시, 이미 존재하는 이름으로 수정 요청하면 예외를 발생시킨다.")
    @Test
    void update_fail_duplicate_id() {
        //given
        final Line create1 = lineService.create(LINE_REQUEST_중앙선_STATION_1_3);
        final Line create2 = lineService.create(LINE_REQUEST_2호선_STATION_1_3);
        final LineRequest lineRequest = new LineRequest(create2.getName(), create2.getColor());

        //when & then
        assertThatThrownBy(() -> lineService.update(create1.getId(), lineRequest))
            .isInstanceOf(LineDuplicateException.class)
            .hasMessage("[ERROR] 이미 존재하는 노선입니다.");
    }

    @DisplayName("특정 노선을 제거할 수 있다.")
    @Test
    void delete() {
        //given
        final Line line = lineService.create(LINE_REQUEST_신분당선_STATION_1_2);

        //when
        lineService.delete(line.getId());

        //then
        assertThatThrownBy(() -> lineService.findById(line.getId()))
            .isInstanceOf(LineNotFoundException.class)
            .hasMessage("[ERROR] 해당 노선이 없습니다.");
    }

    @DisplayName("특정 노선을 삭제시, 없는 노선을 삭제 요청하면 예외를 발생시킨다.")
    @Test
    void delete_fail() {
        //given & when &then
        assertThatThrownBy(() -> lineService.delete(-1L))
            .isInstanceOf(LineNotFoundException.class)
            .hasMessage("[ERROR] 해당 노선이 없습니다.");
    }
}
