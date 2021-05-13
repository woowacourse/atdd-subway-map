package wooteco.subway.line.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.exception.line.LineDuplicatedNameException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.Line;
import wooteco.subway.line.dao.JdbcLineDao;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.line.dto.response.LineResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("지하철 노선 비즈니스 로직 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    private final LineDao lineDao = Mockito.mock(JdbcLineDao.class);

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선 생성")
    @Test
    void save() {
        // given
        LineCreateRequest 분당선 =
                new LineCreateRequest("분당선", "red", 1L, 2L, 3);
        given(lineDao.save(any(Line.class)))
                .willReturn(new Line(1L, "분당선", "red"));

        // when
        LineCreateResponse lineCreateResponse = lineService.save(분당선);

        // then
        assertThat(lineCreateResponse).usingRecursiveComparison()
                .isEqualTo(new LineCreateResponse(1L, "분당선", "red"));
        verify(lineDao).save(any(Line.class));
    }

    @DisplayName("중복 지하철 노선 생성")
    @Test
    void LineDuplicatedNameException() {
        // given
        LineCreateRequest 분당선 =
                new LineCreateRequest("분당선", "red", 1L, 2L, 3);
        given(lineDao.existByName(any(String.class)))
                .willThrow(LineDuplicatedNameException.class);

        // when & then
        assertThatThrownBy(() -> lineService.save(분당선))
                .isInstanceOf(LineDuplicatedNameException.class);
        verify(lineDao).existByName(any(String.class));
    }

    @DisplayName("노선 하나 조회")
    @Test
    void findBy() {
        // given
        given(lineDao.findById(1L))
                .willReturn(new Line(1L, "분당선", "red"));

        // when
        LineResponse result = lineService.findBy(1L);

        // then
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(new LineCreateResponse(1L, "분당선", "red"));
        verify(lineDao).findById(1L);
    }

    @DisplayName("존재하지 않는 노선 조회")
    @Test
    void LineNotFoundException() {
        // given
        given(lineDao.findById(1L))
                .willThrow(LineNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> lineService.findBy(1L))
                .isInstanceOf(LineNotFoundException.class);
        verify(lineDao).findById(1L);
    }

    @DisplayName("모든 노선 조회")
    @Test
    void findAll() {
        // given
        given(lineDao.findAll())
                .willReturn(Arrays.asList(
                        new Line("분당선", "red"),
                        new Line("신분당선", "green"),
                        new Line("2호선", "blue")
                ));

        // when
        List<LineResponse> results = lineService.findAll();
        List<Line> lines = results.stream()
                .map(response -> new Line(response.getName(), response.getColor()))
                .collect(Collectors.toList());

        // then
        assertThat(lines).usingRecursiveFieldByFieldElementComparator()
                .containsAll(Arrays.asList(
                        new Line("분당선", "red"),
                        new Line("신분당선", "green"),
                        new Line("2호선", "blue")
                ));
        verify(lineDao).findAll();
    }

    @DisplayName("노선 정보 수정")
    @Test
    void update() {
        // given
        given(lineDao.findById(1L))
                .willReturn(new Line(1L, "분당선", "red"));
        given(lineDao.existByNameAndNotInOriginalName("2호선", "분당선"))
                .willReturn(false);

        // when
        lineService.update(1L, new LineUpdateRequest("2호선", "green"));

        // then
        verify(lineDao).update(any(Line.class));
    }

    @DisplayName("존재하지 않는 노선 수정")
    @Test
    void updateNotExist() {
        // given
        given(lineDao.findById(1L))
                .willThrow(LineNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> lineService.update(1L, new LineUpdateRequest("2호선", "green")))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("이미 존재하는 노선이름으로 노선 정보 수정")
    @Test
    void updateDuplicatedName() {
        // given
        given(lineDao.findById(1L))
                .willReturn(new Line(1L, "분당선", "red"));
        given(lineDao.existByNameAndNotInOriginalName("2호선", "분당선"))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.update(1L, new LineUpdateRequest("2호선", "green")))
                .isInstanceOf(LineDuplicatedNameException.class);
    }

    @DisplayName("노선 삭제")
    @Test
    void delete() {
        // given
        Long id = 1L;

        // when
        lineService.delete(id);

        // then
        verify(lineDao).delete(any(Long.class));
    }
}