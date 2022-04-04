package gcp.graphcoloring;

import gcp.curclass.Cur;
import gcp.curclass.SmallCurClass;
import gcp.SpeciesIndividual;
import inputentity.Curriculum;
import inputentity.RawInput;

import java.util.*;

public class Refining {
    // 复制
    public static List<SmallCurClass> Duplication(RawInput rawInput, SpeciesIndividual s, int begin, int end) throws Exception {

        List<SmallCurClass> SmallCurClasses = new ArrayList<>();
        for (int c = begin; c < end; c++) {
            Set<Integer> curTeam = new HashSet<>();
            for (int i = 0; i < s.getGenes().length; i++) {
                if (s.getGenes()[i].equals(Integer.toString(c))) {
                    curTeam.add(i);
                }
            }
            if(curTeam.isEmpty()){
                continue;
            }
            List<Cur> CurList = new ArrayList<>();
            for (Integer integer : curTeam) {
                Curriculum cur = rawInput.getCurriculumList().get(integer);
                // 当约束10-2、3课时读取发生冲突时
                Map<String, String> map = cur.getConstraint();
                if (map.containsKey("10")) {
                    int hourNum = Integer.parseInt(cur.getCurHour()) / 5;
                    int moreHour = Integer.parseInt(cur.getCurHour()) % 5;
                    if (moreHour == 2) {
                        Cur aCur = new Cur(integer, hourNum + 1, hourNum,null);
                        CurList.add(aCur);
                    } else if (moreHour == 3) {
                        Cur aCur = new Cur(integer, hourNum, hourNum + 1,null);
                        CurList.add(aCur);
                    } else if (moreHour == 0) {
                        Cur aCur = new Cur(integer, hourNum, hourNum,null);
                        CurList.add(aCur);
                    } else {
                        throw new Exception("3课时的课程出错！");
                    }
                } else {
                    int hourNum = Integer.parseInt(cur.getCurHour()) / 2;
                    Cur aCur = new Cur(integer, hourNum, 0,null);
                    CurList.add(aCur);
                }
            }
            int smallCurNum = Integer.MAX_VALUE;
            for (Cur aCur : CurList) {
                smallCurNum = Math.min(aCur.getTwoNum(), smallCurNum);
            }
            SmallCurClass sCC = new SmallCurClass(c, smallCurNum, CurList);
            SmallCurClasses.add(sCC);
        }

        return SmallCurClasses;
    }

    public static List<Cur> Remain(RawInput rawInput,List<SmallCurClass> SmallCurClasses) {
        // 多余课时
        List<Cur> RemainCurs = new ArrayList<>();
        for (SmallCurClass sCC : SmallCurClasses) {
            List<Cur> CurList = sCC.getCurList();
            for (Cur theCur : CurList) {
                int twoCurNum = theCur.getTwoNum() - sCC.getSmallCurNum();
                Cur remainCur2 = new Cur(theCur.getSupId(), 1, 0,rawInput.getCurriculumList().get(theCur.getSupId()).getConstraint());
                Cur remainCur3 = new Cur(theCur.getSupId(), 0, 1,rawInput.getCurriculumList().get(theCur.getSupId()).getConstraint());
                for (int i = 0; i < twoCurNum; i++) {
                    Cur remainCur = new Cur(remainCur2);
                    RemainCurs.add(remainCur);
                }
                for (int i = 0; i < theCur.getThreeNum(); i++) {
                    Cur remainCur = new Cur(remainCur3);
                    RemainCurs.add(remainCur);
                }
            }
        }
        return RemainCurs;
    }
}
