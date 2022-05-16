package wooteco.subway.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.*;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionsResponse;
import wooteco.subway.exception.ClientException;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void save(Long id, SectionRequest request, SectionsResponse response, LineResponse line) {
        validateLineExist(id);
        Sections sections = new Sections(response.getSections());
        sections.validateUpAndDownSameStation(request);
        sections.validateSaveCondition(request, line);

        if (sections.isAddSectionMiddle(request)) {
            addMiddleSection(id, request, sections);
            return;
        }
        sectionDao.save(id, new Section(id, request.getUpStationId(), request.getDownStationId(), request.getDistance()));
    }

    private void addMiddleSection(Long id, SectionRequest request, Sections sections) {
        Optional<Section> targetSection = sections.getSections()
                .stream()
                .filter(section -> section.hasSameStationId(request))
                .findAny();

        targetSection.ifPresent(section -> addSectionBySameStationId(id, request, section));
    }

    private void addSectionBySameStationId(Long id, SectionRequest request, Section section) {
        sectionDao.save(id, section.createBySameStationId(id, request));
        sectionDao.delete(id, section);

        section.updateSameStationId(request);
        sectionDao.save(id, section);
    }

    @Transactional
    public void delete(Long id, Long stationId) {
        validateLineExist(id);
        validateStationExist(stationId);
        sectionDao.findById(id).validateDeleteCondition();

        Sections sections = sectionDao.findById(id);
        if (sections.isMiddleSection(stationId)) {
            deleteMiddleSection(id, sections.findDownSection(stationId).get(), sections.findUpSection(stationId).get());
            return;
        }
        deleteEndSection(id, sections.findDownSection(stationId), sections.findUpSection(stationId));
    }

    private void validateLineExist(Long id) {
        if (!lineDao.isExistById(id)) {
            throw new ClientException("존재하지 않는 노선입니다.");
        }
    }

    private void validateStationExist(Long id) {
        if (!stationDao.isExistById(id)) {
            throw new ClientException("존재하지 않는 역입니다.");
        }
    }

    private void deleteMiddleSection(Long id, Section downSection, Section upSection) {
        sectionDao.save(id, new Section(id, upSection.getUpStationId(), downSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance()));
        sectionDao.delete(id, downSection);
        sectionDao.delete(id, upSection);
    }

    private void deleteEndSection(Long id, Optional<Section> downSection, Optional<Section> upSection) {
        if (upSection.isEmpty()) {
            sectionDao.delete(id, downSection.get());
        }
        if (downSection.isEmpty()) {
            sectionDao.delete(id, upSection.get());
        }
    }
}
