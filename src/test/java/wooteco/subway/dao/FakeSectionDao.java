package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public class FakeSectionDao implements SectionDao {

    private static final int EXECUTED_COLUMN_COUNT_ONE = 1;
    private static final int EXECUTED_COLUMN_COUNT_NONE = 0;

    private Long seq = 0L;
    private final Map<Long, Section> sections = new HashMap<>();

    @Override
    public Section save(Section section) {
        Long id = ++seq;
        sections.put(id, new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance()));
        return sections.get(id);
    }

    @Override
    public Sections findByLineId(Long lineId) {
        return new Sections(sections.values()
                .stream()
                .filter(section -> section.getLineId().equals(lineId))
                .collect(Collectors.toList()), lineId);
    }

    @Override
    public int delete(Long id) {
        if (sections.containsKey(id)) {
            sections.remove(id);
            return EXECUTED_COLUMN_COUNT_ONE;
        }
        return EXECUTED_COLUMN_COUNT_NONE;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        for (Long id : ids) {
            sections.remove(id);
        }
        return ids.size();
    }

    @Override
    public int update(Section section) {
        Long id = section.getId();
        if (sections.containsKey(id)) {
            sections.put(id, section);
            return 1;
        }
        return 0;
    }
}
