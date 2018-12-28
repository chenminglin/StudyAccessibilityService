package com.bethena.studyaccessibilityservice;

import org.junit.Test;

public class ComputeTest {
    @Test
    public void andTest(){

        int target = 955825732;

        System.out.println(target & 1);
        System.out.println(target & 0x00000080);
        System.out.println(target & 0x00200000);
        System.out.println("---------------");
        System.out.println( 0x00200000);
        System.out.println( 1<<21);
    }


    @Test
    public void orTest(){
        System.out.println(0x20000000 | 0X00010000);
    }
}
