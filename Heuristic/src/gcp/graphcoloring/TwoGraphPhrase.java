package gcp.graphcoloring;

import gcp.GCPData;
import gcp.GeneticAlgorithm;
import gcp.SpeciesIndividual;
import gcp.SpeciesPopulation;
import gcp.curclass.Cur;
import gcp.curclass.SmallCurClass;
import inputentity.Classes;
import inputentity.Curriculum;
import inputentity.RawInput;

import java.util.*;

public class TwoGraphPhrase {

    public static List<List<Set<Cur>>> twoGraphColoring(RawInput rawInput) throws Exception {

        // 大图着色
        List<SpeciesIndividual> bigGenes = BigGCP(rawInput);
        System.out.println("bigGenes.size() = " + bigGenes.size());

//        for (SpeciesIndividual s : bigGenes) {
//            // 计算现在是几种颜色集合
//            int colorNumber = s.getColorNum();
//            for (String gene : s.getGenes()) {
//                int num = Integer.parseInt(gene) + 1;
//                if (num > colorNumber) {
//                    colorNumber = num;
//                }
//            }
//            System.out.println(s.geneToSet(0, colorNumber - 1));
//            System.out.println(s.getColorNum());
//        }

        // attention!此时bigGenes中的每种方案的colorNumber都是未修改冲突前的，不是12或13，而是9，以此来进入小图着色
        List<List<Set<Cur>>> result = new ArrayList<>();  // 我真不知道咋取名了【大哭】
        // 对于每种方案
        for (SpeciesIndividual s : bigGenes) {
            // 计算现在是几种颜色集合
            int colorNumber = s.getColorNum();
            for (String gene : s.getGenes()) {
                int num = Integer.parseInt(gene) + 1;
                if (num > colorNumber) {
                    colorNumber = num;
                }
            }

            // SmallCurClasses1记录了第一集合【即大一大二课程混合9种颜色】
            List<SmallCurClass> SmallCurClasses1 = Refining.Duplication(rawInput, s, 0, s.getColorNum());
            List<List<Set<Cur>>> CurSetList1 = s.getCurSetList(rawInput, SmallCurClasses1);

            // 经过后面debug at GCP.GraphColoring.TwoGraphPhrase.SmallGCP(TwoGraphPhrase.java:205)
            // 如果为空，说明颜色不够用，那就直接换下一个BigGene
            if (CurSetList1.size() == 0) {
                continue;
            }

            // SmallCurClasses2记录了第二集合【即矛盾的那部分大二课程2种颜色】
            List<SmallCurClass> SmallCurClasses2 = Refining.Duplication(rawInput, s, s.getColorNum(), colorNumber);
            List<List<Set<Cur>>> CurSetList2 = s.getCurSetList(rawInput, SmallCurClasses2);
            if (CurSetList1.size() == 0) {
                continue;
            }

            for (List<Set<Cur>> CurSet1 : CurSetList1) {
                for (List<Set<Cur>> CurSet2 : CurSetList2) {
                    List<Set<Cur>> totalCurSet = merge(CurSet1, CurSet2);
                    result.add(totalCurSet);
                }
            }

            System.out.println("result.size()大小累计为" + result.size());
        }
        return result;
    }


    // 大图着色
    private static List<SpeciesIndividual> BigGCP(RawInput rawInput) {
        // 决定遗传算法各参数
        GCPData totalGCP = new GCPData();
        List<Integer> timeList = totalGCP.Initializing(rawInput.getCurriculumList(), rawInput.getClassList());   // 将原问题转化为图着色问题，同时得到班级空闲时间段
        if (rawInput.getCurriculumList().size() <= totalGCP.getColorNum()) {
            int restrictNum = rawInput.getCurriculumList().size() / 2;
            totalGCP.setColorNum(restrictNum);
            List<Integer> restrictTimeList = new ArrayList<>();
            for (int i = 0; i < restrictNum; i++) {
                restrictTimeList.add(timeList.get(i));
            }
            timeList = restrictTimeList;
        }
        // 创建遗传算法驱动对象
        GeneticAlgorithm GA = new GeneticAlgorithm();

        // 创建初始种群
        SpeciesPopulation speciesPopulation = new SpeciesPopulation();

        // 开始遗传算法（选择、交叉、变异算子）
        List<SpeciesIndividual> bestRate = GA.run(speciesPopulation, totalGCP);

        /*// 打印
        System.out.println("++++++++++++++++++++++++++++++++++++++");
        for (SpeciesIndividual s : bestRate) {
            s.printRate();
            s.printConflict();
            System.out.println(s.getConfIndex());
        }*/

        // 多余大二课程[以实际情况来看多是大二]
        List<Set<Integer>> ConfIndexList = new ArrayList<>();
        for (SpeciesIndividual s : bestRate) {
            // 随机选择冲突集合中的一个点
            Set<Integer> ConfIndex = s.getConfIndex();
            ConfIndexList.add(ConfIndex);
        }
        Set<Integer> hs1 = new HashSet<>(timeList);
        for (int k = 0; k < ConfIndexList.size(); k++) {
            List<Curriculum> theCur = rawInput.getCurriculumList(ConfIndexList.get(k));
            List<Classes> theClass = rawInput.getClassList(ConfIndexList.get(k));
            // 决定遗传算法各参数
            GCPData bigGCP = new GCPData();
            List<Integer> timeList2 = bigGCP.Initializing(theCur, theClass);

//            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            // 从大二空闲时间段减去和大一共用时间
            Set<Integer> hs2 = new HashSet<>(timeList2);
            hs2.removeAll(hs1);
            List<Integer> timeList3 = new ArrayList<>(hs2);
            int colorNums = Math.min(timeList3.size(), ConfIndexList.get(k).size());

            for (int i = 2; i <= colorNums; i++) {
                bigGCP.setColorNum(i);
                // 创建初始种群
                SpeciesPopulation speciesPopulation2 = new SpeciesPopulation();

                // 开始遗传算法（选择、交叉、变异算子）
                List<SpeciesIndividual> bestRate2 = GA.run(speciesPopulation2, bigGCP);
//                System.out.println("#######################################");
                boolean book = false;
                for (SpeciesIndividual s2 : bestRate2) {
                    boolean flag = s2.printRate();
                    if (flag) {
                        book = true;
                        for (int cur = 0; cur < theCur.size(); cur++) {
                            int new_genes = Integer.parseInt(s2.genes[cur]) + timeList.size();
                            bestRate.get(k).genes[Integer.parseInt(theCur.get(cur).getCurId()) - 1] = Integer.toString(new_genes);
                        }
                        break;
                    }
                }
                if (book) {
                    break;
                }
            }

        }

//        // 挑选几个
//        if (bestRate.size() <= 4) {
//            return bestRate;
//        } else {
//            int[] randomNum = randomCommon(0, bestRate.size(), 2);
//            List<SpeciesIndividual> BigGenes = new ArrayList<>();
//            assert randomNum != null;
//            for (int num : randomNum) {
//                BigGenes.add(bestRate.get(num));
//            }
//            return BigGenes;
//        }

        return bestRate;
    }


