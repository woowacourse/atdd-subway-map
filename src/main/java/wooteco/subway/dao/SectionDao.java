package wooteco.subway.dao;

import wooteco.subway.dto.SectionDto;

public interface SectionDao {

    SectionDto save(SectionDto sectionDto);

    SectionDto findById(Long id);
}
