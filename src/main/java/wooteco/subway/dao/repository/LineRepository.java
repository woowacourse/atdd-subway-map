package wooteco.subway.dao.repository;

import java.util.List;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

public interface LineRepository {
	Long save(Line line);

	List<Line> findAll();

	Line findById(Long id);

	void update(Line line);

	void remove(Long id);

	Boolean existsByName(String name);
}
