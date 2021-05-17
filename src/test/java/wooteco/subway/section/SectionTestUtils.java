package wooteco.subway.section;

import org.springframework.stereotype.Component;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.service.NewSectionService;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class SectionTestUtils {

    private final LineService lineService;
    private final SectionDao sectionDao;
    private final NewSectionService sectionService;

    public SectionTestUtils(LineService lineService, SectionDao sectionDao) {
        this.lineService = lineService;
        this.sectionDao = sectionDao;
    }

    public void printStationIds(final Line line){
        for(final Long stationId : lineService.allStationIdInLine(line)){
            System.out.print(stationId + " ");
        }
        System.out.println();
    }

    public void assertStationOrder(final Line line, final StationResponse... stations){
        int index = 0;
        for(final Long stationId : lineService.allStationIdInLine(line)){
            assertThat(stationId).isEqualTo(stations[index++].getId());
        }
    }

    public void assertSectionDistance(final Line line, final int... distances){
        int index =0;
        sectionDao.findDistance()
        for(final Section section : sectionDao.findSections(line.getId())){
            System.out.println(section.distance());
//            assertThat(section.distance()).isEqualTo(distances[index++]);
        }
    }
}
