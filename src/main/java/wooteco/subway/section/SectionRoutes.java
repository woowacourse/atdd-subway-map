package wooteco.subway.section;

import java.util.Map;
import java.util.Map.Entry;
import wooteco.subway.line.LineEndPoint;

public class SectionRoutes {

    Map<Long, Long> route;

    public SectionRoutes(Map<Long, Long> route) {
        this.route = route;
    }


    public LineEndPoint findEndPointInRoute(Map<Long, Long> route) {
        long upStationId = 0L;
        long downStationId = 0L;

        for (Entry<Long, Long> entry : route.entrySet()) {
            if (entry.getValue() == 1L) {
                upStationId = entry.getKey();
            }
            if (entry.getValue() == -1L) {
                downStationId = entry.getKey();
            }
        }
        return new LineEndPoint(upStationId, downStationId);
    }
}
