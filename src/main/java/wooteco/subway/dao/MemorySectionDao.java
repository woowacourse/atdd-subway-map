package wooteco.subway.dao;

import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MemorySectionDao implements SectionDao {

    private List<Section> sections = new ArrayList<>();
    private AtomicLong sequence = new AtomicLong();

    @Override
    public long save(Section section) {
        section.setId(sequence.incrementAndGet());
        sections.add(section);
        return sequence.get();
    }

    @Override
    public Optional<Section> findById(Long id) {
        return sections.stream()
                .filter(section -> section.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        return sections.stream()
                .filter(section -> section.getLineId().equals(lineId))
                .collect(Collectors.toList());
    }

    @Override
    public long update(Section section) {
        Section found = findById(section.getId()).get();
        sections.remove(found);
        sections.add(section);
        return section.getId();
    }

    @Override
    public void deleteSection(Section upperSection) {
        sections.remove(upperSection);
    }
}
