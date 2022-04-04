package gcp;

public class Adjacent {
    public int neighbor;
    public Adjacent next;

    public Adjacent() {
        this.neighbor = 0;
        this.next = null;
    }

    public static void link_neighbor(Adjacent[] matrix, int x1, int x2) {
        // add x2 to x1's neighbor list
        Adjacent p1 = matrix[x1];
        matrix[x1].neighbor += 1;
        while (p1.next != null) {
            p1 = p1.next;
        }
        // 建立x2结点
        Adjacent q1 = new Adjacent();
        q1.neighbor = x2;
        q1.next = null;

        p1.next = q1;
    }
}
