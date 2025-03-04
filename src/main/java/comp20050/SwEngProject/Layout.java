package comp20050.SwEngProject;

import java.util.ArrayList;

public class Layout {
    public final Orientation o;
    public final Point size;
    public final Point origin;
    static public Orientation flat = new Orientation(3.0 / 2.0, 0.0, Math.sqrt(3.0) / 2.0, Math.sqrt(3.0), 2.0 / 3.0, 0.0, -1.0 / 3.0, Math.sqrt(3.0) / 3.0, 0.0);

    public Layout(Orientation o, Point size, Point origin) {
        this.o = o;
        this.size = size;
        this.origin = origin;
    }

    public Point hexToPoint(Hexagon h) {
        Orientation M = o;
        double x = (M.f0 * h.q + M.f1 * h.r) * size.x;
        double y = (M.f2 * h.q + M.f3 * h.r) * size.y;
        return new Point(x + origin.x, y + origin.y);
    }

    public FractionalHexagon pointToHex(Point p)
    {
        Orientation M = o;
        Point pt = new Point((p.x - origin.x) / size.x, (p.y - origin.y) / size.y);
        double q = M.b0 * pt.x + M.b1 * pt.y;
        double r = M.b2 * pt.x + M.b3 * pt.y;
        return new FractionalHexagon(q, r, -q - r);
    }

    public Point hexCornerOffset(int corner)
    {
        Orientation M = o;
        double angle = 2.0 * Math.PI * (M.start - corner) / 6.0;
        return new Point(size.x * Math.cos(angle), size.y * Math.sin(angle));
    }


    public ArrayList<Point> polygonCorners(Hexagon h)
    {
        ArrayList<Point> corners = new ArrayList<Point>(){{}};
        Point center = hexToPoint(h);
        for (int i = 0; i < 6; i++)
        {
            Point offset = hexCornerOffset(i);
            corners.add(new Point(center.x + offset.x, center.y + offset.y));
        }
        return corners;
    }
}
