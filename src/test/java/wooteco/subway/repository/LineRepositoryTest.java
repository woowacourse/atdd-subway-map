package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineSeries;

@SpringBootTest
@Sql(value = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("persist를 통해 저장한다.")
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
    @DisplayName("persist를 통해 업데이트한다.")
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
    @DisplayName("persist를 통해 삭제한다.")
    public void deleteByPersist() {
        // given
        LineSeries lineSeries = new LineSeries(new ArrayList<>());
        final Line addLine = new Line("deleteLine", "deleteColor");
        lineSeries.add(addLine);
        lineRepository.persist(lineSeries);

        // when
        lineSeries.delete(addLine.getId());
        lineRepository.persist(lineSeries);

        // then
        assertThat(lineRepository.findAllLines()).hasSize(0);
    }
}