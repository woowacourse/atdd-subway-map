package wooteco.subway.section.repository;

import java.util.List;
import wooteco.subway.section.model.Section;

public interface SectionRepository {

    List<Section> findSectionsByLineId(Long id);

    void save(Section section);

    void deleteAllByLineId(Long id);

    void saveAll(List<Section> sections);
}
