package wooteco.subway.repository.dao;

import java.util.List;

import wooteco.subway.repository.dao.dto.SectionDto;

public interface SectionDao {

    Long save(SectionDto section);

    List<SectionDto> findAllByLineId(Long lineId);

    void remove(Long id);
}
