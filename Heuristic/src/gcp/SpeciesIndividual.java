package gcp;

import gcp.curclass.Cur;
import gcp.curclass.SmallCurClass;
import gcp.graphcoloring.Refining;
import inputentity.RawInput;
import lombok.Data;

import java.util.*;

import static gcp.graphcoloring.TwoGraphPhrase.SmallGCP;
import static gcp.graphcoloring.TwoGraphPhrase.getLeisureTime;

@Data
public class SpeciesIndividual {

    private int NODE_NUM;
    private int colorNum;
    private Adjacent[] Matrix;
    public String[] genes; // 基因序列
    int conflict; // 冲突数
    float fitness;  // 适应度
    SpeciesIndividual next;
    float rate;

    public SpeciesIndividual() {
    }

    public SpeciesIndividual(GCPData GCP) {
        this.NODE_NUM = GCP.getNODE_NUM();
        this.colorNum = GCP.getColorNum();
        this.Matrix = GCP.getMatrix();
        this.genes = new String[NODE_NUM];
        this.fitness = 0.0f;
        this.conflict = 0;
        this.next = null;
        rate = 0.0f;
    }

    //初始化物种基因（贪婪）
    void createByGreedyGenes() {
        int[] Initial_Flag = new int[genes.length];
        int[][] Initial_Matrix = new int[NODE_NUM][colorNum];
        int[] Initial_Set = new int[colorNum];
        Arrays.fill(Initial_Set, -1);


        //a. 随机选择colorNum个顶点并分配至colorNum个集合
        Random random = new Random();
        for (int s = 0; s < colorNum; s++) {
            int r;
            while (true) {
                int flag = 1;
                r = random.nextInt(NODE_NUM);
                // 避免重复情况
                int t = 0;
                while (t < s) {
                    if (r == Initial_Set[t]) {
                        flag = 0;
                        break;
                    }
                    t++;
                }
                if (flag == 1) {
                    break;
                }
            }
            Initial_Set[s] = r;
        }

        for (int j = 0; j < colorNum; j++) {
            genes[Initial_Set[j]] = Integer.toString(j);    // 先行者对应的集合序号
            Initial_Flag[Initial_Set[j]] = 1;
            Adjacent p = Matrix[Initial_Set[j]];
            while (p.next != null) {
                p = p.next;
                //有邻居顶点不是初始节点，及未分配的顶点，计算与本集合的冲突数
                if (Initial_Flag[p.neighbor] == 0) {
                    Initial_Matrix[p.neighbor][j]++;
                }
            }
        }

        //b. 一个一个将剩余顶点加入集合中
        int[] CSL = new int[50];
        int min, nb;
        int count = colorNum;
        while (count < NODE_NUM) {
            for (int k = 0; k < colorNum; k++) {
                min = Integer.MAX_VALUE;
                nb = 0;
                if (count >= NODE_NUM) {
                    break;
                }
                for (int i = 0; i < NODE_NUM; i++) {
                    if (Initial_Flag[i] == 0) {
                        if (Initial_Matrix[i][k] < min) {
                            min = Initial_Matrix[i][k];
                            CSL[0] = i;
                            nb = 1;
                        } else if (Initial_Matrix[i][k] == min && nb < 30) {
                            CSL[nb] = i;
                            nb++;
                        }
                    }
                }

                int r = random.nextInt(nb);
                genes[CSL[r]] = Integer.toString(k);
                Initial_Flag[CSL[r]] = 1;
                Adjacent p = Matrix[CSL[r]];
                while (p.next != null) {
                    p = p.next;
                    if (Initial_Flag[p.neighbor] == 0) {
                        Initial_Matrix[p.neighbor][k]++;
                    }
                }
                count++;
            }
        }
    }

    //计算物种适应度
    void calFitness() {
        int totalConflict = 0;
        Set<String> cSet = new HashSet<>();
        for (int i = 0; i < NODE_NUM; i++) {
            if (cSet.contains(genes[i])) {
                continue;
            }
            cSet.add(genes[i]);
            Adjacent p = Matrix[i];
            while (p.next != null) {
                p = p.next; //第一个p.neighbor代表的是有几个邻居节点
                if (genes[p.neighbor].equals(genes[i])) {
                    totalConflict++;
                }
            }
        }
        this.conflict = totalConflict;
        this.fitness = 1.0f / (1 + totalConflict);
    }

    //深拷贝
    public SpeciesIndividual clone() {
        SpeciesIndividual species = new SpeciesIndividual();
        species.genes = new String[this.genes.length];
        //复制值
        species.NODE_NUM = this.NODE_NUM;
        species.colorNum = this.colorNum;
        species.Matrix = this.Matrix;
        System.arraycopy(this.genes, 0, species.genes, 0, this.genes.length);
        species.conflict = this.conflict;
        species.fitness = this.fitness;

        return species;
    }

