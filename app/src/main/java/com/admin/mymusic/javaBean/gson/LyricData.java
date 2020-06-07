package com.admin.mymusic.javaBean.gson;

import java.util.List;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/4/5 14:15
 **/
public class LyricData {

    private DataBean data;
    private String msg;
    private String reqid;
    private int status;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReqid() {
        return reqid;
    }

    public void setReqid(String reqid) {
        this.reqid = reqid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class DataBean {

        private List<LrclistBean> lrclist;


        public List<LrclistBean> getLrclist() {
            return lrclist;
        }

        public void setLrclist(List<LrclistBean> lrclist) {
            this.lrclist = lrclist;
        }
        public static class LrclistBean {

            private String time;
            private String lineLyric;

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getLineLyric() {
                return lineLyric;
            }

            public void setLineLyric(String lineLyric) {
                this.lineLyric = lineLyric;
            }
        }
    }

}
