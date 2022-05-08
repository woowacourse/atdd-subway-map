package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Test
    @DisplayName("노선을 생성한다.")
    void create() {
        // given
        given(lineDao.save(any(Line.class))).willReturn(new Line(1L, "1호선", "blue"));

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
        given(lineDao.findById(1L)).willReturn(Optional.of(new Line(1L, "1호선", "blue")));

        // when
        LineResponse lineResponse = lineService.showById(1L);

        // then
        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getName()).isEqualTo("1호선");
        assertThat(lineResponse.getColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 조회할 경우 예외를 발생한다.")
    void notFindById() {
        // given
        given(lineDao.findById(1L)).willThrow(new NotFoundException("조회하려는 id가 존재하지 않습니다."));

        // when && then
        assertThatThrownBy(() -> lineService.showById(1L))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("조회하려는 id가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("노선 전체를 조회한다.")
    void findAll() {
        // given
        Line line1 = new Line(1L, "1호선", "blue");
        Line line2 = new Line(2L, "2호선", "green");
        given(lineDao.findAll()).willReturn(List.of(line1, line2));

        // when
        List<LineResponse> lineResponses = lineService.showAll();

        // then
        assertThat(lineResponses).hasSize(2);
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void update() {
        // given
        Line line = new Line(1L, "1호선", "blue");
        given(lineDao.findById(1L)).willReturn(Optional.of(line));

        // when
        lineService.updateById(1L, new LineUpdateRequest("2호선", "green"));

        // then
        then(lineDao).should(times(1)).modifyById(1L, line);
    }

    @Test
    @DisplayName("존재하지 않는 노선을 수정할 경우 예외를 발생한다.")
    void notUpdateById() {
        // given
        given(lineDao.findById(1L)).willThrow(new NotFoundException("조회하려는 id가 존재하지 않습니다."));

        // when
        assertThatThrownBy(() -> lineService.updateById(1L, new LineUpdateRequest("2호선", "blue")))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("조회하려는 id가 존재하지 않습니다.");

        // then
        then(lineDao).should(times(0)).modifyById(1L, new Line("2호선", "blue"));
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void delete() {
        // given & when
        lineService.removeById(1L);

        // then
        then(lineDao).should().deleteById(1L);
    }
}
