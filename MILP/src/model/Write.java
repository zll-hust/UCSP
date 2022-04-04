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
        //����Excel�ļ�
        File file=new File("D:\\�����\\����ħ��ʦ\\�ſ�\\����ʵ��\\ȫ��.xls");
        //�����ļ�
        file.createNewFile();
        //����������
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        //����sheet
        WritableSheet sheet=workbook.createSheet("sheet1",0);
        //�������
        String[] title={"��һ1-2","��һ3-4","��һ5-6","��һ7-8","��һ9-12","�ܶ�1-2","�ܶ�3-4","�ܶ�5-6","�ܶ�7-8","�ܶ�9-12","����1-2","����3-4","����5-6","����7-8","����9-12","����1-2","����3-4","����5-6","����7-8","����9-12","����1-2","����3-4","����5-6","����7-8","����9-12"};
        Label label=null;
        for (int i=0;i<title.length;i++){
            label=new Label(i + 1,0,title[i]);
            sheet.addCell(label);
        }
        
//        for (int i = 1;i <= schedule.length;i++){
//            label=new Label(0,i,"��" + i + "��");
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
//        		label=new Label(0,i + 1,"��" + (i+1) + "��");
//        	}
//        	else {
//        		if(schedule[i-1] != null) {
//        			label=new Label(0,i + 1 + schedule[i-1].length,"��" + (i+1) + "��");
//        		}
//        		else {
//        			label=new Label(0,i + 1,"��" + (i+1) + "��");
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
        	label=new Label(0,rows,"��" + (i+1) + "��");
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
        //����Excel�ļ�
		String name = "�༶" + classId + ".xls";
        File file=new File("D:\\�����\\����ħ��ʦ\\�ſ�\\����ʵ��\\" + name);
        //�����ļ�
        file.createNewFile();
        //����������
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        //����sheet
        WritableSheet sheet=workbook.createSheet("sheet1",0);
        //�������
        String[] title={"��һ1-2","��һ3-4","��һ5-6","��һ7-8","��һ9-12","�ܶ�1-2","�ܶ�3-4","�ܶ�5-6","�ܶ�7-8","�ܶ�9-12","����1-2","����3-4","����5-6","����7-8","����9-12","����1-2","����3-4","����5-6","����7-8","����9-12","����1-2","����3-4","����5-6","����7-8","����9-12"};
        Label label=null;
        for (int i=0;i<title.length;i++){
            label=new Label(i + 1,0,title[i]);
            sheet.addCell(label);
        }
        

        for(int i = 0; i < schedule.length; i++) {
        	label=new Label(0,i + 1,"��" + (i+1) + "��");
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
