package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import jxl.*;
import jxl.read.biff.BiffException;

public class FileManager {

    public ArrayList<Class> readClass() throws IOException, BiffException {
    	ArrayList<Class> instance = new ArrayList<Class>();
    	
    	//读取班级编号
        File ideFile = new File("T表\\T0.xls");
        Workbook ideWorkbook = Workbook.getWorkbook(ideFile);
        Sheet ideSheet = ideWorkbook.getSheets()[0];
        if (ideSheet != null) {
        	int rows = ideSheet.getRows();
            for (int row = 1; row < rows; row++) {
            		Cell cell = ideSheet.getCell(0, row);
                    instance.add(new Class(Integer.parseInt(cell.getContents()) - 1));    
             }  
        }
        ideWorkbook.close();
        
        //读取各班排课可用时间段
        int num = instance.size();//班级数
        for(int i = 1; i <= num; i++)
        {
        	String path = "T表\\T" + i + ".xls";
        	File xlsFile = new File(path);
            Workbook workbook = Workbook.getWorkbook(xlsFile);
            Sheet sheet = workbook.getSheets()[0];
            int[][][] schedule = new int[19][6][6];
            if (sheet != null) {
            	
                for (int col = 1; col <= 18; col++) {
                	int count = 0;
                	for (int row = 1; row <= 25; row++) {
                		Cell cell = sheet.getCell(col, row);
                		if(cell.getContents().startsWith("-"))
                			schedule[col - 1][count][(row - 1) % 5] = -1;
                		else
                			schedule[col - 1][count][(row - 1) % 5] = Integer.parseInt(cell.getContents());
                    	if((row - 1) % 5 == 4)
                    		count ++;
                	}
                	
                 }  
            }
            instance.get(i - 1).setSchedlue(schedule);
            workbook.close();
        }
        return instance;
    }
    
    
    public ArrayList<Teacher> readTeacher() throws IOException, BiffException {
    	ArrayList<Teacher> instance = new ArrayList<Teacher>();
    	
        File ideFile = new File("K表\\K0.xls");
        Workbook ideWorkbook = Workbook.getWorkbook(ideFile);
        Sheet ideSheet = ideWorkbook.getSheets()[0];
        if (ideSheet != null) {
        	int rows = ideSheet.getRows();
            int cols = ideSheet.getColumns();
            for (int row = 2; row < rows; row++) {
            	Cell ide = ideSheet.getCell(0, row);
            	int[][][] unfree = new int[18][5][5];
            	int day = 0;
            	
            	for (int col = 2; col < cols; col++) {
            		Cell cell = ideSheet.getCell(col, row);
            		day = (col - 2) / 5;

            		if(!cell.getContents().equals(""))
            		{
            			String[] sweeks = cell.getContents().split(",");
                		for(int i = 0; i < sweeks.length; i++)
                		{
                			unfree[Integer.parseInt(sweeks[i]) - 1][day][(col - 2) % 5] = 1;
                		}

            		}
            	}
            	instance.add(new Teacher(Integer.parseInt(ide.getContents()) - 1, unfree)); 
             }  
        }
        ideWorkbook.close();
        return instance;
    }
    
    public ArrayList<Course> readCourse() throws IOException, BiffException {
    	ArrayList<Course> instance = new ArrayList<Course>();
    	
    	//读取课程编号
        File ideFile = new File("C表\\C0.xls");
        Workbook ideWorkbook = Workbook.getWorkbook(ideFile);
        Sheet ideSheet = ideWorkbook.getSheets()[0];
        if (ideSheet != null) {
        	int rows = ideSheet.getRows();
            for (int row = 1; row < rows; row++) {
            	int ide = Integer.parseInt(ideSheet.getCell(0, row).getContents()) - 1;
            	
            	String[] sclasses = ideSheet.getCell(2, row).getContents().split(",");
            	int[] iclasses = new int[sclasses.length];
            	for(int i = 0; i < sclasses.length; i++)
        		{
            		iclasses[i] = Integer.parseInt(sclasses[i]) - 1;
        		}
            	
            	String[] stIde = ideSheet.getCell(3, row).getContents().split(",");
            	int[] tIde = new int[stIde.length];
            	for(int i = 0; i < stIde.length; i++)
        		{
            		tIde[i] = Integer.parseInt(stIde[i]) - 1;
        		}
            	
            	int classHours = Integer.parseInt(ideSheet.getCell(4, row).getContents());
            	
            	String[] softContraints = ideSheet.getCell(5, row).getContents().split(";");
            	
            	instance.add(new Course(ide, iclasses, tIde, classHours, softContraints));
            }
        }
        ideWorkbook.close();
        return instance;
    }
}
