package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

import java.util.List;

@Repository
@Transactional
public interface LineRepository extends CrudRepository<Line, Long> {
    @Override
    List<Line> findAll();
}
