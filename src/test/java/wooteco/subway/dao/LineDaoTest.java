package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.domain.Line;

class LineDaoTest {

    @AfterEach
    void cleanUp() {
        LineDao.findAll().clear(); // 수정 필요
    }

    @DisplayName("노선 저장 기능을 테스트한다.")
    @Test
    void save_Line() {
        Line line = new Line("2호선", "초록색");

        Line persistLine = LineDao.save(line);

        assertThat(persistLine.getId()).isNotNull();
        assertThat(persistLine.getName()).isEqualTo("2호선");
        assertThat(persistLine.getColor()).isEqualTo("초록색");
    }

    @DisplayName("중복된 이름의 노선을 저장할 경우 예외가 발생한다.")
    @Test
    void save_Duplicated_Name_Line() {
        Line line = new Line("2호선", "초록색");
        LineDao.save(line);

        assertThatThrownBy(() -> LineDao.save(line))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("전체 노선의 개수가 맞는지 확인한다.")
    @Test
    void find_All_Line() {
        Line lineTwo = new Line("2호선", "초록색");
        Line lineEight = new Line("8호선", "분홍색");
        LineDao.save(lineTwo);
        LineDao.save(lineEight);

        assertThat(LineDao.findAll().size()).isEqualTo(2);
    }
}
