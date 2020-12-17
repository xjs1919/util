package com.github.xjs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CRUDUtil {

    private CRUDUtil(){}

    /**
     * 根据输入list和db list，拆分成add、update、delete 3个部分
     * @param inputList 输入list
     * @param dbList 数据库list
     * @param matcher 比较器
     * */
    public static <T> GroupResult<T> groupByCRUD(List<T> inputList, List<T> dbList, Matcher<T> matcher){
        // input不能为空，
        if(inputList == null){
            throw new IllegalArgumentException("inputList is nulll");
        }
        GroupResult<T> result = new GroupResult<T>();
        //db为空，则全部insert
        if(CollectionUtil.isEmpty(dbList)){
            for(T input : inputList){
                T combined = matcher.preInsert(input);
                if(combined != null){
                    result.getAddList().add(combined);
                }
            }
            return result;
        }
        for(T input : inputList){
            T db = find(dbList, input, matcher);
            //input有    db没有 需要insert
            if(db == null){
                T combined = matcher.preInsert(input);
                if(combined != null){
                    result.getAddList().add(combined);
                }
            }
            // input有    db有   需要update
            if(db != null){
                T combined = matcher.preUpdate(db, input);
                if(combined != null){
                    result.getUpdList().add(combined);
                }
            }
        }
        for(T db : dbList){
            T input = find(inputList, db, matcher);
            //input没有  db有   需要delete
            if(input == null){
                T combined = matcher.preDelete(db);
                if(combined != null){
                    result.getDelList().add(combined);
                }
            }
        }
        return result;
    }

    public static <T> T find(List<T> list, T e, Matcher<T> matcher) {
        for(T elem : list){
            if(matcher.isMatches(elem, e)){
                return elem;
            }
        }
        return null;
    }

    public static <T> boolean contains(List<T> list, T e, Matcher<T> matcher) {
        T ret = find(list, e, matcher);
        return ret != null;
    }

    public interface Matcher<T>{

        /**比较两个元素是否相等*/
        boolean isMatches(T t1, T t2);

        /**在insert前，可以手动设置id号*/
        default T preInsert(T input){
            return input;
        }

        /**在update前，可以把db中的id赋值给input*/
        default T preUpdate(T db, T input){
            return input;
        }

        /**在delete之前，可以设置逻辑删除标志*/
        default T preDelete(T db){
            return db;
        }

    }

    public static class GroupResult<T>{
        private List<T> addList = new ArrayList<>();
        private List<T> updList = new ArrayList<>();
        private List<T> delList = new ArrayList<>();

        public List<T> getAddList() {
            return addList;
        }

        public void setAddList(List<T> addList) {
            this.addList = addList;
        }

        public List<T> getUpdList() {
            return updList;
        }

        public void setUpdList(List<T> updList) {
            this.updList = updList;
        }

        public List<T> getDelList() {
            return delList;
        }

        public void setDelList(List<T> delList) {
            this.delList = delList;
        }
    }

    public static void main(String[] args) {
        List<Integer> input = Arrays.asList(1,2,3);
        List<Integer> db = Arrays.asList(3,4);
        GroupResult<Integer> result = groupByCRUD(input, db, new Matcher<Integer>() {
            @Override
            public boolean isMatches(Integer t1, Integer t2) {
                return t1.equals(t2);
            }
        });
        System.out.println("add:" + result.getAddList());
        System.out.println("upd:" + result.getUpdList());
        System.out.println("del:" + result.getDelList());
    }
}
