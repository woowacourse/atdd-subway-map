package wooteco.subway.admin.repository;

import org.springframework.data.repository.CrudRepository;
import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
}