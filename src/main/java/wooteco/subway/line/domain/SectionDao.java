package wooteco.subway.line.domain;

import java.util.List;

public interface SectionDao {
    Section save(Section section);

    void delete(Long id);

    List<Section> findByLineId(Long id);

    void deleteByLineId(Long lineId);

    void batchInsert(List<Section> sortedSections);
}
