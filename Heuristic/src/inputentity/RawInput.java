package inputentity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawInput {
    private List<Curriculum> curriculumList;
    private List<Classes> classList;
    private List<Teacher> teacherList;


    public List<Curriculum> getCurriculumList(Set<Integer> IndexSet) {
        List<Curriculum> theCur = new ArrayList<>();
        // 获取set集合数据
        for (Integer integer : IndexSet) {
            int index = integer;
            theCur.add(getCurriculumList().get(index));
        }
        return theCur;
    }

    public List<Classes> getClassList(Set<Integer> IndexSet) {
        List<Curriculum> confCur = getCurriculumList(IndexSet);
        Set<Integer> confClass = new HashSet<>();
        for (Curriculum c : confCur) {
            for (String classId : c.getClassId()) {
                confClass.add(Integer.parseInt(classId));
            }
        }
        List<Classes> theClass = new ArrayList<>();
        // 获取set集合数据
        for (Integer integer : confClass) {
            int index = integer;
            theClass.add(getClassList().get(index - 1));
        }
        return theClass;
    }
}
