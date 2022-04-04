package gcp;

public class SpeciesPopulation {
    SpeciesIndividual head; //头结点
    int speciesNum; //物种数量

    public SpeciesPopulation() {
        head = new SpeciesIndividual();
        speciesNum = GCPData.SPECIES_NUM;
    }

    //添加物种
    void add(SpeciesIndividual species) {
        SpeciesIndividual point = head;   // 游标
        while (point.next != null) {    // 寻找表尾结点
            point = point.next;
        }
        point.next = species;
    }

    //遍历
    void traverse() {
        SpeciesIndividual point = head.next;  //游标
        while (point != null) {
            for (int i = 0; i < GCPData.SPECIES_NUM; i++) {
                System.out.print(point.genes[i] + " ");
            }
            System.out.println(point.conflict);
            point = point.next;
        }
        System.out.println("------------------------");
    }
}
