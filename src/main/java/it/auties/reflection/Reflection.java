package it.auties.reflection;

import sun.misc.Unsafe;

import java.io.OutputStream;
import java.lang.reflect.AccessibleObject;
import java.util.NoSuchElementException;

public class Reflection {
    private static final Unsafe unsafe = openUnsafe();
    private static final long offset = findOffset();

    public static <T extends AccessibleObject> T open(T object){
        if(offset != -1){
            unsafe.putBoolean(object, offset, true);
            return object;
        }

        object.setAccessible(true);
        return object;
    }

    private static long findOffset() {
        try {
            var offsetField = AccessibleObject.class.getDeclaredField("override");
            return unsafe.objectFieldOffset(offsetField);
        }catch (Throwable throwable){
            return findOffsetFallback();
        }
    }

    private static long findOffsetFallback() {
        try {
            return unsafe.objectFieldOffset(AccessibleObjectPlaceholder.class.getDeclaredField("override"));
        }catch (Throwable innerThrowable){
            return -1;
        }
    }

    private static Unsafe openUnsafe() {
        try {
            var unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return (Unsafe) unsafeField.get(null);
        }catch (NoSuchFieldException exception){
            throw new NoSuchElementException("Cannot find unsafe field in wrapper class");
        }catch (IllegalAccessException exception){
            throw new UnsupportedOperationException("Access to the unsafe wrapper has been blocked: the day has come. In this future has the OpenJDK team created a publicly available compiler api that can do something? Probably not", exception);
        }
    }

    @SuppressWarnings("all")
    private static class AccessibleObjectPlaceholder {
        boolean override;
        Object accessCheckCache;
    }

    @SuppressWarnings("all")
    public static class ModulePlaceholder {
        boolean first;
        static final Object staticObj = OutputStream.class;
        volatile Object second;
        private static volatile boolean staticSecond;
        private static volatile boolean staticThird;
    }
}