    // 小图着色
    public static List<SpeciesIndividual> SmallGCP(RawInput rawInput, List<Cur> RemainCurs, int colorNum_max) throws Exception {
        // 决定遗传算法各参数
        GCPData smallGCP = new GCPData();
        smallGCP.Initializing2(rawInput.getCurriculumList(), RemainCurs);   // 将原问题转化为图着色问题
//        smallGCP.setColorNum(colorNum);

        // 创建遗传算法驱动对象
        GeneticAlgorithm GA = new GeneticAlgorithm();

//        for (int i = colorNum_min; i <= colorNum_max; i++) {
//            smallGCP.setColorNum(i);
//            // 创建初始种群
//            SpeciesPopulation speciesPopulation2 = new SpeciesPopulation();
//
//            // 开始遗传算法（选择、交叉、变异算子）
//            List<SpeciesIndividual> bestRate2 = GA.run(speciesPopulation2, smallGCP);
//
//            // 选出最好的几个返回
//            Random random=new Random();
//            int r=random.nextInt(bestRate2.size());
//            if(bestRate2.get(r).printRate()){
//                if(bestRate2.size()<=4){
//                    return bestRate2;
//                }else{
//                    int[] randomNum=randomCommon(0, bestRate2.size(), 4);
//                    List<SpeciesIndividual> smallGenes=new ArrayList<>();
//                    assert randomNum != null;
//                    for(int num:randomNum){
//                        smallGenes.add(bestRate2.get(num));
//                    }
//                    return smallGenes;
//                }
//            }
//
//        }
        List<SpeciesIndividual> bestRate = new ArrayList<>();
        for (int i = colorNum_max; i > 0; i--) {
            smallGCP.setColorNum(i);
            // 创建初始种群
            SpeciesPopulation speciesPopulation2 = new SpeciesPopulation();

            // 开始遗传算法（选择、交叉、变异算子）
            List<SpeciesIndividual> bestRate2 = GA.run(speciesPopulation2, smallGCP);

            // 选出最好的几个返回
            Random random = new Random();
            int r = random.nextInt(bestRate2.size());
            if (bestRate2.get(r).printRate()) {
                bestRate = bestRate2;
            } else if (bestRate.size() <= 2) {
                if (bestRate.size() == 0) {
//                    throw new Exception("SmallGCP failed,the colorNum is not enough!");
                    System.out.println("SmallGCP failed,the colorNum is not enough!Skip this bigGene's phrase");
                }
                return bestRate;
            } else {
                int[] randomNum = randomCommon(0, bestRate.size(), 2);
                List<SpeciesIndividual> smallGenes = new ArrayList<>();
                assert randomNum != null;
                for (int num : randomNum) {
                    smallGenes.add(bestRate.get(num));
                }
                return smallGenes;
            }


        }

//        // 创建初始种群
//        SpeciesPopulation speciesPopulation2 = new SpeciesPopulation();
//
//        // 开始遗传算法（选择、交叉、变异算子）
//        List<SpeciesIndividual> bestRate2 = GA.run(speciesPopulation2, smallGCP);
//
//        for (SpeciesIndividual s2 : bestRate2) {
//            boolean flag = s2.printRate();
//            if (flag) {
//                return bestRate2;
//            }
//
//        }
        throw new Exception("SmallGCP failed,the colorNum is not enough!");
    }

    public static int getLeisureTime(List<Classes> classesList) {
        int leisureTime = 25 * 18;
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 25; i++) {
            set.clear();
            for (Classes aClass : classesList) {
                set.addAll(aClass.getIsForbidden().get(i));
            }
            leisureTime -= set.size();
        }
        return leisureTime;
    }

    private static int[] randomCommon(int min, int max, int n) {
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    // Generic method to join two lists in Java
    private static <T> List<T> merge(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<>(list1);
        list.addAll(list2);

        return list;
    }
}
