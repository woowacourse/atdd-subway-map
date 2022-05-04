package wooteco.subway.service;

import java.util.List;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

public interface LineService {

    Line save(LineRequest lineRequest);

    List<Line> findAll();

    Line findById(Long id);

    void update(Long id, LineRequest lineRequest);

    void deleteById(Long id);
}
