package com.admin.mymusic.javaBean.gson;


import java.util.List;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/4/1 20:55
 **/
public class SearchDataBean {



    private int code;
    private long curTime;
    private DataBean data;
    private String msg;
    private String profileId;
    private String reqId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getCurTime() {
        return curTime;
    }

    public void setCurTime(long curTime) {
        this.curTime = curTime;
    }

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

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public static class DataBean {


        private String total;
        private List<ListBean> list;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {

            private String musicrid;
            private String artist;
            private MvpayinfoBean mvpayinfo;
            private String pic;
            private int isstar;
            private int rid;
            private int duration;
            private String content_type;
            private int track;
            private boolean hasLossless;
            private int hasmv;
            private String releaseDate;
            private String album;
            private int albumid;
            private String pay;
            private int artistid;
            private String albumpic;
            private String songTimeMinutes;
            private boolean isListenFee;
            private String pic120;
            private String name;
            private int online;
            private PayInfoBean payInfo;

            public String getMusicrid() {
                return musicrid;
            }

            public void setMusicrid(String musicrid) {
                this.musicrid = musicrid;
            }

            public String getArtist() {
                return artist;
            }

            public void setArtist(String artist) {
                this.artist = artist;
            }

            public MvpayinfoBean getMvpayinfo() {
                return mvpayinfo;
            }

            public void setMvpayinfo(MvpayinfoBean mvpayinfo) {
                this.mvpayinfo = mvpayinfo;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public int getIsstar() {
                return isstar;
            }

            public void setIsstar(int isstar) {
                this.isstar = isstar;
            }

            public int getRid() {
                return rid;
            }

            public void setRid(int rid) {
                this.rid = rid;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public String getContent_type() {
                return content_type;
            }

            public void setContent_type(String content_type) {
                this.content_type = content_type;
            }

            public int getTrack() {
                return track;
            }

            public void setTrack(int track) {
                this.track = track;
            }

            public boolean isHasLossless() {
                return hasLossless;
            }

            public void setHasLossless(boolean hasLossless) {
                this.hasLossless = hasLossless;
            }

            public int getHasmv() {
                return hasmv;
            }

            public void setHasmv(int hasmv) {
                this.hasmv = hasmv;
            }

            public String getReleaseDate() {
                return releaseDate;
            }

            public void setReleaseDate(String releaseDate) {
                this.releaseDate = releaseDate;
            }

            public String getAlbum() {
                return album;
            }

            public void setAlbum(String album) {
                this.album = album;
            }

            public int getAlbumid() {
                return albumid;
            }

            public void setAlbumid(int albumid) {
                this.albumid = albumid;
            }

            public String getPay() {
                return pay;
            }

            public void setPay(String pay) {
                this.pay = pay;
            }

            public int getArtistid() {
                return artistid;
            }

            public void setArtistid(int artistid) {
                this.artistid = artistid;
            }

            public String getAlbumpic() {
                return albumpic;
            }

            public void setAlbumpic(String albumpic) {
                this.albumpic = albumpic;
            }

            public String getSongTimeMinutes() {
                return songTimeMinutes;
            }

            public void setSongTimeMinutes(String songTimeMinutes) {
                this.songTimeMinutes = songTimeMinutes;
            }

            public boolean isIsListenFee() {
                return isListenFee;
            }

            public void setIsListenFee(boolean isListenFee) {
                this.isListenFee = isListenFee;
            }

            public String getPic120() {
                return pic120;
            }

            public void setPic120(String pic120) {
                this.pic120 = pic120;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getOnline() {
                return online;
            }

            public void setOnline(int online) {
                this.online = online;
            }

            public PayInfoBean getPayInfo() {
                return payInfo;
            }

            public void setPayInfo(PayInfoBean payInfo) {
                this.payInfo = payInfo;
            }

            public static class MvpayinfoBean {


                private int play;
                private int vid;
                private int down;

                public int getPlay() {
                    return play;
                }

                public void setPlay(int play) {
                    this.play = play;
                }

                public int getVid() {
                    return vid;
                }

                public void setVid(int vid) {
                    this.vid = vid;
                }

                public int getDown() {
                    return down;
                }

                public void setDown(int down) {
                    this.down = down;
                }
            }

            public static class PayInfoBean {


                private String play;
                private int cannotDownload;
                private FeeTypeBean feeType;
                private int cannotOnlinePlay;
                private String down;

                public String getPlay() {
                    return play;
                }

                public void setPlay(String play) {
                    this.play = play;
                }

                public int getCannotDownload() {
                    return cannotDownload;
                }

                public void setCannotDownload(int cannotDownload) {
                    this.cannotDownload = cannotDownload;
                }

                public FeeTypeBean getFeeType() {
                    return feeType;
                }

                public void setFeeType(FeeTypeBean feeType) {
                    this.feeType = feeType;
                }

                public int getCannotOnlinePlay() {
                    return cannotOnlinePlay;
                }

                public void setCannotOnlinePlay(int cannotOnlinePlay) {
                    this.cannotOnlinePlay = cannotOnlinePlay;
                }

                public String getDown() {
                    return down;
                }

                public void setDown(String down) {
                    this.down = down;
                }

                public static class FeeTypeBean {


                    private String song;
                    private String vip;

                    public String getSong() {
                        return song;
                    }

                    public void setSong(String song) {
                        this.song = song;
                    }

                    public String getVip() {
                        return vip;
                    }

                    public void setVip(String vip) {
                        this.vip = vip;
                    }
                }
            }
        }
    }
}
