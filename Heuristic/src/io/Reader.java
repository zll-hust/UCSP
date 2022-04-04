package io;


import inputentity.Classes;
import inputentity.Curriculum;
import inputentity.Teacher;

import java.util.*;

public class Reader {
    public static List<Curriculum> getCurInput(String path) throws Exception {
        ArrayList<Curriculum> list = new ArrayList<>();
        List<Map<String, String>> maps = ExcelDataUtil.redExcel(path + "\\C2中.xlsx");
        for (Map<String, String> map : maps) {
            Map<String, String> constraintMap = new HashMap<>();
            String[] constraints = map.get("软约束").split(";");

            for (String s : constraints) {
                if (s == null || s.length() <= 0) {
                    continue;
                }
                String[] ms = s.split(":");
                constraintMap.put(ms[0], ms[1]);
            }

            Curriculum curriculum = new Curriculum(map.get("课程编号"), map.get("课程名称"),
                    Arrays.asList(map.get("专业班级").split(",")),
                    Arrays.asList(map.get("任课教师").split(",")),
                    map.get("总学时"), constraintMap);
//            if (map.size() > 5) {
//                curriculum.setConstraint(Arrays.asList(map.get("软约束").split(";")));
//            }
            list.add(curriculum);
        }
        return list;
    }

    public static List<Teacher> getTeaInput(String path) throws Exception {
        ArrayList<Teacher> list = new ArrayList<>();
        List<Map<String, String>> maps = ExcelDataUtil.redExcel(path + "\\K1.xlsx");
        for (Map<String, String> map : maps) {
            boolean[][] isForbidden = getForbidden(map, true);
            Teacher teacher = new Teacher(map.get("教师编号"), map.get("任课教师"), isForbidden);
            list.add(teacher);
        }
        return list;
    }

    public static List<Classes> getClassInput(String path) throws Exception {
        ArrayList<Classes> list = new ArrayList<>();
        List<Map<String, String>> maps = ExcelDataUtil.redExcel(path + "\\T0.xlsx");
        for (int i = 0; i < maps.size(); i++) {
            Classes classes = new Classes(maps.get(i).get("课程编号"), maps.get(i).get("专业班级"), null);
            //尽管说T2-T5是一样的,T6-T10是一样的，但还是读吧，更具普遍性，后期有需要再改
            List<Map<String, String>> classT = ExcelDataUtil.redExcel(path + "\\T" + (i + 1) + ".xlsx");
            List<Set<Integer>> timeList = new ArrayList<>();
            for (int j = 0; j < 25; j++) {
                Map<String, String> map = classT.get(j);
                timeList.add(getKey(map));   //得到不可安排课程的周次
            }
            classes.setIsForbidden(timeList);
            list.add(classes);
        }
        return list;

    }

    private static Set<Integer> getKey(Map<String, String> map) {
        // 使用for循环遍历
        Set<Integer> set = new HashSet<>();
        boolean flag = true;
        for (Map.Entry<String, String> m : map.entrySet()) {
            if (flag) {
                flag = false;
                continue;
            }
            String key;
//            System.out.println(m.getValue().getClass().getName());
            if (!m.getValue().equals("0")) {
                key = m.getKey();
                set.add((int) Double.parseDouble(key));
            }
        }
        return set;
    }

    private static boolean[][] getForbidden(Map<String, String> map, boolean book) {
        boolean[][] isForbidden = new boolean[18][25];
        // 使用for循环遍历
        boolean flag1 = true, flag2 = true;
        for (Map.Entry<String, String> m : map.entrySet()) {
            if (flag1) {
                flag1 = false;
                continue;
            }
            if (flag2) {
                flag2 = false;
                continue;
            }
            if (!m.getValue().equals("0")) {
                int key = (int) Double.parseDouble(m.getKey());
                String[] values = m.getValue().split(",");
                if (book) {
                    for (String value : values) {
                        int valueInt = Integer.parseInt(value);
                        isForbidden[valueInt - 1][key - 1] = true;
                    }
                } else if (!book) {
                    for (String value : values) {
                        int valueInt = Integer.parseInt(value);
                        isForbidden[key - 1][valueInt - 1] = true;
                    }
                }
            }
        }
        return isForbidden;

    }

}
