package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public List<Section> findAllByLineId(final Long lineId) {
        return sectionDao.findAllByLineId(lineId)
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }
}
