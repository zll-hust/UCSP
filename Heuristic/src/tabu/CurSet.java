package tabu;

import gcp.curclass.Cur;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class CurSet {
    private Set<Cur> curSet;
    private int curSetId;
    private boolean[][] forbidden;
//    private ArrayList<Map<String, String>> constraints;
    private List<String> teachers;
}
