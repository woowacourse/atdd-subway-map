package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.LineDto;

public interface CommonLineDao {

    Line save(final LineDto lineDto);

    List<Line> findAll();

    Line findById(final Long id);

    int update(final Long id, final Line line);

    int deleteById(final Long id);
}
