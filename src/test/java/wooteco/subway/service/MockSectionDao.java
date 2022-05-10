package wooteco.subway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

public class MockSectionDao implements SectionDao {

    private final Map<Long, Section> mockDb = new HashMap<>();
    private long sequenceId = 1;


    @Override
    public Long save(Section section) {
        Long id = sequenceId;
        mockDb.put(sequenceId++, createSection(id, section));
        return id;
    }

    @Override
    public Long update(Long id, Section section) {
        mockDb.put(id, createSection(id, section));
        return id;
    }

    @Override
    public Section findById(Long id) {
        return mockDb.get(id);
    }

    @Override
    public List<Section> findAllByLineId(Long lineId) {
        return mockDb.values().stream()
                .filter(section -> section.getLineId().equals(lineId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Section> findAllByStationId(Long id) {
        return mockDb.values().stream()
                .filter(section -> section.getUpStationId().equals(id) || section.getDownStationId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId) {
        return mockDb.values().stream()
                .filter(section -> section.getLineId().equals(lineId))
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst();
    }

    @Override
    public Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId) {
        return mockDb.values().stream()
                .filter(section -> section.getLineId().equals(lineId))
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        mockDb.remove(id);
    }

    @Override
    public void deleteAllByLineId(Long id) {
        List<Section> sections = mockDb.values().stream()
                .filter(section -> section.getLineId().equals(id))
                .collect(Collectors.toList());
        for (Section section : sections) {
            mockDb.remove(section.getId());
        }
    }

    @Override
    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        List<Section> sections = mockDb.values().stream()
                .filter(section -> section.getLineId().equals(lineId))
                .filter(section ->
                        section.getUpStationId().equals(stationId) || section.getDownStationId().equals(stationId))
                .collect(Collectors.toList());
        for (Section section : sections) {
            mockDb.remove(section.getId());
        }
    }

    private Section createSection(Long id, Section section) {
        return new Section(
                id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }
}
