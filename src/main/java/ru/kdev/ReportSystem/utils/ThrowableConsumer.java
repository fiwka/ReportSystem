package ru.kdev.ReportSystem.utils;

public interface ThrowableConsumer<Object, Cause extends Throwable> {

    void accept(Object object) throws Cause;

}
