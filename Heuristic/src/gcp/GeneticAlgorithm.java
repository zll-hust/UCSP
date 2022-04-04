package gcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 遗传算法类
 * 包含：
 * 1.run 开始跑算法
 * 2.createBeginningSpecies 创建种群
 * 3.calRate 计算每一种物种被选中的概率
 * 4.select 轮盘策略，选择适应度高的物种
 * 5.crossover 染色体交叉
 * 6.mutate 染色体变异
 * 7.getBest 获得适应度最大的物种
 */
public class GeneticAlgorithm {
    /*
        // 开始遗传
        public SpeciesIndividual run(SpeciesPopulation list) {
            // 创建初始种群
            createBeginningSpecies(list);
            for (int i = 1; i <= GCPData.DEVELOP_NUM; i++) {
                // 选择
                select(list);

                // 交叉
                crossover(list);

                // 变异
                mutate(list);
            }

            return getBest(list);
        }
    */
    // 开始遗传
    public List<SpeciesIndividual> run(SpeciesPopulation list, GCPData GCP) {
        // 创建初始种群
        createBeginningSpecies(list, GCP);
        for (int i = 1; i <= GCPData.DEVELOP_NUM; i++) {
            // 选择
            select(list);

            // 交叉
            crossover(list);

            // 变异
            mutate(list);
        }
        SpeciesIndividual point = list.head.next;
        while (point != null) {
            point.calFitness();
            point = point.next;
        }
        return getBest(list);
    }

    // 创建初始种群
    void createBeginningSpecies(SpeciesPopulation list, GCPData GCP) {
        int greedyNum = GCPData.SPECIES_NUM;
        for (int i = 1; i <= greedyNum; i++) {
            SpeciesIndividual species = new SpeciesIndividual(GCP);  // 创建结点
            species.createByGreedyGenes();  // 初始种群基因

            list.add(species);  // 添加物种
        }
    }

    // 计算每一物种被选中的概率
    void calRate(SpeciesPopulation list) {
        // 计算总适应度
        float totalFitness = 0.0f;
        list.speciesNum = 0;
        SpeciesIndividual point = list.head.next; // 游标
        while (point != null) {
            point.calFitness();
//            System.out.println(point.fitness);
            totalFitness += point.fitness;
            list.speciesNum++;
            point = point.next;
        }

        // 计算选中概率
        point = list.head.next;   // 游标
        while (point != null) {
            point.rate = point.fitness / totalFitness;
            point = point.next;
        }
    }

    // 选择优秀物种（轮盘赌）
    void select(SpeciesPopulation list) {
        // 计算适应度
        calRate(list);

        // 找出最大适应度物种
        int talentConf = Integer.MAX_VALUE;
        SpeciesIndividual talentSpecies = null;
        SpeciesIndividual point = list.head.next; //游标

        while (point != null) {
            if (talentConf > point.conflict) {
                talentConf = point.conflict;
                talentSpecies = point;
            }
            point = point.next;
        }

        // 将最大适应度物种复制talentNum个
        SpeciesPopulation newSpeciesPopulation = new SpeciesPopulation();
        int talentNum = list.speciesNum / 4;
        for (int i = 1; i <= talentNum; i++) {
            // 复制物种至新表
            assert talentSpecies != null;
            SpeciesIndividual newSpecies = talentSpecies.clone();
            newSpeciesPopulation.add(newSpecies);
        }

        // 轮盘赌list,speciesNum-talentNum次
        int roundNum = list.speciesNum - talentNum;
        for (int i = 1; i <= roundNum; i++) {
            // 产生0-1的概率
            float rate = (float) Math.random();
            SpeciesIndividual oldPoint = list.head.next;    // 游标
            while (oldPoint != null && oldPoint != talentSpecies)   // 寻找表尾结点
            {
                if (rate <= oldPoint.rate) {
                    SpeciesIndividual newSpecies = oldPoint.clone();
                    newSpeciesPopulation.add(newSpecies);

                    break;
                } else {
                    rate = rate - oldPoint.rate;
                }
                oldPoint = oldPoint.next;
            }
            if (oldPoint == null || oldPoint == talentSpecies) {
                // 复制最后一个
                point = list.head;  // 游标
                while (point.next != null)  // 寻找表尾结点
                    point = point.next;
                SpeciesIndividual newSpecies = point.clone();
                newSpeciesPopulation.add(newSpecies);
            }

        }
        list.head = newSpeciesPopulation.head;

    }

//    // 交叉操作
//    void crossover(SpeciesPopulation list) {
//        // 以概率pcl~pch进行
//        float rate = (float) Math.random();
//        if (rate > GCPData.pcl && rate < GCPData.pch) {
//            SpeciesIndividual point = list.head.next; // 游标
//            int nodeNum = point.genes.length;
//            Random rand = new Random();
//            int find = rand.nextInt(list.speciesNum);
//            while (point != null && find != 0) {
//                point = point.next;
//                find--;
//            }
//
//            assert point != null;
//            if (point.next != null) {
//                int begin = rand.nextInt(nodeNum);
//
//                // 取point和point.next进行交叉，形成新的两个染色体
//                for (int i = begin; i < nodeNum; i++) {
//                    int fir, sec;
//                    for (fir = 0; !point.genes[fir].equals(point.next.genes[i]); fir++) ;
//                    for (sec = 0; !point.next.genes[sec].equals(point.genes[i]); sec++) ;
//                    // 两个基因互换
//                    String temp;
//                    temp = point.genes[i];
//                    point.genes[i] = point.next.genes[i];
//                    point.next.genes[i] = temp;
//
//                    // 消去互换后重复的那个基因
//                    point.genes[fir] = point.next.genes[i];
//                    point.next.genes[sec] = point.genes[i];
//                }
//            }
//        }
//    }

