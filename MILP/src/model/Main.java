package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ilog.cplex.*;
import ilog.concert.*;

public class Main {
	public static void main(String[] args) throws BiffException, IOException, RowsExceededException, WriteException
	{
		FileManager fileManager = new FileManager();
		ArrayList<Class> classes = fileManager.readClass();
		ArrayList<Teacher> teachers = fileManager.readTeacher();
		ArrayList<Course> courses = fileManager.readCourse();
		
		int N1 = 10;
		int N2 = 1;
		int N3 = 1;
		int N4 = 1;
		int N5 = 1;
		int N7 = 100;

		
		//将班级、课程、老师相关联
		for(int t = 0; t < teachers.size(); t++) {
			ArrayList<Course> tCourses = new ArrayList<Course>(); 
			for(int c = 0; c < courses.size(); c++) {
				for(int i = 0; i < courses.get(c).getTIde().length; i++) {
					if(teachers.get(t).getIde() == courses.get(c).getTIde()[i]) {
						tCourses.add(courses.get(c));
					}
				}
			}
			teachers.get(t).settCourse(tCourses);
		}
		
		for(int s = 0; s < classes.size(); s++) {
			ArrayList<Course> sCourses = new ArrayList<Course>(); 
			for(int c = 0; c < courses.size(); c++) {
				for(int i = 0; i < courses.get(c).getClasses().length; i++) {
					if(classes.get(s).getIde() == courses.get(c).getClasses()[i]) {
						sCourses.add(courses.get(c));
					}
				}
			}
			classes.get(s).settCourse(sCourses);
		}
							

		//班级数据检验
/*		int num = classes.size();//班级数
        for(int i = 1; i <= num; i++)
        {
        	System.out.println(i + "班级的课程");
        	for(int c = 0; c < classes.get(i - 1).getsCourse().size(); c++) {
        		System.out.println(classes.get(i - 1).getsCourse().get(c).getIde() + " ");
        	}
        	
        }*/

		//教师数据检验
/*
		int num = teachers.size();//班级数
        for(int i = 1; i <= num; i++)
        {
        	System.out.println(i + "老师的课程");
        	for(int c = 0; c < teachers.get(i - 1).gettCourse().size(); c++)
        	{

            	System.out.println(teachers.get(i - 1).gettCourse().get(c).getIde() + " ");

        	}
        }*/

		
		//课程数据检验
/*
		int num2 = courses.size();//班级数
        for(int i = 1; i <= num2; i++)
        {
        	System.out.println(i + "课程");
        	System.out.print("对应班级");
        	for(int m = 1; m <= courses.get(i - 1).getClasses().length; m++)
        		System.out.print(courses.get(i - 1).getClasses()[m - 1] + " ");
        	System.out.println();
        	System.out.println("教师编号" + courses.get(i - 1).getIde());
        	System.out.println("学时" + courses.get(i - 1).getClassHours());
        	System.out.print("对应软约束");
        	for(int m = 1; m <= courses.get(i - 1).getSoftConstraints().length; m++)
        		System.out.print(courses.get(i - 1).getSoftConstraints()[m - 1] + " ");
        	System.out.println();
        	
        }*/


		int weeks = 18;
		int days = 5;
		int periods = 5;
		
		double timeStart = System.currentTimeMillis();


			try {
				IloCplex model = new IloCplex();
				model.setParam(IloCplex.DoubleParam.TiLim, 3600);

				
				// define variables  x_{w,d,p,s,t,c} : 0-1 变量，当 x_{w,d,p,c} = 1 时表示课程 c，安排在第 w 周的 d 天的 p 时间段
				IloIntVar[][][][] x = new IloIntVar[weeks][days][periods][courses.size()];
				for (int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int period = 0; period < periods; period++) {
							for(int c = 0; c < courses.size(); c++) {
								x[week][day][period][c] = model.boolVar("X[" + week +"," + day + "," + period + ","  + c + "]");
								}
						}
					}
				}
				
				
				// define variables  μ_{c1,c2,w,d,i} 用于软约束3
				IloIntVar[][][][][] miu = new IloIntVar[courses.size()][courses.size()][weeks][days][2];
				for(int c1 = 0; c1 < courses.size(); c1++) {
					for(int c2 = 0; c2 < courses.size(); c2++) {
						for(int week = 0; week < weeks; week++) {
							for(int day = 0; day < days; day++) {
								for(int i = 0; i < 2; i++) {
									miu[c1][c2][week][day][i] = model.boolVar("mu[" + c1 + "," + c2 + "," + week + "," + day + "," + i + "]");
								}
							}
						}
					}
				}
				
				
				// define variables  Ω_{w,c}^1 用于软约束4
				IloIntVar[][] omega1 = new IloIntVar[weeks][courses.size()];
				for(int w = 0; w < weeks; w++) {
					for (int c = 0; c < courses.size(); c++) {
						omega1[w][c] = model.boolVar("omega1[" + w + "," + c + "]");				
					}
				}
				
				
				// define variables  Ω_{w,c}^2 用于软约束4
				IloIntVar[][] omega2 = new IloIntVar[weeks][courses.size()];
				for(int w = 0; w < weeks; w++) {
					for (int c = 0; c < courses.size(); c++) {
						omega2[w][c] = model.boolVar("omega2[" + w + "," + c + "]");				
					}
				}
				
