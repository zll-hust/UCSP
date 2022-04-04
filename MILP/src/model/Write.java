package model;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Write {
	
	public void writeAll(int[][][] schedule) throws IOException, RowsExceededException, WriteException {
        //创建Excel文件
        File file=new File("D:\\大二下\\数据魔术师\\排课\\代码实现\\全段.xls");
        //创建文件
        file.createNewFile();
        //创建工作薄
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        //创建sheet
        WritableSheet sheet=workbook.createSheet("sheet1",0);
        //添加数据
        String[] title={"周一1-2","周一3-4","周一5-6","周一7-8","周一9-12","周二1-2","周二3-4","周二5-6","周二7-8","周二9-12","周三1-2","周三3-4","周三5-6","周三7-8","周三9-12","周四1-2","周四3-4","周四5-6","周四7-8","周四9-12","周五1-2","周五3-4","周五5-6","周五7-8","周五9-12"};
        Label label=null;
        for (int i=0;i<title.length;i++){
            label=new Label(i + 1,0,title[i]);
            sheet.addCell(label);
        }
        
//        for (int i = 1;i <= schedule.length;i++){
//            label=new Label(0,i,"第" + i + "周");
//            sheet.addCell(label);
//        }
        
        
//        for(int[][] Week: schedule) {
//        	int w = 1;
//        	for (int[] ints : Week) {
//        		int period = 1;
//                for (int anInt : ints) {
//                	label=new Label(period,w,String.valueOf(anInt));
//                    sheet.addCell(label);
//                    period++;
//                }  
//            }
//        	w++;
//        }
        
//        for(int i = 0; i < schedule.length; i++) {
//        	if(i == 0) {
//        		label=new Label(0,i + 1,"第" + (i+1) + "周");
//        	}
//        	else {
//        		if(schedule[i-1] != null) {
//        			label=new Label(0,i + 1 + schedule[i-1].length,"第" + (i+1) + "周");
//        		}
//        		else {
//        			label=new Label(0,i + 1,"第" + (i+1) + "周");
//        		}
//        	}
//            sheet.addCell(label);
//            if(schedule[i] != null) {
//            	for(int j = 0; j < schedule[i].length; j++) {
//            		for(int t = 0; t < schedule[i][j].length; t++) {
//            			label=new Label(t + 1,i + 1 + j,String.valueOf(schedule[i][j][t]));
//            			sheet.addCell(label);
//            		}
//            	}
//            }
//        }
        
        int rows = 1;
        for(int i = 0; i < schedule.length; i++) {
        	label=new Label(0,rows,"第" + (i+1) + "周");
            sheet.addCell(label);
            if(schedule[i] != null) {
            	for(int j = 0; j < schedule[i].length; j++) {
            		for(int t = 0; t < schedule[i][j].length; t++) {
            			label=new Label(t + 1,rows,String.valueOf(schedule[i][j][t]));
            			sheet.addCell(label);
            		}
            		rows++;
            	}
            }
            else {
            	rows++;
            }
        }
        
        
        workbook.write();
        workbook.close();

	}
	
	public void writeClass(int classId,int[][] schedule) throws IOException, RowsExceededException, WriteException {
        //创建Excel文件
		String name = "班级" + classId + ".xls";
        File file=new File("D:\\大二下\\数据魔术师\\排课\\代码实现\\" + name);
        //创建文件
        file.createNewFile();
        //创建工作薄
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        //创建sheet
        WritableSheet sheet=workbook.createSheet("sheet1",0);
        //添加数据
        String[] title={"周一1-2","周一3-4","周一5-6","周一7-8","周一9-12","周二1-2","周二3-4","周二5-6","周二7-8","周二9-12","周三1-2","周三3-4","周三5-6","周三7-8","周三9-12","周四1-2","周四3-4","周四5-6","周四7-8","周四9-12","周五1-2","周五3-4","周五5-6","周五7-8","周五9-12"};
        Label label=null;
        for (int i=0;i<title.length;i++){
            label=new Label(i + 1,0,title[i]);
            sheet.addCell(label);
        }
        

        for(int i = 0; i < schedule.length; i++) {
        	label=new Label(0,i + 1,"第" + (i+1) + "周");
            sheet.addCell(label);
            
            for(int j = 0; j < schedule[i].length; j++) {
            	label=new Label(j + 1,i + 1,String.valueOf(schedule[i][j]));
            	sheet.addCell(label);	
            }

        }
        
        
        workbook.write();
        workbook.close();

	}

}
