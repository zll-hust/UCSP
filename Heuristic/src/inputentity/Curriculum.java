package inputentity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Curriculum {
    private String curId;   // 课程编号
    private String curName;    // 课程名字
    private List<String> classId;   // 专业班级编号
    private List<String> teacherId;   // 任课教师编号
    private String curHour;    // 课时数量
    private Map<String,String> constraint;    // 软约束


}
