package wooteco.subway.admin.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    private Line line;

    @Test
    void getEdgesTest() {
        line = new Line(1L, "비내리는호남선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-yellow-700");
        line.addEdge(new Edge(null, 1L, 10, 10));
        line.addEdge(new Edge(1L, 2L, 10, 10));
        line.addEdge(new Edge(2L, 3L, 10, 10));

        List<Long> stationIds = line.getEdgeIds();

        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(1)).isEqualTo(2L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
    }

    @Test
    void removeOneEdgeTest() {
        line = new Line(1L, "비내리는호남선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-yellow-700");
        line.addEdge(new Edge(null, 1L, 10, 10));
        line.addEdge(new Edge(1L, 2L, 10, 10));
        line.addEdge(new Edge(2L, 3L, 10, 10));

        line.removeEdgeById(1L);
        assertThat(line.getEdges()).hasSize(2);
        line.removeEdgeById(2L);
        assertThat(line.getEdges()).hasSize(2);
        line.removeEdgeById(3L);
        assertThat(line.getEdges()).hasSize(2);
    }

    @DisplayName("간선이 없을때 빈 리스트가 반환되는지 테스트")
    @Test
    void getSortedEdgesTest1() {
        line = new Line(1L, "비내리는호남선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-yellow-700");
        List<Edge> sortedEdges = line.getSortedEdges();
        assertThat(sortedEdges.size()).isEqualTo(0);
    }

    @DisplayName("간선이 1개일때 원소가 1개인 리스트가 잘 반환되는지 테스트")
    @Test
    void getSortedEdgesTest2() {
        line = new Line(1L, "비내리는호남선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-yellow-700");
        Edge edge1 = new Edge(null, 3L, 10, 10);
        line.addEdge(edge1);
        List<Edge> sortedEdges = line.getSortedEdges();
        assertThat(sortedEdges.size()).isEqualTo(1);
        assertThat(sortedEdges.get(0)).isEqualTo(edge1);
    }

    @DisplayName("간선이 2개 이상일때 순서대로 잘 계산하는지 테스트")
    @Test
    void getSortedEdgesTest3() {
        // 3--1--5--7--2--4
        line = new Line(1L, "비내리는호남선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-yellow-700");
        Edge edge1 = new Edge(1L, 5L, 10, 10);
        Edge edge2 = new Edge(7L, 2L, 10, 10);
        Edge edge3 = new Edge(2L, 4L, 10, 10);
        Edge edge4 = new Edge(null, 3L, 10, 10);
        Edge edge5 = new Edge(5L, 7L, 10, 10);
        Edge edge6 = new Edge(3L, 1L, 10, 10);

        line.addEdge(edge1);
        line.addEdge(edge2);
        line.addEdge(edge3);
        line.addEdge(edge4);
        line.addEdge(edge5);
        line.addEdge(edge6);

        List<Edge> sortedEdges = line.getSortedEdges();
        assertThat(sortedEdges.size()).isEqualTo(6);
        assertThat(sortedEdges.get(0)).isEqualTo(edge4);
        assertThat(sortedEdges.get(1)).isEqualTo(edge6);
        assertThat(sortedEdges.get(2)).isEqualTo(edge1);
        assertThat(sortedEdges.get(3)).isEqualTo(edge5);
        assertThat(sortedEdges.get(4)).isEqualTo(edge2);
        assertThat(sortedEdges.get(5)).isEqualTo(edge3);
    }
}
