package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.repository.dao.JdbcLineDao;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setup() {
        lineDao = new JdbcLineDao(jdbcTemplate);
    }

    LineEntity saveLine(String name, String color) {
        return lineDao.save(new LineEntity(null, name, color));
    }

    @Test
    @DisplayName("노선을 저장하면 저장된 노선 정보를 반환한다.")
    void save() {
        // given
        final LineEntity line = new LineEntity(null, "7호선", "bg-red-600");

        // when
        LineEntity savedLine = lineDao.save(line);

        // then
        assertThat(savedLine.getName()).isEqualTo(line.getName());
        assertThat(savedLine.getColor()).isEqualTo(line.getColor());
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void findAll() {
        // given
        String line7Name = "7호선";
        String line7Color = "bg-red-600";
        saveLine(line7Name, line7Color);

        final String line5Name = "5호선";
        final String line5Color = "bg-green-600";
        saveLine(line5Name, line5Color);

        // when
        final List<LineEntity> lines = lineDao.findAll();
        List<String> names = lines.stream()
                .map(LineEntity::getName)
                .collect(Collectors.toList());

        // then
        assertThat(lines.size()).isEqualTo(2);
        assertThat(names).containsOnly(line5Name, line7Name);
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 조회한다.")
    void findById() {
        // given
        LineEntity persistLine = saveLine("7호선", "bg-red-600");

        // when
        LineEntity actual = lineDao.findById(persistLine.getId()).get();

        // then
        assertAll(() -> {
            assertThat(actual.getName()).isEqualTo(persistLine.getName());
            assertThat(actual.getColor()).isEqualTo(persistLine.getColor());
        });
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 업데이트한다.")
    void updateById() {
        // given
        final LineEntity persistLine = saveLine("7호선", "bg-red-600");

        // when
        final LineEntity lineForUpdate = new LineEntity(persistLine.getId(), "5호선", "bg-green-600");
        int affectedRows = lineDao.update(lineForUpdate);

        // then
        assertThat(affectedRows).isOne();
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 삭제한다.")
    void deleteById() {
        // given
        final LineEntity persistLine = saveLine("7호선", "bg-red-600");

        // when
        Long id = persistLine.getId();
        Integer affectedRows = lineDao.deleteById(id);

        // then
        assertThat(affectedRows).isOne();
        assertThat(lineDao.findAll()).hasSize(0);
    }
}
