package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.repository.dao.LineDao;

class LineServiceTest {

    private final LineService service = new LineService(new LineDao());

    @BeforeEach
    void initStore() {
        LineDao.removeAll();
    }

    @DisplayName("노선 이름과 색깔을 입력받아서 해당 이름과 색깔을 가진 노선을 등록한다.")
    @Test
    void register() {
        Line created = service.register("2호선", "bg-green-600");

        assertAll(
                () -> assertThat(created.getName()).isEqualTo("2호선"),
                () -> assertThat(created.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("이미 존재하는 노선이름으로 등록하려할 시 예외가 발생한다.")
    @Test
    void registerDuplicateName() {
        service.register("2호선", "bg-green-600");

        assertThatThrownBy(() -> service.register("2호선", "bg-green-600"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 이미 존재하는 노선 이름입니다.");
    }
}