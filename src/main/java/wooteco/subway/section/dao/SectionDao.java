package wooteco.subway.section.dao;

import wooteco.subway.section.Section;

import java.util.List;

public interface SectionDao {
    Section save(Section section);

    List<Section> findAllByLineId(Long id);

    void update(Section section);

    void deleteById(Long id);
}