				// define variables  Ω_{w,d,p,c} 用于软约束4
				IloIntVar[][][][] omega3 = new IloIntVar[weeks][days][periods][courses.size()];
				for(int w = 0; w < weeks; w++) {
					for(int d = 0; d < days; d++) {
						for(int p = 0; p < periods; p++) {
							for (int c = 0; c < courses.size(); c++) {
								omega3[w][d][p][c] = model.boolVar("omega3[" + w +"," + d + "," + p + "," + c + "]");				
							}
						}
					}
				}
				
				
				
				//整数变量 表示课程的开始周次
				IloIntVar[] S = new IloIntVar[courses.size()];
				for (int i = 0; i < S.length; i++) {
					S[i] =  model.intVar(1,18,"S[" + i + "]");
				}
				//整数变量，表示课程的结束周次
				IloIntVar[] E = new IloIntVar[courses.size()];
				for (int i = 0; i < E.length; i++) {
					E[i] =  model.intVar(1,18,"E[" + i + "]");
				}
				
				//define variables δ_{w,d,c} 用于软约束1 \delta_{d,c} 
				IloIntVar[][][] delta = new IloIntVar[weeks][days][courses.size()];
				for(int w = 0; w < weeks; w++) {
					for (int d = 0; d < days; d++) {
						for(int c = 0; c < courses.size(); c++) {
							delta[w][d][c] = model.intVar(0,5,"delta[" + w + "," + d + "," + c + "]");
						}			
					}
				}
				
				// define variables  λ_{c} 用于软约束8
				IloIntVar[][] lambda = new IloIntVar[weeks][courses.size()];
				for(int week = 0; week < weeks; week++) {
					for (int c = 0; c < courses.size(); c++) {
						lambda[week][c] = model.intVar(0,25,"lamda[" + week + "," + c + "]");				
					}
				}
				
				// define variables  γ_{w,c} 用于软约束8
				IloIntVar[][] gamma = new IloIntVar[weeks][courses.size()];
				for(int week = 0; week < weeks; week++) {
					for (int c = 0; c < courses.size(); c++) {
						gamma[week][c] = model.boolVar("gamma[" + week + "," + c + "]");				
					}
				}
				
				// define variables  α_{w,c} 表示课程c是否安排在w周
				IloIntVar[][] alpha = new IloIntVar[weeks][courses.size()];
				for(int week = 0; week < weeks; week++) {
					for (int c = 0; c < courses.size(); c++) {
						alpha[week][c] = model.boolVar("alpha[" + week + "," + c + "]");				
					}
				}
				
