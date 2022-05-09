package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest
public class LineServiceTest {

    private final LineService lineService;

    @Autowired
    private LineServiceTest(LineDao lineDao) {
        this.lineService = new LineService(lineDao);
    }

    @DisplayName("중복되는 노선 이름이 없을 때 성공적으로 저장되는지 테스트")
    @Test
    void save() {
        LineResponse lineResponse = lineService.save(new LineRequest("10호선", "yellow"));
        assertThat(lineResponse.getName()).isEqualTo("10호선");
    }

    @DisplayName("중복되는 노선 이름이 있을 때 에러가 발생하는지 테스트")
    @Test
    void save_duplicate() {
        LineResponse lineResponse = lineService.save(new LineRequest("11호선", "green"));
        assertThatThrownBy(() -> lineService.save(new LineRequest("11호선", "green")))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("유효한 id를 가진 노선을 가져오는지 테스트")
    @Test
    void findById() {
        LineResponse lineResponse = lineService.save(new LineRequest("12호선", "green"));
        assertThat(lineService.findById(lineResponse.getId()).getName()).isEqualTo("12호선");
    }

    @DisplayName("없는 id를 가진 노선을 조회할 때 예외가 발생하는지 테스트")
    @Test
    void findById_no_eixst_id() {
        assertThatThrownBy(() -> lineService.findById(-1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("존재하지 않는 id의 이름을 바꿀때 예외가 발생하는지 테스트")
    @Test
    void change_name_no_exist_id() {
        assertThatThrownBy(() -> lineService.changeLineName(-1L, "test"))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("존재하지 않는 id로 노선을 삭제할 때 예외가 발생하는지 테스트")
    @Test
    void delete_no_exist_id() {
        assertThatThrownBy(() -> lineService.deleteById(-1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
