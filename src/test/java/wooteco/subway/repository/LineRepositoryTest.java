package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.LineSeries;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("저장")
    public void saveByPersist() {
        // given
        LineSeries lineSeries = new LineSeries(new ArrayList<>());
        lineSeries.add(new Line("myName", "myColor"));
        // when
        lineRepository.persist(lineSeries);
        // then
        assertThat(lineRepository.findAllLines()).hasSize(1);
    }

    @Test
    @DisplayName("업데이트")
    public void updateByPersist() {
        // given
        LineSeries lineSeries = new LineSeries(new ArrayList<>());
        final Line addLine = new Line("myName", "myColor");
        lineSeries.add(addLine);
        lineRepository.persist(lineSeries);

        // when
        lineSeries.update(new Line(addLine.getId(), "yourName", "yourColor"));
        lineRepository.persist(lineSeries);
        // then
        assertThat(lineRepository.findAllLines()).hasSize(1);
    }

    @Test
    @DisplayName("삭제")
    public void deleteByPersist() {
        // given
        LineSeries lineSeries = new LineSeries(new ArrayList<>());
        final Line addLine = new Line("myName", "myColor");
        lineSeries.add(addLine);
        lineRepository.persist(lineSeries);

        // when
        lineSeries.delete(addLine.getId());
        lineRepository.persist(lineSeries);

        // then
        assertThat(lineRepository.findAllLines()).hasSize(0);
    }
}