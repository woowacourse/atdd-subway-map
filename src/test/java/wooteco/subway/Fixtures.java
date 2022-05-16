package wooteco.subway;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class Fixtures {

    public static final String HYEHWA = "혜화역";
    public static final String SINSA = "신사역";
    public static final String GANGNAM = "강남역";
    public static final String JAMSIL = "잠실역";

    public static final String LINE_2 = "2호선";
    public static final String LINE_4 = "4호선";

    public static final String RED = "bg-red-600";
    public static final String BLUE = "bg-blue-500";

    public static final Section SECTION_1_2 = new Section(new Station(1L, HYEHWA), new Station(2L, SINSA), 10);
    public static final Section SECTION_2_3 = new Section(new Station(2L, SINSA), new Station(3L, GANGNAM), 10);
    public static final Section SECTION_3_4 = new Section(new Station(3L, GANGNAM), new Station(4L, JAMSIL), 10);
    public static final Section SECTION_1_3 = new Section(new Station(1L, HYEHWA), new Station(3L, GANGNAM), 10);

    public static final Section SECTION_1_2_SHORT = new Section(new Station(1L, HYEHWA), new Station(2L, SINSA), 5);
    public static final Section SECTION_2_3_SHORT = new Section(new Station(2L, SINSA), new Station(3L, GANGNAM), 5);
}
