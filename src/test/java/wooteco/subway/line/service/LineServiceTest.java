package wooteco.subway.line.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineRequest;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @AfterEach
    void deleteLog() {
        lineService.removeAll();
    }

    @DisplayName("노선 저장")
    @Test
    void save() throws Exception {
        Line save = 라인_생성("2호선", "bg-red-600");

        //then
        assertThat(save).usingRecursiveComparison().isEqualTo(new Line(save.getId(), "2호선", "bg-red-600"));
    }

    private Line 라인_생성(String name, String color) {
        //given
        Line line = new Line(name, color);

        //when
        Line save = lineService.save(line);
        return save;
    }

    @DisplayName("노선 조회 - 아이디")
    @Test
    void findById() throws Exception {
        //given
        Line save = 라인_생성("2호선", "bg-red-600");
        라인_생성("3호선", "bg-blue-600");

        //then
        assertThat(lineService.findLineById(save.getId())).usingRecursiveComparison().isEqualTo(new Line(save.getId(), "2호선", "bg-red-600"));
    }

    @DisplayName("노선 조회 - 이름")
    @Test
    void findByName() throws Exception {
        Line save = 라인_생성("2호선", "bg-red-600");
        라인_생성("3호선", "bg-blue-600");

        //then
        assertThat(lineService.findLineByName("2호선").get()).usingRecursiveComparison().isEqualTo(new Line(save.getId(), "2호선", "bg-red-600"));
    }

    @DisplayName("노선 전체 조회")
    @Test
    void findAll() throws Exception {
        Line save1 = 라인_생성("2호선", "bg-red-600");
        Line save2 = 라인_생성("3호선", "bg-blue-600");

        //then
        assertThat(lineService.findAll()).usingRecursiveComparison()
                .isEqualTo(Arrays.asList(new Line(save1.getId(), "2호선", "bg-red-600"), new Line(save2.getId(), "3호선", "bg-blue-600")));
    }

    @DisplayName("노선 삭제")
    @Test
    void remove() throws Exception {
        Line save1 = 라인_생성("2호선", "bg-red-600");
        Line save2 = 라인_생성("3호선", "bg-blue-600");

        //then
        lineService.removeLine(save1.getId());
        assertThat(lineService.findAll()).usingRecursiveComparison()
                .isEqualTo(Collections.singletonList(new Line(save2.getId(), "3호선", "bg-blue-600")));
    }

    @DisplayName("노선 전체 삭제")
    @Test
    void removeAll() throws Exception {
        라인_생성("2호선", "bg-red-600");
        라인_생성("3호선", "bg-blue-600");

        //then
        lineService.removeAll();
        assertThat(lineService.findAll()).hasSize(0);
    }

    @DisplayName("노선 업데이트")
    @Test
    void update() throws Exception {
        // when
        Line save = 라인_생성("2호선", "bg-red-600");

        //then
        lineService.update(save.getId(), new LineRequest("3호선", "bg-blue-600"));

        //then
        assertThat(lineService.findLineById(save.getId())).usingRecursiveComparison().isEqualTo(new Line(save.getId(), "3호선", "bg-blue-600"));
    }
}
