package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.dto.SectionDto;

public interface SectionDao {

    SectionDto save(SectionDto sectionDto);

    SectionDto findById(Long id);

    List<SectionDto> findByLineId(Long id);
}
