package com.example.moneymgmt_v2;

public interface IDataAccess {
    public ActivityModel loadActivity(int activityID);
    public int saveActivity(ActivityModel activity);
}
