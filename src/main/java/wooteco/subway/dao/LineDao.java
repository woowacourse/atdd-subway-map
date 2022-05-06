package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

public interface LineDao {

    Line save(LineRequest lineRequest);

    List<Line> findAll();

    Line find(Long id);

    int update(Long id, LineRequest lineRequest);

    int delete(Long id);
}
