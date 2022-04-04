package inputentity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    private String teacherId;   // 教师编号
    private String teacherName; // 任课教师姓名
    private boolean[][] isForbidden;    // 教师不可教周次
}
