package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest
@Sql("/lineTestSchema.sql")
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("이미 존재하는 노선의 이름이 있을 때 예외가 발생한다.")
    void saveExceptionByExistName() {
        LineRequest 분당선 = new LineRequest("분당선", "bg-red-600", 1L, 2L, 10);
        lineService.save(분당선);
        assertThatThrownBy(() -> lineService.save(분당선))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 노선 이름입니다.");
    }

    @Test
    @DisplayName("노선 저장 시 결과를 확인한다.")
    void checkSaveResult() {
        // given
        LineRequest 분당선 = new LineRequest("분당선", "bg-red-600", 1L, 2L, 10);

        //when
        LineResponse response = lineService.save(분당선);

        //then
        assertThat(response).extracting("id", "name", "color")
                .containsExactly(1L, "분당선", "bg-red-600");
    }

    @Test
    @DisplayName("노선 단건 조회 테스트 ")
    void findById() {
        // given
        LineRequest 분당선 = new LineRequest("분당선", "bg-red-600", 1L, 2L, 10);
        lineService.save(분당선);

        //when
        LineResponse response = lineService.findById(1L);

        //then
        assertThat(response).extracting("id", "name", "color")
                .containsExactly(1L, "분당선", "bg-red-600");
    }

    @Test
    @DisplayName("노선 전체 조회 테스트 ")
    void findAll() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "yellow", 1L, 2L, 10);
        LineRequest 분당선 = new LineRequest("분당선", "bg-red-600", 1L, 2L, 10);
        lineService.save(신분당선);
        lineService.save(분당선);

        //when
        List<LineResponse> response = lineService.findAll();

        //then
        assertThat(response).extracting("id", "name", "color")
                .containsExactly(tuple(1L, "신분당선", "yellow"), tuple(2L, "분당선", "bg-red-600"));
    }

/*
    @Test
    @DisplayName("없는 id의 Line을 삭제할 수 없다.")
    void deleteByInvalidId() {
        Line line = lineService.save(new Line("신분당선", "bg-red-600"));
        Long lineId = line.getId() + 1;

        assertThatThrownBy(() -> lineService.delete(lineId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("없는 Line 입니다.");
    }

    @Test
    @DisplayName("이미 삭제한 id의 Line을 또 삭제할 수 없다.")
    void deleteByDuplicatedId() {
        Line line = lineService.save(new Line("신분당선", "bg-red-600"));
        Long lineId = line.getId();
        lineService.delete(lineId);

        assertThatThrownBy(() -> lineService.delete(lineId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("없는 Line 입니다.");
    }*/
}
