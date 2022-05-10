package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.domain.Section;

public class FakeSectionDao implements SectionDao {

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
    public List<Section> findByLineId(Long lineId) {
        return sections.values()
                .stream()
                .filter(section -> section.getLineId().equals(lineId))
                .collect(Collectors.toList());
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
