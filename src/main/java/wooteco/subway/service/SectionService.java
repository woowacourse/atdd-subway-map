package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void firstSave(Long lineId, SectionRequest sectionRequest) {
        sectionDao.save(new Section(
            null, lineId,
            sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(),
            sectionRequest.getDistance(),
            1L));
    }

    public void save(Long lineId, SectionRequest sectionReq) {
        long upStationId = sectionReq.getUpStationId();
        long downStationId = sectionReq.getDownStationId();
        int distance = sectionReq.getDistance();

        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.validateSection(upStationId, downStationId, distance);

        List<Section> targetSections = sections.findOverlapSection(upStationId, downStationId,
            distance);
        updateSections(targetSections);
    }

    private void updateSections(List<Section> sections) {
        updateFrontSection(sections.get(0));
        updateBackSection(sections.get(1));
    }

    private void updateFrontSection(Section section) {
        sectionDao.deleteById(section.getId());
        sectionDao.save(section);
    }

    private void updateBackSection(Section section) {
        sectionDao.updateLineOrderByInc(section.getLineId(), section.getLineOrder());
        sectionDao.save(section);
    }

    public List<Long> findAllStationByLineId(long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.getStationsId();
    }

    public void deleteByLineIdAndStationId(long lineId, long stationId) {
        Sections sections = new Sections(sectionDao.findByLineIdAndStationId(lineId, stationId));
        if (sections.hasTwoSection()) {
            Section upsideSection = sections.getUpsideSection();
            Section downsideSection = sections.getDownsideSection();

            deleteAndUnionTwoSection(lineId, upsideSection, downsideSection);
            return;
        }
        deleteSingleSection(lineId, sections);
    }

    private void deleteAndUnionTwoSection(long lineId, Section upsideSection,
                                          Section downsideSection) {
        sectionDao.deleteById(upsideSection.getId());
        sectionDao.deleteById(downsideSection.getId());
        sectionDao.save(new Section(null, lineId,
            upsideSection.getUpStationId(), downsideSection.getDownStationId(),
            upsideSection.getDistance() + downsideSection.getDistance(),
            upsideSection.getLineOrder()));

        sectionDao.updateLineOrderByDec(lineId, downsideSection.getLineOrder());
    }

    private void deleteSingleSection(long lineId, Sections sections) {
        Section section = sections.getSingleDeleteSection();
        sectionDao.deleteById(section.getId());

        sectionDao.updateLineOrderByDec(lineId, section.getLineOrder());
    }
}
