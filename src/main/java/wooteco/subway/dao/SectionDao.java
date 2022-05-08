package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.dto.SectionDto;

public interface SectionDao {

    SectionDto save(SectionDto sectionDto);

    int saveAll(List<SectionDto> sectionDtos);

    SectionDto findById(Long id);

    List<SectionDto> findByLineId(Long id);
}
