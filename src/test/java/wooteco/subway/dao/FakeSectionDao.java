package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.Section;

public class FakeSectionDao implements SectionDao {

    private final List<Section> sections = new ArrayList<>();

    @Override
    public void save(final Section section) {
        sections.add(section);
    }

    @Override
    public List<Section> findByLineId(Long lindId) {
        return sections.stream()
                .filter(it -> it.getLindId().equals(lindId))
                .collect(Collectors.toUnmodifiableList());
    }
}
