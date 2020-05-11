package wooteco.subway.admin.line.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import wooteco.subway.admin.line.domain.line.Line;

public interface LineRepository extends CrudRepository<Line, Long> {

	@Override
	List<Line> findAll();

}
