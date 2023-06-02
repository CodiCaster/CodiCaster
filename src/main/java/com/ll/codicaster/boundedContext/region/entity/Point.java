package com.ll.codicaster.boundedContext.region.entity;

import lombok.Data;

@Data
public class Point {
    private final int x;
    private final int y;

    public Point(double x, double y) {
        this.x = (int) x;
        this.y = (int) y;
    }
}
