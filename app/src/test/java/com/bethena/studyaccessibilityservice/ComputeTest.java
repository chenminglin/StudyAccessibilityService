package com.bethena.studyaccessibilityservice;

import com.bethena.studyaccessibilityservice.bean.ProcessInfo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ComputeTest {
    @Test
    public void andTest() {

        int target = 955825732;

        System.out.println(target & 1);
        System.out.println(target & 0x00000080);
        System.out.println(target & 0x00200000);
        System.out.println("---------------");
        System.out.println(0x00200000);
        System.out.println(1 << 21);
    }


    @Test
    public void orTest() {

        Observable.intervalRange(0, 4, 0, 400, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                System.out.println("" + aLong);
            }
        });

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ProcessInfo> list = new ArrayList<>();

        Collections.sort(list, new Comparator<ProcessInfo>() {
            @Override
            public int compare(ProcessInfo o1, ProcessInfo o2) {
                if (o1.size > o2.size) {
                    return 1;
                } else if (o1.size < o2.size) {
                    return 2;
                }
                return 0;
            }
        });

    }
}
