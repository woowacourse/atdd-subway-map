package wooteco.subway.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionJdbcDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.ClientException;

@Service
public class SectionService {

    private final SectionJdbcDao sectionJdbcDao;

    public SectionService(SectionJdbcDao sectionJdbcDao) {
        this.sectionJdbcDao = sectionJdbcDao;
    }

    public void saveSection(Long id, SectionRequest sectionRequest, Sections sections, LineResponse line) {
        validateSameStation(sectionRequest);

        if (sections.isExistSection()) {
            sections.validateSaveCondition(sectionRequest, line);
            if (sections.isAddSectionMiddle(sectionRequest)) {
                addSectionMiddle(id, sectionRequest, sections);
                return;
            }
        }
        sectionJdbcDao.save(id, new Section(0L, id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance()));
    }

    private void validateSameStation(SectionRequest sectionRequest) {
        if (Objects.equals(sectionRequest.getDownStationId(), sectionRequest.getUpStationId())) {
            throw new ClientException("상행역과 하행역이 같을 수 없습니다.");
        }
    }

    private void addSectionMiddle(Long id, SectionRequest sectionRequest, Sections sections) {
        for (Section section : sections.getSections()) {
            if (section.isSameUpStationId(sectionRequest) || section.isSameDownStationId(sectionRequest)) {
                addSameStationId(id, sectionRequest, section);
            }
        }
    }

    private void addSameStationId(Long id, SectionRequest sectionRequest, Section section) {
        sectionJdbcDao.save(id, section.createBySameStationId(id, sectionRequest));
        sectionJdbcDao.delete(id, section);

        section.updateSameStationId(sectionRequest);
        sectionJdbcDao.save(id, section);
    }

    public void delete(Long id, Long stationId) {
        sectionJdbcDao.find(id).validateDeleteCondition();
        Sections sections = sectionJdbcDao.find(id);

        Optional<Section> downSection = sections.findDownSection(stationId);
        Optional<Section> upSection = sections.findUpSection(stationId);
        if (upSection.isPresent() && downSection.isPresent()) {
            deleteMiddleSection(id, downSection, upSection);
            return;
        }
        deleteEndSection(id, downSection, upSection);
    }

    private void deleteMiddleSection(Long id, Optional<Section> downSection, Optional<Section> upSection) {
        sectionJdbcDao.save(id, new Section(0L, id, upSection.get().getUpStationId(), downSection.get().getDownStationId(), upSection.get().getDistance() + downSection.get().getDistance()));
        sectionJdbcDao.delete(id, downSection.get());
        sectionJdbcDao.delete(id, upSection.get());
    }

    private void deleteEndSection(Long id, Optional<Section> downSection, Optional<Section> upSection) {
        if (upSection.isEmpty()) {
            sectionJdbcDao.delete(id, downSection.get());
        }
        if (downSection.isEmpty()) {
            sectionJdbcDao.delete(id, upSection.get());
        }
    }
}
