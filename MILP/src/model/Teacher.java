package model;
import java.util.ArrayList;

public class Teacher {
	private int ide;//���
//	private ArrayList<ArrayList<Integer>> period;//�ǿ���ʱ��
	private int[][][] period;//ʱ�䰲��,�洢һ�ܵ�ʱ������ļ��ܷǿ��У�ֵΪ0��ʾ��ʱ��ξ�����
	private ArrayList<Course> tCourse;//���ڵĿγ̼���
//	private ArrayList<Class> tClass;//���ڵİ༶����
	
	public Teacher()
	{
		this.ide = 0;
		this.period = new int[18][5][5];
		this.tCourse = new ArrayList<Course>();
//		this.tClass = new ArrayList<Class>();
		
//		this.period = new ArrayList<ArrayList<Integer>>();
	}
/*
	public Teacher(int ide, ArrayList<ArrayList<Integer>> period)
	{
		this.ide = ide;
		this.period = period;
	}
*/	
	public Teacher(int ide)
	{
		this.ide = ide;
		this.period = new int[18][5][5];
		this.tCourse = new ArrayList<Course>();
//		this.tClass = new ArrayList<Class>();
	}
	
	public Teacher(int ide, int[][][] period)
	{
		this.ide = ide;
		this.period = period;
		this.tCourse = new ArrayList<Course>();
//		this.tClass = new ArrayList<Class>();
	}
	
	public int getIde()
    {
    	return this.ide;
    }
    
    public void setIde(int ide)
    {
    	this.ide = ide;
    }
    
    public int[][][] getPeriod()
    {
    	return this.period;
    }
    
    public void setPeriod(int[][][] period)
    {
    	this.period = period;
    }
    
    public Teacher getTeacher(int ide)
    {
    	if(this.ide == ide)
    		return this;
    	return null;
    }
    
    public ArrayList<Course> gettCourse()
    {
    	return this.tCourse;
    }
    
    public void settCourse(ArrayList<Course> tCourse)
    {
    	this.tCourse = tCourse;
    }
/*    
    public ArrayList<Class> gettClass()
    {
    	return this.tClass;
    }
    
    public void settClass(ArrayList<Class> tClass)
    {
    	this.tClass = tClass;
    }
*/    
    

}
