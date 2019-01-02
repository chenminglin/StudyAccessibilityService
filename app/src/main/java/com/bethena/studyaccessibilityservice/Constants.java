package com.bethena.studyaccessibilityservice;


public interface Constants {


    String KEY_PARAM1 = "KEY_PARAM1";
    String KEY_PARAM2 = "KEY_PARAM2";


    String KEY_IS_START_CLEAN = "KEY_IS_START_CLEAN";

    /***
     *
     *
     * 每加一个action记得注册加一下
     *
     *
     *
     */
    String ACTION_RECEIVER_ACC_FINISH = "ACTION_RECEIVER_ACC_FINISH";

    String ACTION_RECEIVER_ACC_CLEAN_ONE = "ACTION_RECEIVER_ACC_CLEAN_ONE";

    //中断
    String ACTION_RECEIVER_ACC_CLEAN_INTERCEPTER = "ACTION_RECEIVER_ACC_CLEAN_INTERCEPTER";

    String ACTION_RECEIVER_ACC_CLEAN_BUTTON_NOT_FOUND  = "ACTION_RECEIVER_ACC_CLEAN_BUTTON_NOT_FOUND";
    //该进程已经结束
    String ACTION_RECEIVER_ACC_PROCESS_HAVE_FINISH  = "ACTION_RECEIVER_ACC_PROCESS_HAVE_FINISH";

    String ACTION_RECEIVER_ACC_CLEAN_VIEW_NOT_FOUND  = "ACTION_RECEIVER_ACC_CLEAN_VIEW_NOT_FOUND";

    String ACTION_RECEIVER_ACC_CLEAN_ERROR = "ACTION_RECEIVER_ACC_CLEAN_ERROR";

    String ACTION_RECEIVER_ACC_CLEAN_NEXT_IF_HAVE = "ACTION_RECEIVER_ACC_CLEAN_NEXT_IF_HAVE";

    String ACTION_RECEIVER_ACC_RECORD_ACTIVITY = "ACTION_RECEIVER_ACC_RECORD_ACTIVITY";

    String ACTION_TO_ACC_DOIT = "ACTION_TO_ACC_DOIT";
}
