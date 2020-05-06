package wooteco.subway.admin.repository;

import org.springframework.data.repository.CrudRepository;
import wooteco.subway.admin.domain.Station;

import java.util.List;

public interface StationRepository extends CrudRepository<Station, Long> {
	@Override
	List<Station> findAll();
}