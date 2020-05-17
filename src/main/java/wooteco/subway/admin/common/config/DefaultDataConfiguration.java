package wooteco.subway.admin.common.config;

import java.time.LocalTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import wooteco.subway.admin.line.domain.line.Line;
import wooteco.subway.admin.line.repository.LineRepository;
import wooteco.subway.admin.station.domain.Station;
import wooteco.subway.admin.station.repository.StationRepository;

@Configuration
public class DefaultDataConfiguration {

	@Profile("local")
	@Configuration
	private static class LocalDefaultDataConfiguration implements ApplicationRunner {

		private final LineRepository lineRepository;
		private final StationRepository stationRepository;

		public LocalDefaultDataConfiguration(final LineRepository lineRepository,
			final StationRepository stationRepository) {
			this.lineRepository = lineRepository;
			this.stationRepository = stationRepository;
		}

		@Override
		public void run(ApplicationArguments args) throws Exception {
			configureLines();
			configureStations();
		}

		private void configureLines() {
			lineRepository.save(new Line("1호선", LocalTime.of(5, 10), LocalTime.of(23, 30), 10, "bg-blue-700"));
			lineRepository.save(new Line("2호선", LocalTime.of(5, 10), LocalTime.of(23, 30), 10, "bg-green-500"));
			lineRepository.save(new Line("2호선", LocalTime.of(5, 10), LocalTime.of(23, 30), 10, "bg-blue-500"));
		}

		private void configureStations() {
			stationRepository.save(new Station("당산"));
			stationRepository.save(new Station("합정"));
			stationRepository.save(new Station("홍대입구"));
			stationRepository.save(new Station("신촌"));
			stationRepository.save(new Station("이대"));
			stationRepository.save(new Station("아현"));
			stationRepository.save(new Station("충정로"));
			stationRepository.save(new Station("시청"));
			stationRepository.save(new Station("서울역"));
			stationRepository.save(new Station("남영"));
			stationRepository.save(new Station("용산"));
			stationRepository.save(new Station("노량진"));
			stationRepository.save(new Station("숙대입구"));
			stationRepository.save(new Station("삼각지"));
			stationRepository.save(new Station("신용산"));
			stationRepository.save(new Station("이촌"));
			stationRepository.save(new Station("동작"));
			stationRepository.save(new Station("이수"));
		}
	}
}
