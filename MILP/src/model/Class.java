package model;

import java.util.ArrayList;

public class Class {
    private int ide;//�༶���
    private int[][][] schedule;//�ſμƻ���Ϊ0��ʾ��ʱ��λ�δ�ſ� Ϊ-1��ʾ��ʱ��β����ſγ� ��Ϊ0��-1��ʾ��ʱ��ΰ�����ͨʶ��
    private ArrayList<Course> sCourse;//ѧϰ�Ŀγ̼���

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