				// define variables  θ_{w,d,p,c}^1 
				IloIntVar[][][][] theta1 = new IloIntVar[weeks][days][periods][courses.size()];
				for(int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int period = 0; period < periods; period++) {
							for (int c = 0; c < courses.size(); c++) {
								theta1[week][day][period][c] = model.boolVar("theta1[" + week + "," + day + ","+ period + "," + c + "]");				
							}
						}
					}
				}
				
				// define variables  θ_{w,d,p,c}^2 
				IloIntVar[][][][] theta2 = new IloIntVar[weeks][days][periods][courses.size()];
				for(int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int period = 0; period < periods; period++) {
							for (int c = 0; c < courses.size(); c++) {
								theta2[week][day][period][c] = model.boolVar("theta2[" + week + "," + day + ","+ period + "," + c + "]");				
							}
						}
					}
				}
				
				// define variables  θ_{w,d,p,c}^3
				IloIntVar[][][][] theta3 = new IloIntVar[weeks][days][periods][courses.size()];
				for(int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int period = 0; period < periods; period++) {
							for (int c = 0; c < courses.size(); c++) {
								theta3[week][day][period][c] = model.boolVar("theta3[" + week + "," + day + ","+ period + "," + c + "]");				
							}
						}
					}
				}
				
				// define variables  θ_{w,d,p,c}^4
				IloIntVar[][][][] theta4 = new IloIntVar[weeks][days][periods][courses.size()];
				for(int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int period = 0; period < periods; period++) {
							for (int c = 0; c < courses.size(); c++) {
								theta4[week][day][period][c] = model.boolVar("theta4[" + week + "," + day + ","+ period + "," + c + "]");				
							}
						}
					}
				}

				
				//约束（1）一位教师同一时间段只能讲授一门课
				for (int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int period = 0; period < periods; period++) {
							for(int t = 0; t < teachers.size(); t++) {
								if(teachers.get(t).getPeriod()[week][day][period] == 0) {// 找到教师有空的时间段
									IloLinearIntExpr r = model.linearIntExpr();
									for(int c = 0; c < teachers.get(t).gettCourse().size(); c++) {//该老师教的课程集合
										int course = teachers.get(t).gettCourse().get(c).getIde();
										r.addTerm(1, x[week][day][period][course]);
									}
									model.addLe(r, 1);
								}
							}
						}
					}
				}
				
				//约束（2）一个班级在同一时间段只能上一门课
				for (int s = 0; s < classes.size(); s++) {
					for(int week = 0; week < weeks; week++) {
						for(int day = 0; day < days; day++) {
							for(int period = 0; period < periods; period++) {
								if(classes.get(s).getSchedule()[week][day][period] == 0) {// 找到班级有空的时间段
									IloLinearIntExpr r = model.linearIntExpr();
									for(int c = 0; c < classes.get(s).getsCourse().size(); c++) {//该班级学习的课程集合
										int course = classes.get(s).getsCourse().get(c).getIde();
										r.addTerm(1, x[week][day][period][course]);
									}
									model.addLe(r, 1);
								}
							}
						}
					}
				}
								
								
									
				
				//约束(3) 每一门课程都必须被安排，且达到各门课规定学时√
				for(int c = 0; c < courses.size(); c++){
					IloLinearIntExpr r = model.linearIntExpr();//对每个课程
					for (int week = 0; week < weeks; week++) {
						for(int day = 0; day < days; day++) {
							for(int period = 0; period < periods - 1; period++) {
								r.addTerm(2, x[week][day][period][c]);
							}
							r.addTerm(3, x[week][day][4][c]);
						}
					}
					model.addEq(r, courses.get(c).getClassHours());
				}
				
				//约束（4）在教师有空的时间段安排课程
				for (int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int period = 0; period < periods; period++) {
							for(int c = 0; c < courses.size(); c++) {
								for(int t = 0; t < courses.get(c).getTIde().length; t++) {
									if(teachers.get(courses.get(c).getTIde()[t]).getPeriod()[week][day][period] != 0) {//教师不空闲，不安排
										IloLinearIntExpr r = model.linearIntExpr();
										r.addTerm(1, x[week][day][period][c]);
										model.addEq(r, 0);
										
									}
								}
							}
						}
					}
				}
				
				//在班级有空的时间段安排课程
				for (int c = 0; c < courses.size(); c++) {
					for(int s = 0; s < courses.get(c).getClasses().length; s++) {
						for(int week = 0; week < weeks; week++) {
							for(int day = 0; day < days; day++) {
								for(int period = 0; period < periods; period++) {
									if(classes.get(courses.get(c).getClasses()[s]).getSchedule()[week][day][period] != 0) {//班级不空闲，不安排
										IloLinearIntExpr r = model.linearIntExpr();
										r.addTerm(1, x[week][day][period][c]);
										model.addEq(r, 0);
										
									}
								}
							}
						}
					}
				}
				
				//6.约束8保证了有晚课课时要求的课程一学期内晚课课时达到排课次数=1：1）
				for(int c = 0; c < courses.size(); c++) {//对每一门课
					for(int sc = 0; sc < courses.get(c).getSoftConstraints().length; sc++) {//查找对应的软约束
						if(courses.get(c).getSoftConstraints()[sc].startsWith("10")) {//如果这门课存在软约束4要求	
							IloLinearIntExpr r = model.linearIntExpr();
							for(int w = 0; w < weeks; w++) {
								for(int d = 0; d < days; d++) {
									r.addTerm(1, x[w][d][4][c]);
								}
							}
							model.addEq(r, courses.get(c).getClassHours()/5 + (courses.get(c).getClassHours()%5)/3);
						}
					}
				}
				
				
