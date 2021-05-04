package wooteco.subway.line;

public class LineDaoFactory {

    private static final LineDao lineDao = new LineDao();

    private LineDaoFactory() {}

    public static LineDao makeLineDao() {
        return lineDao;
    }
}
