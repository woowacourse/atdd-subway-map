package wooteco.subway.service.section;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionDelete;

@Transactional
@Service
public class SectionDeleteService {
    private final SectionDao sectionDao;

    public SectionDeleteService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void deleteSectionById(Long lineId, Long stationIdToDelete) {
        List<Section> allSectionsInLine = sectionDao.findAllByLineId(lineId);
        SectionDelete sectionDelete = new SectionDelete(allSectionsInLine, stationIdToDelete);
        if (sectionDelete.isFirstOrLastStationDelete()) {
            deleteFirstOrLastStation(sectionDelete);
            return;
        }
        deleteStationFromMiddleOfLine(lineId, sectionDelete);
    }

    private void deleteFirstOrLastStation(SectionDelete sectionDelete) {
        Long sectionIdToDelete = sectionDelete.getSectionIdToDelete();
        sectionDao.deleteById(sectionIdToDelete);
    }

    private void deleteStationFromMiddleOfLine(Long lineId, SectionDelete sectionDelete) {
        Long newUpStationSectionId = sectionDelete.getNewUpStationSectionId();
        Long newDownStationSectionId = sectionDelete.getNewDownStationSectionId();
        Section newSection = sectionDelete.getNewSection(lineId);
        sectionDao.deleteById(newUpStationSectionId);
        sectionDao.deleteById(newDownStationSectionId);
        sectionDao.save(newSection);
    }
}
