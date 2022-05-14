package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wooteco.subway.domain.Section;

public class FakeSectionDao implements SectionDao {

    private Long seq = 0L;
    private final Map<Long, Section> sections = new HashMap<>();

    @Override
    public Long save(Section section, Long lineId) {
        Section newSection = Section.from(++seq, section);
        sections.put(seq, newSection);
        return seq;
    }

    @Override
    public void update(List<Section> sections) {
        for (Section section : sections) {
            this.sections.replace(section.getId(), section);
        }
    }

    @Override
    public boolean delete(Long id) {
        return sections.remove(id) != null;
    }
}
