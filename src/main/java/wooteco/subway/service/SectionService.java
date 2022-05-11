package wooteco.subway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionLinks;
import wooteco.subway.domain.Sections;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public List<Long> findAllStationIdByLineId(Long id) {
        Sections sections = new Sections(sectionDao.findAllByLineId(id));
        return sections.getAllStationId();
    }

    public void save(Section section) {
        List<Section> sections = sectionDao.findAllByLineId(section.getLineId());
        SectionLinks sectionLinks = createStations(sections);
        sectionLinks.validateAddableSection(section);
        if (sectionLinks.isEndSection(section)) {
            sectionDao.save(section);
            return;
        }
        if (sectionLinks.isExistUpStation(section.getUpStationId())) {
            Section findSection = getSameUpSection(section.getUpStationId(), sections);
            validateDistance(section, findSection);
            sectionDao.updateByUpStationId(section);
            saveNewDownSection(section, findSection);
            return;
        }
        if (sectionLinks.isExistDownStation(section.getDownStationId())) {
            Section findSection = getSameDownSection(section.getDownStationId(), sections);
            validateDistance(section, findSection);
            sectionDao.updateByDownStationId(section);
            saveNewUpSection(section, findSection);
        }
    }

    private void saveNewUpSection(Section section, Section findSection) {
        Section newSection = new Section(
            section.getLineId(),
            section.getUpStationId(),
            findSection.getUpStationId(),
            findSection.getDistance() - section.getDistance()
        );
        sectionDao.save(newSection);
    }

    private void saveNewDownSection(Section section, Section findSection) {
        Section newSection = new Section(
            section.getLineId(),
            section.getDownStationId(),
            findSection.getDownStationId(),
            findSection.getDistance() - section.getDistance()
        );
        sectionDao.save(newSection);
    }

    private void validateDistance(Section section, Section existingSection) {
        if (!existingSection.isLong(section)) {
            throw new IllegalStateException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
    }

    private Section getSameUpSection(Long id, List<Section> sections) {
        return sections.stream()
            .filter(it -> it.getUpStationId().equals(id))
            .findFirst()
            .get();
    }

    private Section getSameDownSection(Long id, List<Section> sections) {
        return sections.stream()
            .filter(it -> it.getDownStationId().equals(id))
            .findFirst()
            .get();
    }

    private SectionLinks createStations(List<Section> sections) {
        Map<Long, Long> stationIds = new HashMap<>();
        for (Section existingSection : sections) {
            stationIds.put(existingSection.getUpStationId(), existingSection.getDownStationId());
        }
        return new SectionLinks(stationIds);
    }

    public void delete(Long lineId, Long stationId) {
        List<Section> lineSections = sectionDao.findAllByLineId(lineId);
        SectionLinks sectionLinks = createStations(lineSections);
        validateSectionsSize(lineSections);
        if (sectionLinks.isExistUpStation(stationId) && sectionLinks.isExistDownStation(stationId)) {
            deleteAndSaveSections(lineId, stationId, lineSections);
            return;
        }
        if (sectionLinks.isEndStation(stationId)) {
            sectionDao.deleteEndStation(lineId, stationId);
            return;
        }
        throw new NoSuchElementException("구간이 존재하지 않음");
    }

    private void deleteAndSaveSections(Long lineId, Long stationId, List<Section> lineSections) {
        Section upSection = getSameUpSection(stationId, lineSections);
        Section downSection = getSameDownSection(stationId, lineSections);
        sectionDao.delete(lineId, stationId);
        sectionDao.delete(lineId, downSection.getUpStationId());
        sectionDao.save(new Section(lineId, downSection.getUpStationId(), upSection.getDownStationId(),
            upSection.getDistance() + downSection.getDistance()));
    }

    private void validateSectionsSize(List<Section> lineSections) {
        if (lineSections.size() == 1) {
            throw new IllegalStateException("구간이 하나 남아서 삭제 할 수 없음");
        }
    }
}