/*
 * 软约束
 * 1. 教师上课时间尽量避开上午1、2节和下午7、8节：多数教师在该时间段有特殊事务需处理 (暂未考虑)
 * 2. 教师在某些周次的某些时间有其他安排，无法排课：部分教师有会议安排，或需要出差等 **（合并至K表）**
 * 3. 同一教师负责的多门课程或多个班级的授课安排在相邻两个时间段内进行：例如一位教师同时负责讲授计算机网络技术和数据库两门课程，那么他会偏好于将两门课程都安排在同一天的上午或者下午讲授完**（C表例：编号15和编号24的课要一起上：3:24  3:15）**
 * 4. 部分课程安排在一定周次内完成全部课时：例如将课程数学建模安排在第一周至第九周完成全部课时**（C表例：前半学期排完： 4:1,9）**
 * 5. 某门课程安排在特定课程结束之后进行或同期结束：例如将运筹学(二)安排在运筹(一)结束之 后进行，将计量经济学与财务管理安排在同一周次结束 (暂未考虑)
 * 6. 部分课程在特定时间内进行连排，且至多连排四节：例如matlab物流管理实验课要求连排四节进行讲授(暂未考虑)
 * 7. 较难的课程尽量分开安排：例如微积分与C++尽量不排在同一天，分开安排缓解学生学业压力(暂未考虑)
 * 8. 不同课程在一周内具有不同的课时数量上限：例如运筹学一周至多排四节 **（C表例：一周两次课：一周两次课：8:2）**
 * 9. 一门课程不要连续多个时间段或连续多天进行：例如尽量将运筹学隔天安排，而非一周四个课时安排在相邻时间段或者相邻两天。(暂未考虑)
 * 10. 一周有若干课时要安排在晚上（连续三个课时）**（C表例：一周一次晚课：10:1）**
 * 11. 教师希望在某个时间段上课**（C表例：周三晚9-12节：11:15）**
 */
				
				
				//软约束1 相同的课程一天之内最多只能安排一次
				for(int c = 0; c < courses.size(); c++) {//对每一门课
					for(int week = 0; week < weeks; week++) {
						for(int day = 0; day < days; day++) {
							IloLinearIntExpr r = model.linearIntExpr();
							r.addTerm(1, delta[week][day][c]);
							for(int period = 0; period < periods; period++) {
								r.addTerm(-1, x[week][day][period][c]);
							}
							model.addGe(r, -1);
						}
					}
				}
									

				
				//约束3 同一教师负责的多门课程尽量安排在相邻两个时间段内进行
				for (int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int period = 0; period < periods; period++) {
							for(int t = 0; t < teachers.size(); t++) {
								for(int c1 = 0; c1 < teachers.get(t).gettCourse().size(); c1++) {
									int course1 = teachers.get(t).gettCourse().get(c1).getIde();
									for(int c2 = c1; c2 < teachers.get(t).gettCourse().size(); c2++) {
										int course2 = teachers.get(t).gettCourse().get(c2).getIde();
										IloLinearIntExpr r1 = model.linearIntExpr();
										r1.addTerm(1, x[week][day][1][course1]);
										r1.addTerm(1, x[week][day][2][course2]);
										r1.addTerm(-100000, miu[course1][course2][week][day][0]);
										
										IloLinearIntExpr r2 = model.linearIntExpr();
										r2.addTerm(1, x[week][day][3][course1]);
										r2.addTerm(1, x[week][day][4][course2]);
										r2.addTerm(-100000, miu[course1][course2][week][day][1]);
										
										model.addLe(r1, 1);
										model.addLe(r2, 1);
									}
								}
							}
						}
					}
				}
							
				
				
				
				//约束4 部分课程安排在一定周次内完成全部课时
				for(int c = 0; c < courses.size(); c++) {//对每一门课
					int min = 0;//下限
					int max = 0;//上限
					for(int sc = 0; sc < courses.get(c).getSoftConstraints().length; sc++) {//查找对应的软约束
						if(courses.get(c).getSoftConstraints()[sc].startsWith("4")) {//如果这门课存在软约束4要求
							if(!courses.get(c).getSoftConstraints()[sc].contains(",")) {
								min = 1;
								max = Integer.parseInt(courses.get(c).getSoftConstraints()[sc].substring(2));
							}
							else if(courses.get(c).getSoftConstraints()[sc].substring(3,4).equals(",")) {
								min = Integer.parseInt(courses.get(c).getSoftConstraints()[sc].substring(2, 3));
								max = Integer.parseInt(courses.get(c).getSoftConstraints()[sc].substring(4));
							}
							else {
								min = Integer.parseInt(courses.get(c).getSoftConstraints()[sc].substring(2, 4));
								max = Integer.parseInt(courses.get(c).getSoftConstraints()[sc].substring(5));
							}
							
							for(int week = 0; week < weeks; week++) {
								
								IloLinearIntExpr r1 = model.linearIntExpr();
								r1.addTerm(100000, omega1[week][c]);
								model.addGe(r1, min - week);
								
								IloLinearIntExpr r2 = model.linearIntExpr();
								r2.addTerm(100000, omega2[week][c]);
								model.addGe(r2, week - max);
								
								for(int d = 0; d < days; d++) {
									for(int p = 0; p < periods; p++) {
										IloLinearIntExpr r3 = model.linearIntExpr();
										r3.addTerm(1, omega3[week][d][p][c]);
										r3.addTerm(-1, omega1[week][c]);
										r3.addTerm(-1, omega2[week][c]);
										r3.addTerm(-1, x[week][d][p][c]);
										model.addGe(r3, -2);
										
									}
								}
							}
						}
					}
				
				}
				
				//约束8 不同课程在一周内具有不同的课时数量上限
				for(int week = 0; week < weeks; week++) {
					for(int c = 0; c < courses.size(); c++) {//对每一门课
						int max = 0;//课时上限
						for(int sc = 0; sc < courses.get(c).getSoftConstraints().length; sc++) {//查找对应的软约束
							if(courses.get(c).getSoftConstraints()[sc].startsWith("8")) {//如果这门课存在软约束4要求
								max = Integer.parseInt(courses.get(c).getSoftConstraints()[sc].substring(2));
								
									IloLinearIntExpr r = model.linearIntExpr();
									r.addTerm(1, lambda[week][c]);
									
									for(int day = 0; day < days; day++) {
										for(int period = 0; period < periods; period++) {
											r.addTerm(-1, x[week][day][period][c]);
										}
									}
									model.addGe(r, -max);			
								}
								else {
									max = 3;
									
									IloLinearIntExpr r = model.linearIntExpr();
									r.addTerm(1, lambda[week][c]);
									
									for(int day = 0; day < days; day++) {
										for(int period = 0; period < periods; period++) {
											r.addTerm(-1, x[week][day][period][c]);
										}
									}
										
									model.addGe(r, -max);
								}
							}
							
						}
				}
				
				
				
				
				//约束10 一周有若干课时要安排在晚上
				for(int c = 0; c < courses.size(); c++) {
					for(int w = 0; w < weeks; w++) {
						IloLinearIntExpr r = model.linearIntExpr();
						r.addTerm(100000, alpha[w][c]);
						for(int d = 0; d < days; d++) {
							for(int p = 0; p < periods; p++) {
								r.addTerm(-1, x[w][d][p][c]);
							}
						}
						model.addGe(r, 0);
					}
				}
				
				for(int c = 0; c < courses.size(); c++) {//对每一门课
					int nightHour = 0;
					for(int sc = 0; sc < courses.get(c).getSoftConstraints().length; sc++) {//查找对应的软约束
						if(courses.get(c).getSoftConstraints()[sc].startsWith("10")) {//如果这门课存在软约束4要求					
							nightHour = Integer.parseInt(courses.get(c).getSoftConstraints()[sc].substring(3));
							for(int week = 0; week < weeks; week++) {
								IloLinearIntExpr r1 = model.linearIntExpr();
								r1.addTerm(100000, gamma[week][c]);
								r1.addTerm(-100000, alpha[week][c]);
								IloLinearIntExpr r2 = model.linearIntExpr();
								r2.addTerm(-100000, gamma[week][c]);
								r2.addTerm(100000, alpha[week][c]);
								for(int day = 0; day < days; day++) {	
										r1.addTerm(1, x[week][day][4][c]);
										r2.addTerm(1, x[week][day][4][c]);
								}
								model.addGe(r1, nightHour - 10000);
								model.addLe(r2, nightHour + 10000);
							}
							
						}
					}
				}
				

				
				//7.每门课安排的周次尽量连续；在其安排的周次内，天和时间尽量不发生改变
				for(int c = 0; c < courses.size(); c++) {
					for(int week = 0; week < weeks; week++) {
						IloLinearIntExpr r1 = model.linearIntExpr();
						r1.addTerm(1, S[c]);
						IloLinearIntExpr r2 = model.linearIntExpr();
						r2.addTerm(1, E[c]);
						
						r1.addTerm(100000, alpha[week][c]);
						r2.addTerm(-100000, alpha[week][c]);
						
						model.addLe(r1, week + 100000);
						model.addGe(r2, week - 100000);
						
					}
				}
				
				
				for(int w = 0; w < weeks - 1; w++) {
					for(int d = 0; d < days; d++) {
						for(int p = 0; p < periods; p++) {
							for(int c = 0; c < courses.size(); c++) {
								IloLinearIntExpr r1 = model.linearIntExpr();
								r1.addTerm(1, x[w][d][p][c]);
								r1.addTerm(-1, x[w + 1][d][p][c]);
								r1.addTerm(-1, theta1[w][d][p][c]);
								model.addLe(r1, 0);
								
								IloLinearIntExpr r2 = model.linearIntExpr();
								r2.addTerm(-1, x[w][d][p][c]);
								r2.addTerm(1, x[w + 1][d][p][c]);
								r2.addTerm(-1, theta1[w][d][p][c]);
								model.addLe(r2, 0);
								
								IloLinearIntExpr r3 = model.linearIntExpr();
								r3.addTerm(100000, theta2[w][d][p][c]);
								r3.addTerm(1, S[c]);
								model.addGe(r3, w + 1);
								
								IloLinearIntExpr r4 = model.linearIntExpr();
								r4.addTerm(100000, theta3[w][d][p][c]);
								r4.addTerm(-1, E[c]);
								model.addGe(r4, -w);
								
								IloLinearIntExpr r5 = model.linearIntExpr();
								r5.addTerm(1, theta4[w][d][p][c]);
								r5.addTerm(-1, theta1[w][d][p][c]);
								r5.addTerm(-1, theta2[w][d][p][c]);
								r5.addTerm(-1, theta3[w][d][p][c]);
								model.addGe(r5, -2);
							}
						}
					}
				}
				
				
				IloLinearIntExpr obj = model.linearIntExpr();
				
				for (int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int c = 0; c < courses.size(); c++) {
							obj.addTerm(N1, delta[week][day][c]);
						}
					}
				}
				
				for (int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int c1 = 0; c1 < courses.size(); c1++) {
							for(int c2 = 0; c2 < courses.size(); c2++) {
								obj.addTerm(-N2, miu[c1][c2][week][day][0]);
								obj.addTerm(-N2, miu[c1][c2][week][day][1]);
							}
						}
					}
				}
				
				for (int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int p = 0; p < periods; p++) {
							for(int c = 0; c < courses.size(); c++) {
								obj.addTerm(N3, omega3[week][day][p][c]);
							}
						}
					}
				}
				
				
				for(int week = 0; week < weeks; week++) {
					for (int c = 0; c < courses.size(); c++) {
						obj.addTerm(N4, lambda[week][c]);			
					}
				}
				
				for(int week = 0; week < weeks; week++) {
					for (int c = 0; c < courses.size(); c++) {
						obj.addTerm(N5, gamma[week][c]);			
					}
				}
				
				for (int week = 0; week < weeks; week++) {
					for(int day = 0; day < days; day++) {
						for(int p = 0; p < periods; p++) {
							for(int c = 0; c < courses.size(); c++) {
								obj.addTerm(N7, theta4[week][day][p][c]);
							}
						}
					}
				}
															
				model.addMinimize(obj);

				model.exportModel("model.lp");
				
