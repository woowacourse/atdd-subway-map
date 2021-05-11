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
    private final LineRequest line2Request = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);
    private final LineRequest line3Request = new LineRequest("3호선", "bg-orange-600", 1L, 3L, 13);

    public LineServiceTest(LineService lineService) {
        this.lineService = lineService;
    }

    @Test
    @DisplayName("노선을 생성한다.")
    void create() {
        //given

        //when
        LineResponse lineResponse = lineService.create(line2Request);

        //then
        checkedThen(line2Request, lineResponse);
    }

    @Test
    @DisplayName("중복된 이름의 노선을 생성하면 에러가 발생한다.")
    void createWithDuplicateName() {
        //given
        //when
        lineService.create(line2Request);

        //then
        assertThatThrownBy(() -> lineService.create(line2Request))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 찾는다.")
    void findById() {
        //given
        lineService.create(line2Request);

        //when
        LineResponse lineResponse = lineService.findById(1L);

        //then
        checkedThen(line2Request, lineResponse);
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
        lineService.create(line2Request);
        lineService.create(line3Request);

        //when
        List<LineResponse> lines = lineService.findAll();

        //then
        assertThat(lines).hasSize(2);
        checkedThen(line2Request, lines.get(0));
        checkedThen(line3Request, lines.get(1));
    }

    @Test
    @DisplayName("노선의 정보를 수정한다.")
    void update() {
        //given
        lineService.create(line2Request);

        //when
        lineService.update(1L, line3Request);

        //then
        checkedThen(new LineRequest("3호선", "bg-orange-600", 1L, 2L, 10), lineService.findById(1L));
    }

    @Test
    @DisplayName("없는 노선의 정보를 수정하면 에러가 발생한다.")
    void updateWithNotExistItem() {
        //given

        //when

        //then
        assertThatThrownBy(() -> lineService.update(1L, line2Request))
            .isInstanceOf(NotExistItemException.class);
    }

    @Test
    @DisplayName("기존에 있는 이름으로 노선을 수정시 에러가 발생한다.")
    void updateWithDuplicateName() {
        //given
        lineService.create(line2Request);
        lineService.create(line3Request);

        //when, then
        assertThatThrownBy(() -> lineService.update(1L, line3Request))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 삭제한다.")
    void delete() {
        //given
        lineService.create(line2Request);

        //when
        lineService.delete(1L);

        //then
        assertThat(lineService.findAll()).hasSize(0);
    }

    private void checkedThen(LineRequest lineRequest, LineResponse lineResponse) {
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
        assertThat(lineResponse.getUpStationId()).isEqualTo(lineRequest.getUpStationId());
        assertThat(lineResponse.getDownStationId()).isEqualTo(lineRequest.getDownStationId());
        assertThat(lineResponse.getDistance()).isEqualTo(lineRequest.getDistance());
    }
}