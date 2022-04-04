package gcp;

import gcp.curclass.Cur;
import inputentity.Classes;
import inputentity.Curriculum;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static gcp.Adjacent.link_neighbor;

@Data
public class GCPData {
    private int NODE_NUM;    // 课程数
    private int colorNum;    // 颜色数
//        static final int SPECIES_NUM = 200;   // 种群数
    static final int SPECIES_NUM = 100;   // 种群数
//        static final int DEVELOP_NUM = 1000;  // 进化代数
    static final int DEVELOP_NUM = 200;  // 进化代数
    //    static final float pcl = 0.6f, pch = 0.95f;  // 交叉概率
    static final float pcl = 0.2f, pch = 0.95f;  // 交叉概率
    //    static final float pm = 0.4f; // 变异概率
    static final float pm = 0.9f; // 变异概率
    private int[][] Edge;  // 冲突边
    private Adjacent[] Matrix;   // 邻接表

    public List<Integer> Initializing(List<Curriculum> curriculums, List<Classes> classes) {
        NODE_NUM = curriculums.size();
        Edge = new int[curriculums.size()][curriculums.size()];
        Matrix = new Adjacent[curriculums.size()];
        for (int k = 0; k < curriculums.size(); k++) {
            Matrix[k] = new Adjacent();
        }

        for (int i = 0; i < NODE_NUM; i++) {
            for (int j = i + 1; j < NODE_NUM; j++) {
                // 当班级发生冲突时
                if (!Collections.disjoint(curriculums.get(i).getClassId(), curriculums.get(j).getClassId())) {
                    Edge[i][j] = Edge[j][i] = 1;
                }
                // 当教师发生冲突时
                if (!Collections.disjoint(curriculums.get(i).getTeacherId(), curriculums.get(j).getTeacherId())) {
                    Edge[i][j] = Edge[j][i] = 1;
                }
                // 当约束4-周次发生冲突时
                Map<String, String> map_i = curriculums.get(i).getConstraint();
                Map<String, String> map_j = curriculums.get(j).getConstraint();
                if (map_i.containsKey("4") && map_j.containsKey("4")) {
                    String[] MathSet_i = map_i.get("4").split(",");
                    String[] MathSet_j = map_j.get("4").split(",");
                    if (Integer.parseInt(MathSet_i[0]) <= Integer.parseInt(MathSet_j[0]) && Integer.parseInt(MathSet_i[1]) >= Integer.parseInt(MathSet_j[1])) {
                        continue;
                    } else if (Integer.parseInt(MathSet_i[0]) >= Integer.parseInt(MathSet_j[0]) && Integer.parseInt(MathSet_i[1]) <= Integer.parseInt(MathSet_j[1])) {
                        continue;
                    } else {
                        Edge[i][j] = Edge[j][i] = 1;
                    }
                }
            }
        }

        for (int x1 = 0; x1 < NODE_NUM; x1++) {
            for (int x2 = x1 + 1; x2 < NODE_NUM; x2++) {
                if (Edge[x1][x2] == 1) {
                    link_neighbor(Matrix, x1, x2);
                    link_neighbor(Matrix, x2, x1);
                }
            }
        }

        // 计算最大颜色数，取各班25个时间段中18周全为0的时间段交集
        List<Integer> colorNumber = new ArrayList<>();
        for (int j = 0; j < 25; j++) {
            boolean flag = true;
            for (Classes aClass : classes) {
                if (!aClass.getIsForbidden().get(j).isEmpty()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                colorNumber.add(j);
            }
        }
        colorNum = colorNumber.size();
        return colorNumber;
    }

    public void Initializing2(List<Curriculum> curriculums, List<Cur> Curs) {
        NODE_NUM = Curs.size();
        Edge = new int[Curs.size()][Curs.size()];
        Matrix = new Adjacent[Curs.size()];
        for (int k = 0; k < Curs.size(); k++) {
            Matrix[k] = new Adjacent();
        }

        for (int i = 0; i < NODE_NUM; i++) {
            for (int j = i + 1; j < NODE_NUM; j++) {
                int supId_i = Curs.get(i).getSupId();
                int supId_j = Curs.get(j).getSupId();

                if (supId_i == supId_j) {
                    Edge[i][j] = Edge[j][i] = 1;    // 当课时同属于一门课程
                } else if (!Collections.disjoint(curriculums.get(supId_i).getClassId(), curriculums.get(supId_j).getClassId())) {
                    Edge[i][j] = Edge[j][i] = 1;    // 当班级发生冲突时
                } else if (!Collections.disjoint(curriculums.get(supId_i).getTeacherId(), curriculums.get(supId_j).getTeacherId())) {
                    Edge[i][j] = Edge[j][i] = 1;    // 当教师发生冲突时
                } else if (Curs.get(i).getTwoNum() + Curs.get(j).getThreeNum() == 2 || Curs.get(j).getTwoNum() + Curs.get(i).getThreeNum() == 2) {
                    Edge[i][j] = Edge[j][i] = 1;    // 当约束10-2课时课与3课时课冲突
                }
            }
        }

        for (int x1 = 0; x1 < NODE_NUM; x1++) {
            for (int x2 = x1 + 1; x2 < NODE_NUM; x2++) {
                if (Edge[x1][x2] == 1) {
                    link_neighbor(Matrix, x1, x2);
                    link_neighbor(Matrix, x2, x1);
                }
            }
        }

    }
}
