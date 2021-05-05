package wooteco.subway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.repository.LineJdbcDao;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.station.repository.StationJdbcDao;
import wooteco.subway.station.repository.StationRepository;

@Configuration
public class SubwayConfig {

    private final JdbcTemplate jdbcTemplate;

    public SubwayConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public LineRepository lineRepository() {
//        return new LineDao(); // 1단계
        return new LineJdbcDao(jdbcTemplate); // 2단계
    }

    @Bean
    public StationRepository stationRepository() {
//        return new StationDao(); // 1단계
        return new StationJdbcDao(jdbcTemplate); // 2단계
    }
}
