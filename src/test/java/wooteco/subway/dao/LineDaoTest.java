package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:initializeTable.sql")
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @DisplayName("노선 저장")
    @Test
    public void save() {
        // given
        Line line = new Line("10호선", "붉은색");

        // when
        Line requestedLine = lineDao.create(line);

        // then
        assertThat(requestedLine.getName()).isEqualTo(line.getName());
        assertThat(requestedLine.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("노선 중복 저장 시도")
    @Test
    public void duplicatedSave() {
        // given
        Line line1 = new Line("1호선", "초록색");
        Line line2 = new Line("1호선", "파란색");

        // when
        lineDao.create(line1);

        // then
        assertThatThrownBy(() -> lineDao.create(line2)).isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("id값에 맞는 노선 반환")
    @Test
    public void findLine() {
        // given
        Line line1 = new Line("12호선", "분홍색");
        Line saveLine = lineDao.create(line1);
        long id = saveLine.getId();

        // when
        Line requestedLine = lineDao.show(id);

        // then
        assertThat(requestedLine.getId()).isEqualTo(id);
        assertThat(requestedLine.getName()).isEqualTo(line1.getName());
        assertThat(requestedLine.getColor()).isEqualTo(line1.getColor());
    }

    @DisplayName("존재하지 않는 id 값을 가진 노선 반환 시도")
    @Test
    void findLineNotFoundException() {
        // given

        // when
        long id = -1;

        // then
        assertThatThrownBy(() -> lineDao.show(id))
            .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("모든 노선 호출")
    @Test
    void findAll() {
        // given
        Line line1 = new Line("10호선", "붉은색");
        lineDao.create(line1);
        Line line2 = new Line("11호선", "노란색");
        lineDao.create(line2);

        // when
        List<Line> lines = lineDao.showAll();

        // then
        assertThat(lines.get(0).getName()).isEqualTo(line1.getName());
        assertThat(lines.get(0).getColor()).isEqualTo(line1.getColor());
        assertThat(lines.get(1).getName()).isEqualTo(line2.getName());
        assertThat(lines.get(1).getColor()).isEqualTo(line2.getColor());
    }

    @DisplayName("노선 업데이트")
    @Test
    void update() {
        // given
        Line line1 = new Line("11호선", "보라색");
        Line saveLine = lineDao.create(line1);
        long id = saveLine.getId();
        String requestName = "분당선";
        String requestColor = "노란색";
        Line requestLine = new Line(requestName, requestColor);

        // when
        lineDao.update((int) id, requestLine);
        Line responseLine = lineDao.show(id);

        // then
        assertThat(responseLine.getName()).isEqualTo(requestName);
        assertThat(responseLine.getColor()).isEqualTo(requestColor);
    }

    @DisplayName("노선 삭제")
    @Test
    void remove() {
        // given
        Line line1 = new Line("12호선", "분홍색");
        Line saveLine = lineDao.create(line1);
        long id = saveLine.getId();

        // when
        int number = lineDao.delete(id);

        // then
        assertThat(number).isEqualTo(1);
    }

    @DisplayName("존재하지 않는 id를 통한 노선 삭제 시도시 예외처리")
    @Test
    void removeNotFoundException() {
        // given
        long id = -1;

        // when
        int number = lineDao.delete(id);

        // then
        assertThat(number).isEqualTo(0);
    }
}