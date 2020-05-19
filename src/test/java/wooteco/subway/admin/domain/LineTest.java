package wooteco.subway.admin.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    private Line line;

    @Test
    void getEdges() {
        line = new Line(1L,"비내리는호남선",  LocalTime.of(05, 30), LocalTime.of(22, 30),5, "bg-yellow-700");
        line.addEdge(new Edge(null, 1L, 10, 10));
        line.addEdge(new Edge(1L, 2L, 10, 10));
        line.addEdge(new Edge(2L,3L,10,10));

        List<Long> stationIds = line.getEdgeIds();

        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(1)).isEqualTo(2L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
    }

    @Test
    void removeOneEdge() {
        line = new Line(1L,"비내리는호남선",  LocalTime.of(05, 30), LocalTime.of(22, 30),5, "bg-yellow-700");
        line.addEdge(new Edge(null, 1L, 10, 10));
        line.addEdge(new Edge(1L, 2L, 10, 10));
        line.addEdge(new Edge(2L,3L,10,10));

        line.removeEdgeById(1L);

        assertThat(line.getEdgeIds()).hasSize(2);

        line.removeEdgeById(2L);

        assertThat(line.getEdgeIds()).hasSize(1);

        line.removeEdgeById(3L);

        assertThat(line.getEdgeIds()).hasSize(0);

    }
}
