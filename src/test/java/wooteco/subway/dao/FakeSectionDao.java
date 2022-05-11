package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.domain.Section;

public class FakeSectionDao implements SectionDao {

    private Long seq = 0L;
    private final List<Section> sections = new ArrayList<>();

    @Override
    public void save(final Section section) {
        Section newSection =
                new Section(++seq, section.getLindId(), section.getUpStationId(),
                        section.getDownStationId(), section.getDistance());
        sections.add(newSection);
    }

    @Override
    public List<Section> findByLineId(Long lindId) {
        return sections.stream()
                .filter(it -> it.getLindId().equals(lindId))
                .collect(Collectors.toList());
    }

    @Override
    public void update(final Section section) {
        final Optional<Section> changedSection = sections.stream()
                .filter(it -> it.getId().equals(section.getId()))
                .findAny();
        changedSection.ifPresent(value -> sections.set(sections.indexOf(value), section));
    }
}
