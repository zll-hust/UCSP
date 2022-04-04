package model;

import java.util.ArrayList;

public class Course {
    private int ide;//���
    private int[] classes;//��Ӧ�༶���
    private int[] tIde;//�ڿν�ʦ���
    private int classHours;
    private String[] softContraints;//��Ӧ��Լ�����

    public Course()
    {
        this.ide = 0;
        this.classes = null;
        this.tIde = null;
        this.classHours = 0;
        this.softContraints = null;
    }

    public Course(int ide, int[] classes, int[] tIde, int classHours, String[] softContraints)
    {
        this.ide = ide;
        this.classes = classes;
        this.tIde = tIde;
        this.classHours = classHours;
        this.softContraints = softContraints;
    }
    
    public int getIde()
    {
    	return this.ide;
    }
    
    public void setIde(int ide)
    {
    	this.ide = ide;
    }
    
    public int[] getTIde()
    {
    	return this.tIde;
    }
    
    public void setTide(int[] tIde)
    {
    	this.tIde = tIde;
    }
    
    public int getClassHours()
    {
    	return this.classHours;
    }
    
    public void setClassHours(int classHours)
    {
    	this.classHours = classHours;
    }
    
    public int[] getClasses()
    {
    	return this.classes;
    }
    
    public void setClasses(int[] classes)
    {
    	this.classes = classes;
    }
    
    public String[] getSoftConstraints()
    {
    	return this.softContraints;
    }
    
    public void setSoftConstraints(String[] softConstraints)
    {
    	this.softContraints = softConstraints;
    }
}
