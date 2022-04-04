package inputentity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Classes {
    private String ClassId; // 班级编号
    private String ClassName;   // 班级名称
//    private boolean[][] isForbidden;  // 25段中班级不可排周次
    private List<Set<Integer>> isForbidden;  // 25段中班级不可排周次
}
