package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.*;
import wooteco.subway.dto.SectionRequest;

import java.util.*;

@Transactional
@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest) {
        Section section = sectionRequest.toSection(lineId);
        Sections sections = new Sections(sectionDao.findByLineId(lineId), new ConcreteCreationStrategy(), new ConcreteDeletionStrategy(), new ConcreteSortStrategy());

        sections.save(section);
        Optional<Section> revisedSection = sections.fixOverLappedSection(section);

        sectionDao.save(section);
        revisedSection.ifPresent(sectionDao::update);
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId), new ConcreteCreationStrategy(), new ConcreteDeletionStrategy(), new ConcreteSortStrategy());

        Optional<Section> connectedSection = sections.fixDisconnectedSection(lineId, stationId);
        sections.delete(lineId, stationId);

        sectionDao.delete(lineId, stationId);
        connectedSection.ifPresent(sectionDao::save);
    }
}
