package me.bristermitten.warzone.data;

import me.bristermitten.warzone.util.Numbers;

public record Region(Point min, Point max) {

    public Region realised() {
        return realiseRegion(this);
    }
    public static Region realiseRegion(Region region) {
        var x = Numbers.minMax(region.min().x(), region.max().x());
        var y = Numbers.minMax(region.min().y(), region.max().y());
        var z = Numbers.minMax(region.min().z(), region.max().z());
        var min = new Point(
                x._1,
                y._1,
                z._1
        );
        var max = new Point(
                x._2,
                y._2,
                z._2
        );
        return new Region(min, max);
    }
}
