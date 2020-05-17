package wooteco.subway.admin.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Line;

public interface LineRepository extends CrudRepository<Line, Long> {
	@Override
	List<Line> findAll();

	@Query("SELECT CASE WHEN COUNT(name) > 0 THEN true ELSE false END FROM LINE WHERE LINE.name = :name")
	boolean existsByName(@Param("name") String name);
}
