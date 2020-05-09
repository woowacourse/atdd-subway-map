package wooteco.subway.admin.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import wooteco.subway.admin.domain.Line;

import java.util.List;

@Repository
public interface LineRepository extends CrudRepository<Line, Long> {
    @Override
    List<Line> findAll();
}
