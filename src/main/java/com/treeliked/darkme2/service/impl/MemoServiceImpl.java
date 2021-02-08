//package com.treeliked.darkme2.service.impl;
//
//import java.util.List;
//
//import com.treeliked.darkme2.dao.MemoMapper;
//import com.treeliked.darkme2.service.MemoService;
//import com.treeliked.darkme2.util.IdUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * 便签服务接口实现
// *
// * @author lqs2
// * @date 2018-12-20, Thu
// */
//@Service
//@Transactional(rollbackFor = Exception.class)
//public class MemoServiceImpl implements MemoService {
//
//    private final MemoMapper memoMapper;
//
//    @Autowired
//    public MemoServiceImpl(MemoMapper memoMapper) {
//        this.memoMapper = memoMapper;
//    }
//
//    @Override
//    public int getMemoCountByUser(String username) throws Exception {
//        return memoMapper.selectMemoCountByUser(username);
//
//    }
//
//    @Override
//    public int addNewMemo(Memo memo) throws Exception {
//        memo.setId(IdUtils.get32Id());
//        memo.setMemoState((byte) 0);
//        return memoMapper.insert(memo);
//    }
//
//    @Override
//    public List<Memo> getUserMemoByState(String username, boolean finished) throws Exception {
//        return memoMapper.getUserMemosByState(username, finished);
//    }
//
//    @Override
//    public List<Memo> getMemoByValue(String username, String key) throws Exception {
//        return memoMapper.searchMemo(username, key);
//    }
//
//    @Override
//    public int modUserMemo(String id, int toState, boolean isRemoved) throws Exception {
//        if (isRemoved) {
//            return deleteMemoById(id);
//        } else {
//            return memoMapper.modifyMemoStateById(id, toState);
//        }
//    }
//
//    @Override
//    public int deleteMemoById(String id) throws Exception {
//        return memoMapper.deleteByPrimaryKey(id);
//    }
//
//    @Override
//    public Memo getMemoById(String id) throws Exception {
//        return memoMapper.selectByPrimaryKey(id);
//    }
//
//}
