package wooteco.subway.admin.repository;

import org.springframework.data.repository.CrudRepository;
import wooteco.subway.admin.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends CrudRepository<Line, Long> {
    @Override
    List<Line> findAll();

    @Override
    Optional<Line> findById(Long id);
}
