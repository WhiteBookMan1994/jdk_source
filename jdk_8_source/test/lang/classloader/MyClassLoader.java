package lang.classloader;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 自定义类加载器：
 * 1、如果不想打破父委派模型，那么只需要重写findClass方法即可
 * 2、如果想打破父委派模型，那么就重写整个loadClass方法
 *
 * @author dingchenchen
 * @since 2020/9/27
 */
public class MyClassLoader extends ClassLoader {
    /*@Override
    public Class<?> loadClass(String name) {
        File file = getClassFile(name);
        byte[] byteArray = getData(file);
        Class clazz = defineClass(name, byteArray, 0, byteArray.length);
        return clazz;
    }*/

    @Override
    public Class<?> findClass(String name) {
        File file = getClassFile(name);
        byte[] byteArray = getData(file);
        Class clazz = defineClass(name, byteArray, 0, byteArray.length);
        return clazz;
    }

    //返回类的字节码
    private byte[] getData(File file) {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private File getClassFile(String name) {
        //File file = new File("/Users/dingchenchen/Downloads/" + name + ".class");
        File file = new File("/Users/dingchenchen/Downloads/Test.class");
        return file;
    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        MyClassLoader myClassLoader = new MyClassLoader();
        Object test = myClassLoader.loadClass("com.souche.finance.supply.common.Test").newInstance();
        Method method = test.getClass().getMethod("say", null);
        method.invoke(test, null);
    }

}
