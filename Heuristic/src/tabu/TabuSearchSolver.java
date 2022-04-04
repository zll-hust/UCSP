package tabu;

import gcp.curclass.Cur;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class TabuSearchSolver {
    private final List<CurSet> curSetsFor;
    private final int TABU_Horizon;
    private final int iterations;

    private CurSet[][] BestSchedule = new CurSet[18][25];

    private CurSet[][] schedule;
    private double cost;
    private final int NotMove;
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

    public TabuSearchSolver(int tabuH, int iter, double m2, double m3, double m4, double m5, double m6, double m7, double F1, double F2, List<CurSet> curSets, int size) {
        this.TABU_Horizon = tabuH;
        this.iterations = iter;
        this.M2 = m2;
        this.M3 = m3;
        this.M4 = m4;
        this.M5 = m5;
        this.M6 = m6;
        this.M7 = m7;
        this.f1 = F1;
        this.f2 = F2;
        this.curSetsFor = curSets;

        GreedySolver greedySolver = new GreedySolver(m2, m3, m4, m5, m6, m7, F1, F2, curSets, size);
        greedySolver.solve();
        this.schedule = greedySolver.getSchedule();
        this.cost = greedySolver.getCost();
        System.out.println(this.cost);
        this.NotMove = greedySolver.getNotMove();
        this.startWeek = greedySolver.getStartWeek();
    }

    public TabuSearchSolver solve() {
        // We use 1-0 exchange move
        double BestNCost, NeighborCost;
        int SwapIndexA = -1, SwapIndexB = -1;
        int iteration_number = 0;
        int forceIter = 0;

        int[][] TABU_Matrix = new int[18 * 25 + 2][18 * 25 + 2];

        double bestSolutionCost = this.cost;

        while (iteration_number < iterations && forceIter < 300) {
            BestNCost = Double.MAX_VALUE;
//            for (WeekFrom = 0; WeekFrom < schedule.length; WeekFrom++) {
//                routesFrom = this.schedule[WeekFrom];
//                int RoutesFromLength = routesFrom.length;
//
//                for (int i = 0; i < RoutesFromLength; i++) {
//                    for (WeekTo = 0; WeekTo < schedule.length; WeekTo++) {
//                        routesTo = this.schedule[WeekTo];
//                        int RoutesToLength = routesTo.length;
//                        for (int j = 0; j < RoutesToLength; j++) {
//                            if (WeekFrom != WeekTo || i != j) {
//                                if (((i + 1) % 5 == 0 && (j + 1) % 5 == 0) || ((i + 1) % 5 != 0 && (j + 1) % 5 != 0)) {
//                                    // 要么都是三课时，要么都不是
//
//                                    // minus and added cost after exchange
//                                    double MinusCost1 = calF(routesFrom[i], WeekFrom * 25 + i);
//                                    double AddedCost1 = calF(routesFrom[i], WeekTo * 25 + j);
//                                    double MinusCost2 = calF(routesTo[j], WeekTo * 25 + j);
//                                    double AddedCost2 = calF(routesTo[j], WeekFrom * 25 + i);
//
//                                    // Check if the move is a Tabu!
//                                    if ((TABU_Matrix[routesFrom[i - 1].getCurSetId()][routesFrom[i].getCurSetId()] != 0)
//                                            || (TABU_Matrix[routesTo[j]]))
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }

            // 0和18*25+1代表两个虚拟课程点
            for (int From = 0; From < 18 * 25; From++) {
                CurSet PrevFrom, NowFrom, EndFrom;
                if (From == 0) {
                    PrevFrom = new CurSet(null, 0, null, null);
                } else {
                    PrevFrom = schedule[(From - 1) / 25][(From - 1) % 25];
                }
                NowFrom = schedule[From / 25][From % 25];
                if (From + 1 == 18 * 25) {
                    EndFrom = new CurSet(null, 18 * 25 + 1, null, null);
                } else {
                    EndFrom = schedule[(From + 1) / 25][(From + 1) % 25];
                }
                for (int To = 0; To < 18 * 25; To++) {
                    if (From != To && (((From + 1) % 5 == 0 && (To + 1) % 5 == 0) || ((From + 1) % 5 != 0 && (To + 1) % 5 != 0))) {
                        // 要么都是三课时，要么都不是
                        CurSet PrevTo, NowTo, EndTo;
                        if (To == 0) {
                            PrevTo = new CurSet(null, 0, null, null);
                        } else {
                            PrevTo = schedule[(To - 1) / 25][(To - 1) % 25];
                        }
                        NowTo = schedule[To / 25][To % 25];
                        if (To + 1 == 18 * 25) {
                            EndTo = new CurSet(null, 18 * 25 + 1, null, null);
                        } else {
                            EndTo = schedule[(To + 1) / 25][(To + 1) % 25];
                        }

                        if ((NowFrom.getCurSetId() >= 0 && NowFrom.getCurSetId() < NotMove)
                                || (NowTo.getCurSetId() >= 0 && NowTo.getCurSetId() < NotMove)) {
                            continue;
                        }

                        if ((NowFrom.getForbidden() != null && NowFrom.getForbidden()[To / 25][To % 25])
                                || (NowTo.getForbidden() != null && NowTo.getForbidden()[From / 25][From % 25])) {
                            continue;
                        }
//                        // minus and added cost after remove fromNode
//                        double MinusCost1 = calF(NowFrom, PrevFrom, From, 0) + calF(EndFrom, NowFrom, From + 1, 0);
//                        double AddedCost1 = calF(NowFrom, PrevTo, To, 1) + calF(EndTo, NowFrom, To + 1, 0);
//                        // minus and added cost after remove toNode
//                        double MinusCost2 = calF(NowTo, PrevTo, To, 0) + calF(EndTo, NowTo, To + 1, 0);
//                        double AddedCost2 = calF(NowTo, PrevFrom, From, 1) + calF(EndFrom, NowTo, From + 1, 0);
                        // 交换再复原避免出错 flag=0代表原有，1代表将要插入
                        double MinusCost1 = calF(NowFrom, PrevFrom, From, 0) + calF(EndFrom, NowFrom, From + 1, 0);
                        double MinusCost2 = calF(NowTo, PrevTo, To, 0) + calF(EndTo, NowTo, To + 1, 0);
                        schedule[From / 25][From % 25] = NowTo;
                        schedule[To / 25][To % 25] = NowFrom;
                        double AddedCost1 = calF(NowFrom, PrevTo, To, 1) + calF(EndTo, NowFrom, To + 1, 0);
                        double AddedCost2 = calF(NowTo, PrevFrom, From, 1) + calF(EndFrom, NowTo, From + 1, 0);
                        schedule[From / 25][From % 25] = NowFrom;
                        schedule[To / 25][To % 25] = NowTo;

                        // Check if the move is a Tabu!
                        if ((TABU_Matrix[PrevFrom.getCurSetId()][NowTo.getCurSetId()] != 0)
                                || (TABU_Matrix[NowTo.getCurSetId()][EndFrom.getCurSetId()] != 0)
                                || (TABU_Matrix[PrevTo.getCurSetId()][NowFrom.getCurSetId()] != 0)
                                || (TABU_Matrix[NowFrom.getCurSetId()][EndTo.getCurSetId()] != 0)) {
                            break;
                        }

                        NeighborCost = AddedCost1 + AddedCost2 - MinusCost1 - MinusCost2;

                        // ensure the solution is valid
                        if (NeighborCost < BestNCost) {
                            BestNCost = NeighborCost;
                            SwapIndexA = From;
                            SwapIndexB = To;
                        }
                    }
                }
            }

            for (int o = 0; o < TABU_Matrix[0].length; o++) {
                for (int p = 0; p < TABU_Matrix[0].length; p++) {
                    if (TABU_Matrix[o][p] > 0) {
                        TABU_Matrix[o][p]--;
                    }
                }
            }

            CurSet PrevFrom, NowFrom, EndFrom;
            CurSet PrevTo, NowTo, EndTo;
            if (SwapIndexA == 0) {
                PrevFrom = new CurSet(null, 0, null, null);
            } else {
                PrevFrom = schedule[(SwapIndexA - 1) / 25][(SwapIndexA - 1) % 25];
            }
            NowFrom = schedule[SwapIndexA / 25][SwapIndexA % 25];
            if (SwapIndexA + 1 == 18 * 25) {
                EndFrom = new CurSet(null, 18 * 25 + 1, null, null);
            } else {
                EndFrom = schedule[(SwapIndexA + 1) / 25][(SwapIndexA + 1) % 25];
            }
            if (SwapIndexB == 0) {
                PrevTo = new CurSet(null, 0, null, null);
            } else {
                PrevTo = schedule[(SwapIndexB - 1) / 25][(SwapIndexB - 1) % 25];
            }
            NowTo = schedule[SwapIndexB / 25][SwapIndexB % 25];
            if (SwapIndexB + 1 == 18 * 25) {
                EndTo = new CurSet(null, 18 * 25 + 1, null, null);
            } else {
                EndTo = schedule[(SwapIndexB + 1) / 25][(SwapIndexB + 1) % 25];
            }

            Random TabuRan = new Random();
            int randomDelay1 = TabuRan.nextInt(5);
            int randomDelay2 = TabuRan.nextInt(5);
            int randomDelay3 = TabuRan.nextInt(5);
            int randomDelay4 = TabuRan.nextInt(5);

            TABU_Matrix[PrevFrom.getCurSetId()][NowFrom.getCurSetId()] = this.TABU_Horizon + randomDelay1;
            TABU_Matrix[NowFrom.getCurSetId()][EndFrom.getCurSetId()] = this.TABU_Horizon + randomDelay2;
            TABU_Matrix[PrevTo.getCurSetId()][NowTo.getCurSetId()] = this.TABU_Horizon + randomDelay3;
            TABU_Matrix[NowTo.getCurSetId()][EndTo.getCurSetId()] = this.TABU_Horizon + randomDelay4;

            schedule[SwapIndexA / 25][SwapIndexA % 25] = NowTo;
            schedule[SwapIndexB / 25][SwapIndexB % 25] = NowFrom;

            int AX = SwapIndexA / 25;
            int BX = SwapIndexB / 25;
            if (NowTo.getCurSet() != null) {
                for (Cur toCur : NowTo.getCurSet()) {
                    if (startWeek[toCur.getSupId()] < AX) {
                        startWeek[toCur.getSupId()] = AX;
                    }
                }
            }
            if (NowFrom.getCurSet() != null) {
                for (Cur fromCur : NowFrom.getCurSet()) {
                    if (startWeek[fromCur.getSupId()] < BX) {
                        startWeek[fromCur.getSupId()] = BX;
                    }
                }
            }

            System.out.println(BestNCost);
            this.cost += BestNCost;

            if (this.cost < bestSolutionCost) {
                iteration_number = 0;
                this.BestSchedule = this.schedule;
                bestSolutionCost = this.cost;
            } else {
                iteration_number++;
            }
            forceIter++;
        }
        this.schedule = this.BestSchedule;
        this.cost = bestSolutionCost;

        return this;
    }

    private double calF(CurSet now, CurSet prev, int k, int flag) {
        if (k == 18 * 25 || now.getCurSet() == null) {
            return 0;
        }

        //当前要插入的点
        int tx = k / 25;
        int ty = k % 25;
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
                if (curs.getCurSet() != null) {
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

            // 这里得标记一个状态flag，在tabu中你原来占有的0计算M4时，应该是>；而对于新交换1进来的，应该是>=
            String upper = cur.getConstraint() != null ? cur.getConstraint().get("8") : null;
            int upper2 = upper == null ? 3 : Integer.parseInt(upper);
            if (flag == 1) {
                if (M45count >= upper2) {
                    nextF += M4;
                }
            } else {
                if (M45count > upper2) {
                    nextF += M4;
                }
            }


            String lower = cur.getConstraint() != null ? cur.getConstraint().get("10") : null;
            if (lower != null) {
                int lower2 = Integer.parseInt(lower);
                if (flag == 1) {
                    if (M10count >= lower2) {
                        nextF += M5;
                    }
                } else if (M10count >= lower2 + 1) {
                    nextF += M5;
                }
            }
            if (flag == 1) {
                if (M6count >= 1) {
                    nextF += M6;
                }
            } else {
                if (M6count > 1) {
                    nextF += M6;
                }
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
        if (prev.getCurSet() != null && M2List.contains(ty)) {  // 相邻时间段的奖励值
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
}
