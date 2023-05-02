package com.example.dutmaintenance;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mcampusSpinner;
    private String mblockSpinner;
    private String mfloorEditText;
    private String mproblemEditText;
    private String mproblemSpinner;
    private String mkey;
    private String memail;
    private String mImageUrl;
    private String mstatus;
    private String userId;
    private String assignedPerson;
    private String adminComment;

    public Upload(){

    }
    public  Upload(String campus,String block,String floor,String problem,String problemS,String ImageUrl){



        mcampusSpinner= campus;
        mblockSpinner=block;
        mfloorEditText=floor;
        mproblemEditText=problem;
        mproblemSpinner=problemS;
        mstatus="not seen";
        mImageUrl=ImageUrl;

    }

    public String getCampus() {
        return mcampusSpinner;
    }

    public void setCampus(String campus) {
        mcampusSpinner = campus;
    }

    public String getBlock() {
        return mblockSpinner;
    }

    public void setBlock(String block) {
        mblockSpinner = block;
    }

    public String getFloor() {
        return mfloorEditText;
    }

    public void setFloor(String floor) {
        mfloorEditText = floor;
    }

    public String getProblem() {
        return mproblemEditText;
    }

    public void setProblem(String problem) {
        mproblemEditText = problem;
    }

    public String getProblemS() {
        return mproblemSpinner;
    }

    public void setProblemS(String problemS) {
        mproblemSpinner = problemS;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
       mImageUrl = ImageUrl;
    }
    public String getStatus(){
        return mstatus;
    }
    public void setStatus(String status){
        mstatus=status;
    }
    @Exclude
    public String getkey(){
        return mkey;
    }
    @Exclude
    public void setkey(String key){
        mkey=key;
    }
    @Exclude
    public String getemail(){
        return memail;
    }
    @Exclude
    public void setemail(String email){
        memail=email;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return this.userId;
    }



    public String getAssignedPerson() {
        return this.assignedPerson;
    }
    public void setAssignedPerson(String assignedPerson){
        this.assignedPerson=assignedPerson;
    }
    public String getAdminComment(){
        return  this.adminComment;
    }
    public void setAdminComment(String adminComment){
        this.adminComment= adminComment;
    }
}

