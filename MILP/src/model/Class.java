package model;

import java.util.ArrayList;

public class Class {
    private int ide;//班级编号
    private int[][][] schedule;//排课计划，为0表示此时间段还未排课 为-1表示此时间段不安排课程 不为0或-1表示此时间段安排了通识课
    private ArrayList<Course> sCourse;//学习的课程集合

    public Class()
    {
        this.ide = 0;
        this.schedule = new int[18][5][5];
    }

    public Class(int ide)
    {
        this.ide = ide;
        this.schedule = new int[18][5][5];
    }
    
    public Class(int ide, int[][][] schedule)
    {
        this.ide = ide;
        this.schedule = schedule;

    }
    
    public int getIde()
    {
    	return this.ide;
    }
    
    public void setIde(int ide)
    {
    	this.ide = ide;
    }

    public int[][][] getSchedule()
    {
    	return this.schedule;
    }
    
    public void setSchedlue(int[][][] schedule)
    {
    	this.schedule = schedule;
    }
    
    public ArrayList<Course> getsCourse()
    {
    	return this.sCourse;
    }
    
    public void settCourse(ArrayList<Course> sCourse)
    {
    	this.sCourse = sCourse;
    }
}
