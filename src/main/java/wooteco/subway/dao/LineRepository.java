package wooteco.subway.dao;

import wooteco.subway.dao.dto.LineUpdateDto;
import wooteco.subway.domain.Line;

import java.util.List;

public interface LineRepository {

    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void update(LineUpdateDto lineUpdateDto);

    void deleteById(Long id);
}
