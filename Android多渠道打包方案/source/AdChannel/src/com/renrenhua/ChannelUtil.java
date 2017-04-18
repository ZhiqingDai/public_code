package com.renrenhua;

/**
 * Created by daizhiqing on 2017/4/18.
 * 此类中方法在Android中获取渠道信息
 */
public class ChannelUtil {

    /**
     * 从apk中获取渠道信息
     *
     * @param context
     * @param channelKey
     * @return
     */
    public static String getChannel(Context context, String channelKey) {
        String channel = "";
        try{
            //从apk包中获取
            ApplicationInfo appinfo = context.getApplicationInfo();
            String sourceDir = appinfo.sourceDir;
            //注意这里：默认放在meta-inf/里， 所以需要再拼接一下
            String key = "META-INF/" + channelKey;
            String ret = "";
            ZipFile zipfile = null;
            try {
                zipfile = new ZipFile(sourceDir);
                Enumeration<?> entries = zipfile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName();
                    if (entryName.startsWith(key)) {
                        ret = entryName;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (zipfile != null) {
                    try {
                        zipfile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            String[] split = ret.split("/");
            if (split != null && split.length >= 2) {
                channel = split[split.length-1];
            }
        }catch (Exception e){
            e.printStackTrace();
            channel = "";
        }
        return channel;
    }
}
