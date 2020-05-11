package wooteco.subway.admin.station.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import wooteco.subway.admin.station.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {

	@Override
	List<Station> findAll();

	@Override
	List<Station> findAllById(Iterable<Long> longs);

}