    //打印基因序列
    public boolean printRate() {
//        System.out.println("基因序列：");
//        for (String gene : genes) {
//            System.out.print(gene + " ");
//        }
//        System.out.println("冲突数：" + conflict);
        return conflict == 0;
    }

    // 打印冲突点集合
    public void printConflict() {
        for (int i = 0; i < NODE_NUM; i++) {
            Adjacent p = Matrix[i];
            while (p.next != null) {
                p = p.next;
                if (genes[p.neighbor].equals(genes[i])) {
                    System.out.println((p.neighbor + 1) + "---" + (i + 1));
                }
            }
        }

    }

    // 得到冲突集合
    public Set<Integer> getConfIndex() {
        Set<Integer> confIndex = new HashSet<>();
        for (int i = 0; i < NODE_NUM; i++) {
            Adjacent p = Matrix[i];
            while (p.next != null) {
                p = p.next;
                if (genes[p.neighbor].equals(genes[i])) {
                    if (confIndex.contains(p.neighbor) && confIndex.contains(i)) {
                        double rate = Math.random();
                        if (rate > 0.5) {
                            confIndex.remove(i);
                        } else {
                            confIndex.remove(p.neighbor);
                        }
                        continue;
                    }
                    confIndex.add(p.neighbor);
                    confIndex.add(i);
                }
            }
        }
        return confIndex;
    }

    // 将基因序列转换为集合
    public Set<Set<Integer>> geneToSet() {
        Set<Set<Integer>> mySet = new HashSet<>();
        // 计算现在是几种颜色集合
        int colorNumber = colorNum;
        for (String s : genes) {
            int num = Integer.parseInt(s) + 1;
            if (num > colorNumber) {
                colorNumber = num;
            }
        }
        for (int c = 0; c < colorNumber; c++) {
            Set<Integer> curTeam = new HashSet<>();
            for (int i = 0; i < genes.length; i++) {
                if (genes[i].equals(Integer.toString(c))) {
                    curTeam.add(i);
                }
            }
            mySet.add(curTeam);
        }
        return mySet;
    }

    // 将基因序列转换为集合
    public Set<Set<Integer>> geneToSet(int begin, int end) {
        Set<Set<Integer>> mySet = new HashSet<>();
        for (int c = begin; c <= end; c++) {
            Set<Integer> curTeam = new HashSet<>();
            for (int i = 0; i < genes.length; i++) {
                if (genes[i].equals(Integer.toString(c))) {
                    curTeam.add(i);
                }
            }
            mySet.add(curTeam);
        }
        return mySet;
    }

    public List<List<Set<Cur>>> getCurSetList(RawInput rawInput, List<SmallCurClass> SmallCurClasses) throws Exception {
        List<Cur> RemainCurs = Refining.Remain(rawInput, SmallCurClasses);
        List<List<Set<Cur>>> curSetsList = new ArrayList<>();
        List<Set<Cur>> curSets = new ArrayList<>();
        // 复制部分
        for (SmallCurClass curClass : SmallCurClasses) {
            Set<Cur> curTeam = new HashSet<>();
            for (Cur cur : curClass.getCurList()) {
                Cur smallCur = new Cur(cur.getSupId(), 1, 0, rawInput.getCurriculumList().get(cur.getSupId()).getConstraint());
                curTeam.add(smallCur);
            }
            for (int i = 0; i < curClass.getSmallCurNum(); i++) {
                curSets.add(curTeam);
            }
        }
        if (RemainCurs.size() != 0) {
            // 小图着色
            // 获得第一集合课程对应的班级集合
            Set<Integer> set = new HashSet<>();
            for (SmallCurClass sCC : SmallCurClasses) {
                for (Cur c : sCC.getCurList()) {
                    set.add(c.getSupId());
                }
            }
            // 计算最大颜色数，让小图着色在一个颜色区间内进行
            //        int colorNum_min = getColorNum() * 18;
            int colorNum_max = getLeisureTime(rawInput.getClassList(set));
            for (SmallCurClass curClass : SmallCurClasses) {
                //            colorNum_min -= curClass.getSmallCurNum();
                colorNum_max -= curClass.getSmallCurNum();
            }
            colorNum_max = Math.min(RemainCurs.size(), colorNum_max);
            List<SpeciesIndividual> smallGenes = SmallGCP(rawInput, RemainCurs, colorNum_max);
            for (SpeciesIndividual smallGene : smallGenes) {
                List<Set<Cur>> CurSets = new ArrayList<>(curSets);
                // 多余部分
                for (int k = 0; k < smallGene.getColorNum(); k++) {
                    Set<Cur> curTeam = new HashSet<>();
                    for (int i = 0; i < smallGene.getGenes().length; i++) {
                        if (smallGene.getGenes()[i].equals(Integer.toString(k))) {
                            curTeam.add(RemainCurs.get(i));
                        }
                    }
                    CurSets.add(curTeam);
                }
                curSetsList.add(CurSets);
            }
        } else {
            curSetsList.add(curSets);
        }
        return curSetsList;
    }

}
