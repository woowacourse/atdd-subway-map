package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Sql("classpath:schema.sql")
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("노선 이름, 색을 입력하면 노선을 저장하고 Line 객체를 반환한다")
    @Test
    void save() {
        String name = "2호선";
        String color = "red";
        assertThat(lineDao.save(name, color)).isInstanceOf(Line.class);
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void findAll() {
        String name = "2호선";
        String color = "green";
        lineDao.save(name, color);

        String name2 = "3호선";
        String color2 = "orange";
        lineDao.save(name2, color2);

        assertThat(lineDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("id로 노선을 조회한다")
    @Test
    void findById() {
        String name = "2호선";
        String color = "green";
        Line savedLine = lineDao.save(name, color);

        final Line line = lineDao.findById(savedLine.getId());

        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @DisplayName("노선의 이름과 색상을 수정한다")
    @Test
    void update() {
        String name = "2호선";
        String color = "green";
        Line savedLine = lineDao.save(name, color);

        String newName = "3호선";
        String newColor = "orange";
        lineDao.update(savedLine.getId(), newName, newColor);

        final Line line = lineDao.findById(savedLine.getId());
        assertThat(line.getName()).isEqualTo(newName);
        assertThat(line.getColor()).isEqualTo(newColor);
    }

    @DisplayName("id로 노선을 삭제한다")
    @Test
    void delete() {
        String name = "2호선";
        String color = "green";
        lineDao.save(name, color);

        String name2 = "3호선";
        String color2 = "orange";
        Line savedLine = lineDao.save(name2, color2);

        lineDao.delete(savedLine.getId());
        assertThat(lineDao.findAll().size()).isEqualTo(1);
    }
}