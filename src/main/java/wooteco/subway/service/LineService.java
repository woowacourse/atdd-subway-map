package wooteco.subway.service;

import java.util.List;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

public interface LineService {

    Line save(LineRequest lineRequest);

    Line findById(Long id);

    List<Line> findAll();

    void update(Long id, LineRequest lineRequest);

    void deleteById(Long id);
}
