package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.jdbc.JdbcLineDao;
import wooteco.subway.service.dto.line.LineRequestDTO;
import wooteco.subway.service.dto.line.LineResponseDTO;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class LineServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new JdbcLineDao(jdbcTemplate));
    }

    @Test
    @DisplayName("노선 생성")
    void saveLine() {
        LineResponseDTO lineResponseDTO = lineService.create(new LineRequestDTO("2호선", "테스트색20"));
        assertThat(lineResponseDTO.getName()).isEqualTo("2호선");
    }

    @Test
    @DisplayName("중복 노선 생성시 예외 발생")
    void duplicateLineName() {
        var lineRequestDTO = new LineRequestDTO("1호선", "테스트색21");
        lineService.create(lineRequestDTO);

        assertThatThrownBy(() -> lineService.create(lineRequestDTO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선 조회")
    void findLine() {
        var lineRequest = new LineRequestDTO("1호선", "blue");
        var lineResponse = lineService.create(lineRequest);
        var findLineResponse = lineService.findById(lineResponse.getId());

        assertAll(
                () -> assertThat(findLineResponse.getId()).isEqualTo(lineResponse.getId()),
                () -> assertThat(findLineResponse.getName()).isEqualTo("1호선"),
                () -> assertThat(findLineResponse.getColor()).isEqualTo("blue")
        );
    }

    @Test
    @DisplayName("노선 조회 실패")
    void findLineFail() {
        assertThatThrownBy(() -> lineService.findById(-1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("노선 목록 조회")
    void findAllLine() {
        //given
        var lineRequest1 = new LineRequestDTO("1호선", "blue");
        var lineRequest2 = new LineRequestDTO("2호선", "green");
        var lineResponse1 = lineService.create(lineRequest1);
        var lineResponse2 = lineService.create(lineRequest2);

        //when
        List<Long> ids = lineService.findAll().stream()
                .map(LineResponseDTO::getId)
                .collect(Collectors.toList());

        //then
        assertThat(ids.contains(lineResponse1.getId())).isTrue();
        assertThat(ids.contains(lineResponse2.getId())).isTrue();
    }

    @Test
    @DisplayName("노선 업데이트 성공")
    void updateLine() {
        //given
        var lineRequest = new LineRequestDTO("1호선", "blue");
        var lineResponse = lineService.create(lineRequest);

        //when
        lineService.updateById(lineResponse.getId(), new LineRequestDTO("2호선", "green"));
        var lineInfos = lineService.findById(lineResponse.getId());

        //then
        assertThat(lineInfos.getName()).isEqualTo("2호선");
        assertThat(lineInfos.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("노선 업데이트 실패")
    void failUpdateLine() {
        var lineRequest1 = new LineRequestDTO("1호선", "blue");
        lineService.create(lineRequest1);
        var lineRequest2 = new LineRequestDTO("2호선", "green");
        var lineResponse2 = lineService.create(lineRequest2);

        assertAll(
                () -> assertThatThrownBy(() -> lineService.updateById(-1L, new LineRequestDTO("3호선", "orange")))
                        .isInstanceOf(NoSuchElementException.class),
                () -> assertThatThrownBy(() -> lineService.updateById(lineResponse2.getId(), new LineRequestDTO("1호선", "black")))
                        .isInstanceOf(NoSuchElementException.class),
                () -> assertThatThrownBy(() -> lineService.updateById(lineResponse2.getId(), new LineRequestDTO("3호선", "blue")))
                        .isInstanceOf(NoSuchElementException.class)
        );
    }

    @Test
    @DisplayName("노선 삭제")
    void deleteLine() {
        var lineRequest = new LineRequestDTO("500호선", "테스트색200");
        var lineResponse = lineService.create(lineRequest);
        var id = lineResponse.getId();

        lineService.deleteById(id);

        boolean actual = lineService.findAll().stream()
                .noneMatch(it -> it.getId().equals(id));

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("없는 노선 삭제요청 시 예외 발생")
    void invalidLine() {
        assertThatThrownBy(() -> lineService.deleteById(-1L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
