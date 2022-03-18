package com.github.xjs.util;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CRUDUtil {

    private CRUDUtil(){}

    /**
     * 根据输入list和db list，拆分成add、update、delete 3个部分
     * @param inputList 输入list
     * @param dbList 数据库list
     * @param handler
     * */
    public static <DTO,DO> GroupResult<DO> groupByAddUpdDel(List<DTO> inputList, List<DO> dbList, GroupHandler<DTO,DO> handler){
        // input不能为空，
        if(inputList == null){
            throw new IllegalArgumentException("inputList is nulll");
        }
        inputList = new ArrayList<>(inputList);
        dbList = new ArrayList<>(dbList);
        GroupResult<DO> result = new GroupResult<DO>();
        //db为空，则全部insert
        if(CollectionUtil.isEmpty(dbList)){
            for(DTO input : inputList){
                DTO combined = handler.preInsert(input);
                if(combined != null){
                    DO domain = handler.dtoToDo(combined);
                    result.getAddList().add(domain);
                }
            }
            return result;
        }
        for(int i=0; i<inputList.size(); i++){
            DTO input = inputList.get(i);
            DO db = null;
            for(DO dbElem : dbList){
                if(handler.isMatches(input, dbElem)){
                    db = dbElem;
                    break;
                }
            }
            //input有    db没有 需要insert
            if(db == null){
                DTO combined = handler.preInsert(input);
                if(combined != null){
                    DO domain = handler.dtoToDo(combined);
                    result.getAddList().add(domain);
                }
            }else {
                // input有    db有   需要update
                DTO combined = handler.preUpdate(db, input);
                if(combined != null){
                    DO domain = handler.dtoToDo(combined);
                    result.getUpdList().add(domain);
                }
                dbList.remove(db);
            }
            inputList.remove(i);
            i--;
        }
        for(DO db : dbList){
            DTO input = null;
            for(DTO inputElem : inputList){
                if(handler.isMatches(inputElem, db)){
                    input = inputElem;
                    break;
                }
            }
            //input没有  db有   需要delete
            if(input == null){
                DO combined = handler.preDelete(db);
                if(combined != null){
                    result.getDelList().add(combined);
                }
            }
        }
        return result;
    }

    public interface GroupHandler<DTO,DO>{
        /**比较两个元素是否相等*/
        boolean isMatches(DTO input, DO db);
        /**dto to do, BeanUtils或者MapStruct*/
        DO dtoToDo(DTO input);
        /**在insert前，可以手动设置id*/
        default DTO preInsert(DTO input){
            return input;
        }
        /**在update前，可以设置更新人与更新时间*/
        default DTO preUpdate(DO db, DTO input){
            return input;
        }
        /**在delete之前，可以设置逻辑删除标志*/
        default DO preDelete(DO db){
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
        public List<T> getUpdList() {
            return updList;
        }
        public List<T> getDelList() {
            return delList;
        }
    }

    public static void main(String[] args) {
        List<Integer> input = Arrays.asList(1,2,3);
        List<Integer> db = Arrays.asList(3,4);
        GroupResult<Integer> result = groupByAddUpdDel(input, db, new GroupHandler<Integer, Integer>() {
            @Override
            public boolean isMatches(Integer t1, Integer t2) {
                return t1.equals(t2);
            }
            @Override
            public Integer dtoToDo(Integer elem) {
                return elem;
            }
        });
        System.out.println("add:" + result.getAddList());
        System.out.println("upd:" + result.getUpdList());
        System.out.println("del:" + result.getDelList());

        List<UserDTO> inputUsers = Arrays.asList(new UserDTO(1,"xjs"), new UserDTO("aaa"),new UserDTO("bbb"));
        List<UserDO> dbUsers = Arrays.asList(new UserDO(1,"xjs-old"), new UserDO(2,"xxx"));
        GroupResult<UserDO> groupResult = groupByAddUpdDel(inputUsers, dbUsers, new GroupHandler<UserDTO, UserDO>() {
            @Override
            public boolean isMatches(UserDTO t1, UserDO t2) {
                return t1.getId()!=null && t1.getId().equals(t2.getUserId());
            }
            @Override
            public UserDO dtoToDo(UserDTO userDTO) {
                UserDO userDO = new UserDO(userDTO.getId(), userDTO.getName());
                return userDO;
            }
        });
        System.out.println("add:" + JSON.toJSONString(groupResult.getAddList()));
        System.out.println("upd:" + JSON.toJSONString(groupResult.getUpdList()));
        System.out.println("del:" + JSON.toJSONString(groupResult.getDelList()));
    }

    public static class UserDTO{
        private Integer id;
        private String name;

        public UserDTO(String name){
            this.name = name;
        }

        public UserDTO(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class UserDO{
        private Integer userId;
        private String userName;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public UserDO(Integer userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }
    }
}
