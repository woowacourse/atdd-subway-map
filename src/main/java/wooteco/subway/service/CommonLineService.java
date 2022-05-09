package wooteco.subway.service;

import java.util.List;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

public interface CommonLineService {

    Line save(final LineRequest lineRequest);

    Line findById(final Long id);

    List<Line> findAll();

    void update(final Long id, final LineRequest lineRequest);

    void deleteById(final Long id);
}
