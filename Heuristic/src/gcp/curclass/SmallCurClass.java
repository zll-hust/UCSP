package gcp.curclass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmallCurClass {
    private int supId;  // 颜色编号
    private int smallCurNum; // 2课时数量最少
    private List<Cur> CurList;    // 小图着色课程集合
}