//				System.out.println(model.getModel());

				
				boolean solve = model.solve();

				 if(solve){
		                model.output().println("解的状态： " + model.getStatus());
		                model.output().println("目标函数值： " + model.getObjValue());
		                // 对应的未知变量的值
		                
		                File solution = new File("solution.txt");
		                FileOutputStream fos = new FileOutputStream(solution);
		                PrintWriter pw = new PrintWriter(fos);
		                
/*		                for (int week = 1; week <= weeks; week++) {
							for(int day = 1; day <= days; day++) {
								for(int period = 1; period <= periods; period++) {
									for(int c = 1; c <= courses.size(); c++) {
										double sVal = model.getValue(x[week - 1][day - 1][period - 1][c - 1]);
										if(sVal >= 0.5) {
											String str = "课程" + (courses.get(c-1).getIde() + 1) + "在第" + week + "周" + "星期" + day + "的时间段" + period + "上课\n";
											pw.write(str.toCharArray());
										}	
									}
								}
							}
						}*/
/*		                
		                for (int w = 0; w < weeks; w++) {
		                    System.out.println("\n第" + (w + 1) + "周：");
		                    int len = 0;
		                    for (CurSet Day : schedule[i]) {
		                        if (Day.getCurSet() != null) {
		                            len = Math.max(len, Day.getCurSet().size());
		                        }
		                    }
		                    if (len != 0) {
		                        int[][] Week = new int[len][25];
		                        for (int j = 0; j < 25; j++) {
		                            if (schedule[w][j].getCurSet() != null) {
		                                List<Cur> Day = new ArrayList<>(schedule[i][j].getCurSet());
		                                for (int k = 0; k < Day.size(); k++) {
		                                    Week[k][j] = Day.get(k).getSupId();
		                                }
		                            }
		                        }
		                        for (int[] ints : Week) {
		                            for (int anInt : ints) {
		                                System.out.printf("%7d", anInt);
		                            }
		                            System.out.println();
		                        }
		                    }
		                }
		                */
		                //全段的课表输出
		                System.out.println("全段的课表输出");
		                int[][][] schedule = new int[weeks][][];
		                for (int week = 1; week <= weeks; week++) {
		                	System.out.println("\n第" + week + "周：");
		                	int lens = 0;
							for(int day = 1; day <= days; day++) {
								for(int period = 1; period <= periods; period++) {
									int len = 0;
									for(int c = 1; c <= courses.size(); c++) {
										double sVal = model.getValue(x[week - 1][day - 1][period - 1][c - 1]);
										if(sVal >= 0.5) {
											len++;
										}
									}
									lens = Math.max(lens, len);
								}
							}
							if(lens != 0) {
								int[][] Week = new int[lens][25];
								for(int period = 0; period < 25; period++) {
									ArrayList<Integer> cId = new ArrayList<Integer>();
									for(int c = 1; c <= courses.size(); c++) {
										double sVal = model.getValue(x[week - 1][period / 5][period % 5][c - 1]);
										if(sVal >= 0.5) {
											cId.add(courses.get(c - 1).getIde() + 1);
										}
									}
										for(int l = 0; l < cId.size(); l++) {
											Week[l][period] = cId.get(l);
										}
										for(int n = cId.size(); n < lens; n++) {
											Week[n][period] = 0;
										}
									}
								
								for (int[] ints : Week) {
		                            for (int anInt : ints) {
		                                System.out.printf("%7d", anInt);
		                            }
		                            System.out.println();
		                        }
								
								schedule[week - 1] = new int[Week.length][25];
								
								
								for(int i = 0; i < Week.length; i++) {
			                		for(int j = 0; j < 25; j++) {
			                			schedule[week - 1][i][j] = Week[i][j];
			                		}
			                	}
							}
						}
		                
		                Write write = new Write();
		                write.writeAll(schedule);
		                
		                //班级的课表输出
		                System.out.println("班级的课表输出");
		                for(int s = 1; s <= classes.size(); s++) {
		                	System.out.println("\n" + s + "班级：");
		                	int[][] scheduleClass = new int[weeks][25];
		                	for (int week = 1; week <= weeks; week++) {
			                	System.out.println("\n第" + week + "周：");	
			                	int[] Week = new int[25];
			                	
			                	for(int period = 0; period < 25; period++) {
									
			                		for(int c = 0; c < classes.get(s - 1).getsCourse().size(); c++) {
			                			double sVal = model.getValue(x[week - 1][period / 5][period % 5][c]);
										if(sVal >= 0.5) {
											Week[period] = classes.get(s-1).getsCourse().get(c).getIde() + 1;
											break;
										}
			                		}
								}
								
								for (int ints : Week) {
									System.out.printf("%7d", ints); 
			                    }
								System.out.println();
								
								for(int p = 0; p < 25; p++) {
									scheduleClass[week - 1][p] = Week[p];
								}
							}
		                	write.writeClass(s, scheduleClass);
		                }
		                
		                

		            }else {
		                model.output().println("未找到解法");
		            }
				

			} catch (IloException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				// TODO: handle exception
			}
		
		System.out.println("<<<<<<<<<<<<< Finally we get opt solution >>>>>>>>>>>>>");
		System.out.println("Time Elapsed =  " + ((System.currentTimeMillis() - timeStart) / 1000) + " seconds");
		System.out.println("Done");

	}
}
