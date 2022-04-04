package gcp.curclass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cur {
    private int supId;  // 课程对应编号,编号从0开始
    //    private String CurId; // 课时对应编号
    private int twoNum; // 2课时数量
    private int threeNum;   // 3课时数量
    private Map<String, String> constraint;

    public Cur(Cur another) {
        this.supId = another.supId;
        this.twoNum = another.twoNum;
        this.threeNum = another.threeNum;
        this.constraint = another.constraint;
    }
}
