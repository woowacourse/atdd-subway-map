package wooteco.subway.service;

import java.util.List;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.LineServiceRequest;

public interface LineService {

    Line save(LineServiceRequest lineServiceRequest);

    List<Line> findAll();

    Line findById(Long id);

    void update(Long id, LineServiceRequest lineServiceRequest);

    void deleteById(Long id);
}
