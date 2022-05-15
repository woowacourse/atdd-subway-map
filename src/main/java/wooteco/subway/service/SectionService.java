package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void firstSave(Long lineId, SectionRequest sectionRequest) {
        sectionDao.save(
                createSection(lineId,
                        sectionRequest.getUpStationId(),
                        sectionRequest.getDownStationId(),
                        sectionRequest.getDistance(),
                        1L));
    }

    public void save(Long lineId, SectionRequest sectionReq) {
        save(lineId, sectionReq.getUpStationId(), sectionReq.getDownStationId(), sectionReq.getDistance());
    }

    private void save(Long lineId, long upStationId, long downStationId, int distance) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.validateSection(upStationId, downStationId, distance);

        List<Section> seperatedSections = sections.findSeperatedSection(upStationId, downStationId, distance);
        saveFrontSection(seperatedSections.get(0));
        saveBackSection(seperatedSections.get(1));
    }

    private void saveFrontSection(Section section) {
        sectionDao.deleteById(section.getId());
        sectionDao.save(section);
    }

    private void saveBackSection(Section section) {
        sectionDao.updateLineOrder(section.getLineId(), section.getLineOrder());
        sectionDao.save(section);
    }

    private Section createSection(Long lineId, long upStationId, long downStationId, int distance, Long lineOrder) {
        return new Section(null, lineId, upStationId, downStationId, distance, lineOrder);
    }

    public List<Long> findAllStationByLineId(long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.getStationsId();
    }

    public void deleteByLineIdAndStationId(long lineId, long stationId) {
        Sections sections = new Sections(sectionDao.findByLineIdAndStationId(lineId, stationId));

        if (sections.hasTwoSection()) {
            Section upsideSection = sections.getUpsideEndSection();
            Section downsideSection = sections.getDownsideEndSection();

            deleteAndUnionTwoSection(lineId, upsideSection, downsideSection);
            return;
        }

        deleteSingleSection(lineId, sections);
    }

    private void deleteAndUnionTwoSection(long lineId, Section upsideSection, Section downsideSection) {
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
