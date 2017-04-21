package com.angaria.languagematch.entities;

import java.util.Date;

public class SubTitleBuilder extends SubTitle {

    public static SubTitleBuilder getInstance(){
        return new SubTitleBuilder();
    }

    public SubTitleBuilder startDate(Date date){
        this.startDate = date;
        return this;
    }

    public SubTitleBuilder endDate(Date date){
        this.endDate = date;
        return this;
    }

    public SubTitleBuilder language(String language){
        this.language = language;
        return this;
    }

    public SubTitleBuilder srtObject(SRTObject srtObject){
        this.srtObject = srtObject;
        return this;
    }

    public SubTitleBuilder content(String content){
        this.content = content;
        return this;
    }

    public SubTitle build(){
        return new SubTitle(startDate , endDate , language, srtObject, content);
    }
}
