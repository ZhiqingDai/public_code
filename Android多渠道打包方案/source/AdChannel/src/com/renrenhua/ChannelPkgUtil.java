package com.renrenhua;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 用户APP多渠道打包工具
 * @author daizhiqing
 *
 */
public class ChannelPkgUtil {

    /**
     * 空文件 用于写入app
     */
    static String srcEmptyFileName = "info/empty.txt";
    /**
     * 渠道名称
     */
    static String channelFileName = "info/channel.txt"; //渠道相关信息

    static final String CHANNEL_PATH = "META-INF/rrh/"; //APK生成渠道区别目录

    static String OUTPUT_PATH = "output/rrh-";  //打包文件输出路径 以及前缀

    ArrayList<String>  channelList = new ArrayList<>();

    private static final byte[] BUFFER = new byte[4096 * 1024];

    public static void copy(InputStream input, OutputStream output) throws IOException {
        int bytesRead;
        while ((bytesRead = input.read(BUFFER))!= -1) {
            output.write(BUFFER, 0, bytesRead);
        }
    }

    public void channel(String srcPath){
        try {

            File srcFile  =  new File(srcPath);
            File emptyFile = new File(srcEmptyFileName);

            if(!emptyFile.exists()){
                emptyFile.createNewFile();
            }

            File channelFile = new File(channelFileName);

            FileReader reader = new FileReader(channelFile);
            BufferedReader br = new BufferedReader(reader);
            String line ;
            while ((line  = br.readLine())!= null){
                channelList.add(line);
            }
            close(br);
            close(reader);

            String apkName = "";

            for (String channel:channelList) {
                File cFile = new File(channel.trim());
                emptyFile.renameTo(cFile);
                apkName = OUTPUT_PATH+channel.trim()+".apk";

                File tempFile = new File(apkName);
                if(tempFile.exists()){
                    tempFile.delete();
                }
                tempFile.createNewFile();
//                nioTransferCopy(srcFile , tempFile);

                ZipFile zipFile = new ZipFile(srcFile);
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(tempFile ));

                Enumeration<? extends ZipEntry> entries =  zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry e = entries.nextElement();
                    zipOutputStream.putNextEntry(new ZipEntry(e.getName()));
                    if (!e.isDirectory()) {
                        copy(zipFile.getInputStream(e), zipOutputStream);
                    }
                    zipOutputStream.closeEntry();
                }

                ZipEntry zipEntry = new ZipEntry(CHANNEL_PATH+channel);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.closeEntry();
                close(zipOutputStream);
                cFile.delete();
                System.out.println("打包完成: " + apkName);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件
     * @param source
     * @param target
     */
    @Deprecated
    private  void nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inStream);
            close(in);
            close(outStream);
            close(out);
        }
    }

    /**
     * 关闭流
     * @param closeable
     */
    private void close(Closeable closeable){
        if(closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        ChannelPkgUtil channelPkgUtil = new ChannelPkgUtil();
        for (String arg : args) {
            OUTPUT_PATH += arg.replace(".apk","")+"-";
            channelPkgUtil.channel(arg);
        }
    }
}
