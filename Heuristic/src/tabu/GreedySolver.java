package tabu;

import gcp.curclass.Cur;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GreedySolver {
    private final List<CurSet> curSetsFor;
    private final CurSet[][] schedule = new CurSet[18][25];
    private double cost;
    private int NotMove;    // 记录不能移动的id号
    private final int[] startWeek;    // 记录每门课的开课周次0~17
    /**
     * 目标函数的各系数
     * M1 教师在某些周次的某些时间有其他安排，无法排课*********已当作硬约束处理
     * M2 同一教师负责的课程尽量安排在相邻两个时间段进行，此为满足约束3的奖励值，此约束我决定不采取人工观测文件读取方式，采用教师交集
     * M3 部分课程安排在一定周次内完成全部课时，此为违背约束4的惩罚值
     * M4 不同课程在一周内具有不同的课时数量上限（如无规定，一门课一周安排两到三次），此为违背约束8的惩罚值【一周排完才知道】
     * M5 一周有若干课时要安排在晚上，此为违背约束10的惩罚值【一周排完才知道】
     * M6 一门课程不要连续多个时间段或连续多天进行，此惩罚值要比同一老师课程相邻的奖励值高一些
     * M7 相邻周次课程应尽量相同。周次内时间段发生改变就要给惩罚
     * f1,f2 教师希望在某个时间段上课，均为约束11以及约束1的惩罚值；将课程安排在某个时间段，f1为偏好该时间段时采用的较小惩罚值，f2为不偏好时采用的较大惩罚值
     */
    private final double M2, M3, M4, M5, M6, M7, f1, f2;

    //此处后期可优化为%5==2||4
    private static final List<Integer> M2List = Arrays.asList(2, 4, 7, 9, 12, 14, 17, 19, 22, 24);// M2相邻时间段
    private static final List<Integer> f2List = Arrays.asList(1, 4, 6, 9, 11, 14, 16, 19, 21, 24);// f2不偏好时间段

    public GreedySolver(double m2, double m3, double m4, double m5, double m6, double m7, double F1, double F2, List<CurSet> curSets, int size) {
        M2 = m2;
        M3 = m3;
        M4 = m4;
        M5 = m5;
        M6 = m6;
        M7 = m7;
        f1 = F1;
        f2 = F2;
        curSetsFor = curSets;
        Collections.shuffle(curSetsFor);    // 打乱列表，使得每次方案不同，扩展邻域
        startWeek = new int[size];    // 记录每门课的开课时间
        Arrays.fill(startWeek, -1);
        cost = 0;
    }

    // 贪心构造初始解
    public GreedySolver solve() {
        int n = curSetsFor.size();
        Random random = new Random();
        int idx = random.nextInt(n);
        // 将各个课程组forbidden数组合并，以便跳过都没时间的时间段
        boolean[][] conForbidden = curSetsFor.get(idx).getForbidden();
        int cont = 1;
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 25; j++) {
                for (CurSet curSet : curSetsFor) {
                    conForbidden[i][j] = conForbidden[i][j] && curSet.getForbidden()[i][j];
                }
                if (conForbidden[i][j]) {
                    // 实例化为一个虚假的CurSet并标号，**一方面便于禁忌**，一方面也便于exchange
                    schedule[i][j] = new CurSet(null, cont++, null, null);
                }
            }
        }
        NotMove = cont;   //CurSetId为[1,cont)不可动

        int px = 0, py = 0;
        Set<Integer> inserted = new HashSet<>();
        for (int origin = 0; origin < 18 * 25; origin++) {
            px = origin / 25;
            py = origin % 25;
            if (schedule[px][py] != null) {
                continue;
            } else {
                while (true) {
                    idx = random.nextInt(n);
                    if (!curSetsFor.get(idx).getForbidden()[px][py]) {
                        this.schedule[px][py] = curSetsFor.get(idx);
                        inserted.add(idx);
                        this.schedule[px][py].setCurSetId(cont++);
                        break;
                    }
                }
            }
            break;
        }

        for (Cur cur : curSetsFor.get(idx).getCurSet()) {
            String timeslotString = cur.getConstraint().get("4");
            if (timeslotString != null) {
                String[] timeslot = timeslotString.split(",");
                if (px + 1 < Integer.parseInt(timeslot[0]) || px + 1 > Integer.parseInt(timeslot[1])) {
                    this.cost += M3;
                }
            }
//            String timescale = cur.getConstraint().get("11");
//            if (timescale != null) {
//                //假如正是他所偏好的
//                if (Integer.parseInt(timescale) == py + 1) {
//                    this.cost += f1;
//                } else if (f2List.contains(py + 1)) {
//                    this.cost += f2;
//                }
//            }
            startWeek[cur.getSupId()] = px;
        }
        // 往剩下位置插入
        int k = px * 25 + py + 1;
        int count = 1;
        for (; k < 18 * 25; k++) {
            //当前要插入的点
            int tx = k / 25;
            int ty = k % 25;
            if (schedule[tx][ty] != null) {
                continue;
            }
            if (count < n) {
                //插入下一个节点
                int idx2 = 0;
                double nextF = Double.MAX_VALUE; // 下一个插入的节点所产生的惩罚值
                for (int i = 0; i < n; i++) {
                    if (!inserted.contains(i) && !curSetsFor.get(i).getForbidden()[tx][ty]) {
                        double f_next = calF(curSetsFor.get(i), k);
                        if (nextF > f_next) {
                            nextF = f_next;
                            idx2 = i;
                        }
                    }
                }
                // 要考虑如果没有找到合适的节点插入
                if (nextF != Double.MAX_VALUE) {
                    this.schedule[tx][ty] = curSetsFor.get(idx2);
                    inserted.add(idx2);
                    this.schedule[tx][ty].setCurSetId(cont++);
//                    System.out.println(nextF);
                    this.cost += nextF;
                    for (Cur cur : this.schedule[tx][ty].getCurSet()) {
                        if (startWeek[cur.getSupId()] == -1) {
                            startWeek[cur.getSupId()] = tx;
                        }
                    }
                    count++;
                } else {
                    schedule[tx][ty] = new CurSet(null, cont++, null, null);
                }
            } else {
//                System.out.println("Successfully insert all nodes");
                // 实例化为一个虚假的CurSet并标号，一方面便于禁忌，**一方面也便于exchange**
                schedule[tx][ty] = new CurSet(null, cont++, null, null);
            }
        }
        if ((count < n && k == 18 * 25) || cont != 18 * 25 + 1) {
            System.out.println("\nThe rest curSets do not fit in any Vehicle\n" +
                    "The problem cannot be resolved under these constrains");
            System.exit(0);
        }
        return this;
    }

    private double calF(CurSet now, int k) {
        if (now.getCurSet() == null) {
            return 0;
        }
        //前一个点
        int x = (k - 1) / 25;
        int y = (k - 1) % 25;
        //当前要插入的点
        int tx = k / 25;
        int ty = k % 25;
        CurSet prev = schedule[x][y];
        CurSet[] week = schedule[tx];
        CurSet[] day = new CurSet[5];
        int dayIndex = ty / 5;
        System.arraycopy(week, dayIndex * 5, day, 0, day.length);
        double nextF = 0; // 下一个插入的节点所产生的惩罚值
//        int M6count = 0;
//        // 记当前课程集各课程号
//        ArrayList<Integer> listA = new ArrayList<>();
//        for (Cur nowC : now.getCurSet()) {
//            listA.add(nowC.getSupId());
//        }

//        for (CurSet curs : week) {
//            if (curs != null) {
//                if (curs.getCurSet() == now.getCurSet()) {
//                    M45count += listA.size();
//                } else if (curs.getCurSet() != null) {
//                    ArrayList<Integer> listC = new ArrayList<>();
//                    for (Cur curSC : curs.getCurSet()) {
//                        listC.add(curSC.getSupId());
//                    }
//                    M45count += CollectionUtils.intersection(listA, listC).size();
//                }
//            }
//        }

//        for (CurSet curs : day) {
//            if (curs != null && curs.getCurSet() != null) {
//                ArrayList<Integer> listB = new ArrayList<>();
//                for (Cur curSC : curs.getCurSet()) {
//                    listB.add(curSC.getSupId());
//                }
//                M6count += CollectionUtils.intersection(listA, listB).size();
//            }
//        }


        for (Cur cur : now.getCurSet()) {
            int M45count = 0, M6count = 0, M10count = 0;
            for (CurSet curs : week) {
                if (curs != null && curs.getCurSet() != null) {
                    // 比较当前课程集与week内各课程号
                    for (Cur weekC : curs.getCurSet()) {
                        if (weekC.getSupId() == cur.getSupId()) {
                            M45count++;
                            if (weekC.getThreeNum() == 1 && cur.getThreeNum() == 1) {
                                M10count++;
                            }
                        }
                    }
                }
            }
            for (CurSet curs : day) {
                if (curs != null && curs.getCurSet() != null) {
                    ArrayList<Integer> listB = new ArrayList<>();
                    for (Cur curSC : curs.getCurSet()) {
                        listB.add(curSC.getSupId());
                    }
                    if (listB.contains(cur.getSupId())) {
                        M6count++;
                    }
                }
            }
            String timeslotString = cur.getConstraint() != null ? cur.getConstraint().get("4") : null;
            if (timeslotString != null) {
                String[] timeslot = timeslotString.split(",");
                if (Integer.parseInt(timeslot[0]) > tx + 1 || Integer.parseInt(timeslot[1]) < tx + 1) {
                    nextF += M3;
                }
            }

//            String timescale = cur.getConstraint() != null ? cur.getConstraint().get("11") : null;
//            if (timescale != null) {
//                //假如正是他所偏好的
//                if (Integer.parseInt(timescale) == ty + 1) {
//                    nextF += f1;
//                } else if (f2List.contains(ty + 1)) {
//                    nextF += f2;
//                }
//            }

            String upper = cur.getConstraint() != null ? cur.getConstraint().get("8") : null;
            int upper2 = upper == null ? 3 : Integer.parseInt(upper);
            if (M45count >= upper2) {
                nextF += M4;
            }

            String lower = cur.getConstraint() != null ? cur.getConstraint().get("10") : null;
            if (lower != null) {
                int lower2 = Integer.parseInt(lower);
                if (M10count >= lower2) {
                    nextF += M5;
                }
            }
            // 超过几次给几次惩罚 ，0次啥也不给

            if (M6count >= 1) {
                nextF += M6;
            }

            // 与上周同一时间课程集对比，从第0周到第17周
            if (startWeek[cur.getSupId()] != -1 && startWeek[cur.getSupId()] != tx && tx - 1 >= 0) {
                ArrayList<Integer> lastCurID = new ArrayList<>();
                if (schedule[tx - 1][ty].getCurSet() != null) {
                    for (Cur lastCur : schedule[tx - 1][ty].getCurSet()) {
                        lastCurID.add(lastCur.getSupId());
                    }
                    if (!lastCurID.contains(cur.getSupId())) {  // 如果上周同一时间段没有这门课，给予惩罚
                        nextF += M7;
                    }
                } else {
                    nextF += M7;
                }
            }

        }
//        for (Map<String, String> map : now.getConstraints()) {
//            String timeslotString = map.get("4");
//            if (timeslotString != null) {
//                String[] timeslot = timeslotString.split(",");
//                if (Integer.parseInt(timeslot[0]) > tx + 1 || Integer.parseInt(timeslot[1]) < tx + 1) {
//                    nextF += M3;
//                }
//            }
//
//            String timescale = map.get("11");
//            if (timescale != null) {
//                //假如正是他所偏好的
//                if (Integer.parseInt(timescale) == ty + 1) {
//                    nextF += f1;
//                } else if (f2List.contains(ty + 1)) {
//                    nextF += f2;
//                }
//            }
//
//            String upper = map.get("8");
//            int upper2 = upper == null ? 3 : Integer.parseInt(upper);
//            if (M45count >= upper2) {
//                nextF += M4;
//            }
//
//            String lower = map.get("10");
//            if (lower != null) {
//                int lower2 = Integer.parseInt(lower);
//                nextF += M5 * Math.abs(M45count + 1 - lower2);
//            }
//        }
        if (prev.getTeachers() != null && M2List.contains(ty)) {  // 相邻时间段的奖励值
            nextF -= CollectionUtils.intersection(prev.getTeachers(), now.getTeachers()).size() * M2;
        }
        return nextF;
    }

    public CurSet[][] getSchedule() {
        return schedule;
    }

    public double getCost() {
        return cost;
    }

    public int getNotMove() {
        return NotMove;
    }

    public int[] getStartWeek() {
        return startWeek;
    }
}

