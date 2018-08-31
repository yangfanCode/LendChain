package com.lend.lendchain.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.lend.lendchain.helper.ContextHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**SD卡是否挂载
 * Created by yangfan on 2017/6/16.
 */
public class SDCardMounted {

    public static boolean isSDMounted() {
        boolean isMounted = false;
        StorageManager sm = (StorageManager) ContextHelper.getApplication().getSystemService(Context.STORAGE_SERVICE);

        try {
            Method getVolumList = StorageManager.class.getMethod("getVolumeList",  new Class<?>[]{});
            getVolumList.setAccessible(true);
            Object[] results = (Object[])getVolumList.invoke(sm, new Object[]{});
            if (results != null) {
                for (Object result : results) {
                    Method mRemoveable = result.getClass().getMethod("isRemovable", new Class<?>[]{});
                    Boolean isRemovable = (Boolean) mRemoveable.invoke(result, new Object[]{});
                    if (isRemovable) {
                        Method getPath = result.getClass().getMethod("getPath", new Class<?>[]{});
                        String path = (String) mRemoveable.invoke(result, new Object[]{});
                        Method getState = sm.getClass().getMethod("getVolumeState", String.class);
                        String state = (String)getState.invoke(sm, path);
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            isMounted = true;
                            break;
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return isMounted;
    }


    /*
    是否存在外置SD卡
       * avoid initializations of tool classes
       */
    /**
     * 9      * @Title: getExtSDCardPaths
     * 10      * @Description: to obtain storage paths, the first path is theoretically
     * 11      *               the returned value of
     * 12      *               Environment.getExternalStorageDirectory(), namely the
     * 13      *               primary external storage. It can be the storage of internal
     * 14      *               device, or that of external sdcard. If paths.size() >1,
     * 15      *               basically, the current device contains two type of storage:
     * 16      *               one is the storage of the device itself, one is that of
     * 17      *               external sdcard. Additionally, the paths is directory.
     * 18      * @return List<String>
     * 19      * @throws IOException
     * 20
     */
    public static List<String> getExtSDCardPaths() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED)
                && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
            paths.add(extFile.getAbsolutePath());
        }
        try {
            // obtain executed result of command line code of 'mount', to judge
            // whether tfCard exists by the result
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile
                        .getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                paths.add(mountPath);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paths;
    }
}
