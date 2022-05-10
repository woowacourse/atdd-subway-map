package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
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
        Long lineId = section.getLineId();
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        if (sections.isEndSection(section)) {
            sectionDao.save(section);
            return;
        }
        Section editSection = sections.getSameStationSection(section);
        if (editSection.getUpStationId().equals(section.getUpStationId())) {
            sectionDao.updateByUpStationId(section);
            Section newSection = new Section(section.getLineId(), section.getDownStationId(),
                editSection.getDownStationId(), editSection.getDistance() - section.getDistance());
            sectionDao.save(newSection);
            return;
        }
        sectionDao.updateByDownStationId(section);
        Section newSection = new Section(section.getLineId(), section.getUpStationId(),
            editSection.getUpStationId(), editSection.getDistance() - section.getDistance());
        sectionDao.save(newSection);
    }

    public void delete(Long lineId, Long stationId) {
        List<Section> lineSections = sectionDao.findAllByLineId(lineId);
        if (lineSections.size() == 1) {
            throw new IllegalStateException("구간이 하나 남아서 삭제 할 수 없음");
        }
        Sections sections = new Sections(lineSections);
        if (sections.isMiddleStation(stationId)) {
            Section upSection = sections.findSameUpIdSection(stationId);
            Section downSection = sections.findSameDownIdSection(stationId);
            sectionDao.delete(lineId, stationId);
            sectionDao.delete(lineId, downSection.getUpStationId());
            sectionDao.save(new Section(lineId, downSection.getUpStationId(), upSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance()));
            return;
        }
        if (sections.isEndStation(stationId)) {
            sectionDao.deleteEndStation(lineId, stationId);
            return;
        }
        throw new NoSuchElementException("구간이 존재하지 않음");
    }
}
