package wooteco.subway.admin.common.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import wooteco.subway.admin.line.domain.Line;
import wooteco.subway.admin.line.domain.edge.Edge;
import wooteco.subway.admin.line.domain.repository.LineRepository;
import wooteco.subway.admin.station.domain.Station;
import wooteco.subway.admin.station.domain.repository.StationRepository;

import java.time.LocalTime;

@Configuration
public class DefaultDataConfiguration {

    @Profile("local")
    @Configuration
    private static class LocalDefaultDataConfiguration implements ApplicationRunner {

        private final LineRepository lineRepository;
        private final StationRepository stationRepository;

        public LocalDefaultDataConfiguration(final LineRepository lineRepository, final StationRepository stationRepository) {
            this.lineRepository = lineRepository;
            this.stationRepository = stationRepository;
        }

        @Override
        public void run(final ApplicationArguments args) throws Exception {
            Line airLine = lineRepository.save(new Line("공항철도", LocalTime.of(5, 10), LocalTime.of(23, 30), 10, "bg-blue-200"));
            Line LineNumberOne = lineRepository.save(new Line("1호선", LocalTime.of(5, 20), LocalTime.of(23, 50), 10, "bg-blue-500"));
            Station station1 = stationRepository.save(new Station("검암역"));
            Station station2 = stationRepository.save(new Station("계양역"));
            Station station3 = stationRepository.save(new Station("김포공항역"));
            Station station4 = stationRepository.save(new Station("마곡나루역"));
            Station station5 = stationRepository.save(new Station("서울역"));
            Station station6 = stationRepository.save(new Station("용산역"));
            airLine.addEdge(new Edge(station1.getId(), station2.getId(), 10, 10));
            airLine.addEdge(new Edge(station2.getId(), station3.getId(), 10, 10));
            airLine.addEdge(new Edge(station3.getId(), station4.getId(), 10, 10));
            lineRepository.save(airLine);
        }
    }
}
