package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

public class LineDaoTest {

    @BeforeEach
    void beforEach() {
        LineDao.deleteAll();
    }

    @DisplayName("새 지하철 노선을 저장한다.")
    @Test
    void save() {
        Line testLine = new Line("test", "GREEN");
        Line result = LineDao.save(testLine);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getColor()).isEqualTo("GREEN");
    }

    @DisplayName("지하철 노선 이름을 이용해 지하철 노선을 조회한다.")
    @Test
    void findByName() {
        Line test = new Line("test", "GREEN");
        LineDao.save(test);
        Line result = LineDao.findByName("test").orElse(null);
        Optional<Line> result2 = LineDao.findByName("test2");

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getColor()).isEqualTo("GREEN");
        assertThat(result2).isEmpty();
    }
}
