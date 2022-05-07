package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private JdbcLineDao jdbcLineDao;

    @DisplayName("노선을 생성한다.")
    @Test
    void create() {
        // given
        given(jdbcLineDao.save(any(Line.class))).willReturn(new Line(1L, "호호선", "red"));
        // when
        LineResponse response = lineService.create(new LineRequest("호호선", "red", 1L, 1L, 1));
        // then

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("호호선");
    }

    @DisplayName("id 로 노선을 조회한다")
    @Test
    void findById() {
        // given
        given(jdbcLineDao.findById(any())).willReturn(Optional.of(new Line("미르역", "pink")));
        // when
        LineResponse response = lineService.showById(1L);
        // then

        assertAll(
                () -> assertThat(response.getName()).isEqualTo("미르역"),
                () -> assertThat(response.getColor()).isEqualTo("pink")
        );
    }

    @DisplayName("노선 목록 조회")
    @Test
    void findAll() {
        given(jdbcLineDao.findAll()).willReturn(List.of(new Line("미르노선", "pink"), new Line("수달노선", "black")));

        List<LineResponse> responses = lineService.showAll();

        assertThat(responses).hasSize(2);
    }

    @DisplayName("id 값으로 노선 이름을 update")
    @Test
    void updateById() {
        lineService.updateById(1L, "호호역", "red");
        // then
        then(jdbcLineDao).should(times(1)).modifyById(1L, new Line("호호역", "red"));
    }

    @DisplayName("id 값으로 노선을 삭제한다.")
    @Test
    void deleteById() {
        lineService.removeById(1L);

        then(jdbcLineDao).should(times(1)).deleteById(1L);
    }

}
