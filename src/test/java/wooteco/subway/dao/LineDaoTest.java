package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {

    @Test
    @DisplayName("노선 이름이 중복되면 예외가 발생한다.")
    void save_inValidName() {
        // given
        final Line line = new Line("7호선", "bg-red-600");
        LineDao.save(line);

        // when
        final Line newLine = new Line("7호선", "bg-green-600");

        // then
        assertThatThrownBy(() -> LineDao.save(newLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 이름의 노선은 저장할 수 없습니다.");
    }

}