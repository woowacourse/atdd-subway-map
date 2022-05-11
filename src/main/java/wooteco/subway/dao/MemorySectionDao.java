package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

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
    public Optional<Section> findById(long id) {
        return sections.stream()
                .filter(section -> section.getId().equals(id))
                .findAny();
    }
}
