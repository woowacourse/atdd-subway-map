package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineDto;

public interface LineDao {

    LineDto save(LineDto lineDto);

    List<LineDto> findAll();

    LineDto findById(Long id);

    LineDto update(LineDto lineDto);

    void deleteById(Long id);
}
