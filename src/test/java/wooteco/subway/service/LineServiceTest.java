package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private JdbcLineDao jdbcLineDao;

    @Test
    @DisplayName("노선을 생성한다.")
    void create() {
        // given
        given(jdbcLineDao.save(any(Line.class))).willReturn(new Line(1L, "1호선", "blue"));

        // when
        LineResponse lineResponse = lineService.create(new LineRequest("1호선", "blue"));

        // then
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo("1호선");
        assertThat(lineResponse.getColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("노선을 조회한다.")
    void findById() {
        // given
        given(jdbcLineDao.findById(1L)).willReturn(Optional.of(new Line(1L, "1호선", "blue")));

        // when
        LineResponse lineResponse = lineService.showById(1L);

        // then
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo("1호선");
        assertThat(lineResponse.getColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("노선 전체를 조회한다.")
    void findAll() {
        // given
        Line line1 = new Line(1L, "1호선", "blue");
        Line line2 = new Line(2L, "2호선", "green");
        given(jdbcLineDao.findAll()).willReturn(List.of(line1, line2));

        // when
        List<LineResponse> lineResponses = lineService.showAll();

        // then
        assertThat(lineResponses).hasSize(2);
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void update() {
        // given & when
        lineService.updateById(1L, new LineUpdateRequest("2호선", "blue"));

        // then
        then(jdbcLineDao).should().modifyById(1L, new Line("2호선", "blue"));
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void delete() {
        // given & when
        lineService.removeById(1L);

        // then
        then(jdbcLineDao).should().deleteById(1L);
    }
}
