package com.admin.mymusic.javaBean;

/**
 * 需要用到的常量
 *
 * @author JQ
 */
public class Music {
    /**
     * 收索类型
     *
     * @author JQ
     */
    public enum SearchType {
        SONG("song", "音乐"),
        SINGER("singer", "歌手"),
        ALBUM("album", "专辑"),
        SONGLIST("songList", "歌单"),
        VIDEO("video", "视频"),
        RADIO("radio", "电台"),
        USER("user", "用户"),
        LRC("lrc", "歌词");
        private final String name;
        private final String desc;

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        SearchType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }


    public enum PlayType {
        SINGLE("single", "单曲循环"),
        LIST("list", "列表循环"),
        RAMDOM("random", "随机播放"),
        RANDOM_LIST("randomList", "列表随机播放");
        private final String name;
        private final String desc;

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        PlayType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }


    public enum QualityType {
        K1000("1000kape", "ape"),
        K320("320kmp3", "mp3"),
        K192("192kmp3", "mp3"),
        K128("128kmp3", "mp3");

        private final String name;
        private final String desc;

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        QualityType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }


}
