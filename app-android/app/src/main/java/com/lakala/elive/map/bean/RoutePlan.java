package com.lakala.elive.map.bean;

import java.util.List;

/**
 * 路线规划得到的结果
 * Created by xiaogu on 2017/3/14.
 */
public class RoutePlan {
    private int status;
    private String line;
    private int distance;
    private List<Integer> waypoint_order;//得到的顺序
    private List<Integer> legs_distance;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<Integer> getWaypoint_order() {
        return waypoint_order;
    }

    public void setWaypoint_order(List<Integer> waypoint_order) {
        this.waypoint_order = waypoint_order;
    }

    public List<Integer> getLegs_distance() {
        return legs_distance;
    }

    public void setLegs_distance(List<Integer> legs_distance) {
        this.legs_distance = legs_distance;
    }
}
