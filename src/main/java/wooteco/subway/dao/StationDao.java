package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Station;

public interface StationDao {

	Long save(Station station);

	List<Station> findAll();

	Station findById(Long id);

	Boolean existsByName(String name);

	void remove(Long id);
}