    // 交叉操作
    void crossover(SpeciesPopulation list) {
        // 以概率pcl~pch进行
        float rate = (float) Math.random();
        if (rate > GCPData.pcl && rate < GCPData.pch) {
            SpeciesIndividual point = list.head.next; // 游标
            Random rand = new Random();
            int find = rand.nextInt(list.speciesNum);
            while (point != null && find != 0) {
                point = point.next;
                find--;
            }

            Random random = new Random();
            assert point != null;
            if (point.next != null) {
                int nodeNum = point.genes.length;
                int begin = rand.nextInt(nodeNum);

                // 取point和point.next进行交叉，形成新的两个染色体
                for (int i = begin; i < nodeNum; i++) {
                    int N = random.nextInt(nodeNum);
                    for (int k = 0, j = nodeNum - 1; k < N; ++k, --j) {
                        // 两个基因互换
                        String temp = point.genes[k];
                        point.genes[k] = point.next.genes[j];
                        point.next.genes[j] = temp;
                    }
                }
            }
        }
    }

    // 变异操作
    void mutate(SpeciesPopulation list) {
        SpeciesIndividual point = list.head.next;
        while (point != null) {
            int nodeNum = point.genes.length;
            float rate = (float) Math.random();
            if (rate < GCPData.pm) {
                // 寻找逆转左右端点
                Random rand = new Random();
                int left = rand.nextInt(nodeNum);
                int right = rand.nextInt(nodeNum);
                String temp = point.genes[left];
                point.genes[left] = point.genes[right];
                point.genes[right] = temp;
            }
            point = point.next;
        }
    }

//    // 变异操作
//    void mutate(SpeciesPopulation list) {
//        SpeciesIndividual point = list.head.next;
//        while (point != null) {
//            float rate = (float) Math.random();
//            if (rate < GCPData.pm) {
//                Random rand = new Random();
//                int index = rand.nextInt(NODE_NUM);
//                String color = Integer.toString(rand.nextInt(colorNum));
//                while(color.equals(point.genes[index])){
//                    color = Integer.toString(rand.nextInt(colorNum));
//                }
//                point.genes[index] = color;
//            }
//            point = point.next;
//        }
//    }

//    // 获得适应度最大的物种
//    SpeciesIndividual getBest(SpeciesPopulation list) {
//        int conflict = Integer.MAX_VALUE;
//        SpeciesIndividual bestSpecies = null;
//        SpeciesIndividual point = list.head.next;
//        while (point != null) {
//            if (conflict > point.conflict) {
//                bestSpecies = point;
//                conflict = point.conflict;
//            }
//            point = point.next;
//        }
//        return bestSpecies;
//    }

    // 获得适应度最大的物种列表
    List<SpeciesIndividual> getBest(SpeciesPopulation list) {
        int conflict = Integer.MAX_VALUE;
        List<SpeciesIndividual> bestSpecies = new ArrayList<>();
        List<SpeciesIndividual> bestRate = new ArrayList<>();
        SpeciesIndividual point = list.head.next;
        while (point != null) {
            if (conflict >= point.conflict) {
                if (conflict > point.conflict) {
                    bestSpecies.clear();
                }
                bestSpecies.add(point);
                conflict = point.conflict;
            }
            point = point.next;
        }
        for (int i = 0; i < bestSpecies.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < i; j++) {
                // 剔除基因序列重复或转换为顶点集合后重复情况
                if (Arrays.equals(bestSpecies.get(i).genes, bestSpecies.get(j).genes) || bestSpecies.get(i).geneToSet().equals(bestSpecies.get(j).geneToSet())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                bestRate.add(bestSpecies.get(i));
            }
        }
        return bestRate;
    }

}
