package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistItemException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

@SpringBootTest()
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Sql("classpath:tableInit.sql")
@DisplayName("LineService 테스트")
class LineServiceTest {

    private final LineService lineService;

    public LineServiceTest(LineService lineService) {
        this.lineService = lineService;
    }

    @Test
    @DisplayName("노선을 생성한다.")
    void create() {
        //given
        LineRequest lineRequest = new LineRequest("2호선", "green");

        //when
        LineResponse lineResponse = lineService.create(lineRequest);

        //then
        checkedThen(lineRequest, lineResponse);
    }

    @Test
    @DisplayName("중복된 이름의 노선을 생성하면 에러가 발생한다.")
    void createWithDuplicateName() {
        //given
        LineRequest lineRequest = new LineRequest("2호선", "green");

        //when
        lineService.create(lineRequest);

        //then
        assertThatThrownBy(() -> lineService.create(lineRequest))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 찾는다.")
    void findById() {
        //given
        LineRequest lineRequest = new LineRequest("2호선", "green");
        lineService.create(lineRequest);

        //when
        LineResponse lineResponse = lineService.findById(1L);

        //then
        checkedThen(lineRequest, lineResponse);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 찾는다.")
    void findByIdWithNotExistItemException() {
        assertThatThrownBy(() -> lineService.findById(1L))
            .isInstanceOf(NotExistItemException.class);
    }

    @Test
    @DisplayName("노선 전체를 가져온다.")
    void findAll() {
        //given
        LineRequest lineRequest1 = new LineRequest("2호선", "green");
        LineRequest lineRequest2 = new LineRequest("3호선", "orange");
        lineService.create(lineRequest1);
        lineService.create(lineRequest2);

        //when
        List<LineResponse> lines = lineService.findAll();

        //then
        assertThat(lines).hasSize(2);
        checkedThen(lineRequest1, lines.get(0));
        checkedThen(lineRequest2, lines.get(1));
    }

    @Test
    @DisplayName("노선의 정보를 수정한다.")
    void update() {
        //given
        LineRequest lineRequest1 = new LineRequest("2호선", "green");
        lineService.create(lineRequest1);

        LineRequest lineRequest2 = new LineRequest("3호선", "orange");

        //when
        lineService.update(1L, lineRequest2);

        //then
        checkedThen(lineRequest2, lineService.findById(1L));
    }

    @Test
    @DisplayName("없는 노선의 정보를 수정하면 에러가 발생한다.")
    void updateWithNotExistItem() {
        //given
        LineRequest lineRequest = new LineRequest("3호선", "orange");

        //when, then
        assertThatThrownBy(() -> lineService.update(1L, lineRequest))
            .isInstanceOf(NotExistItemException.class);
    }

    @Test
    @DisplayName("기존에 있는 이름으로 노선을 수정시 에러가 발생한다.")
    void updateWithDuplicateName() {
        //given
        LineRequest lineRequest1 = new LineRequest("2호선", "green");
        LineRequest lineRequest2 = new LineRequest("3호선", "orange");
        lineService.create(lineRequest1);
        lineService.create(lineRequest2);

        //when, then
        assertThatThrownBy(() -> lineService.update(1L, lineRequest2))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 삭제한다.")
    void delete() {
        //given
        LineRequest lineRequest1 = new LineRequest("2호선", "green");
        lineService.create(lineRequest1);

        //when
        lineService.delete(1L);

        //then
        assertThat(lineService.findAll()).hasSize(0);
    }

    private void checkedThen(LineRequest lineRequest, LineResponse lineResponse) {
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }
}