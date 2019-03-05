package com.treeliked.darkme2;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class Darkme2ApplicationTests {

    @Test
    public void contextLoads() {
    }



    @Test
    public void test3() throws IOException {

        String s = "my，t，e，st.tx。t。";
        System.out.println(s.replace(".", "#"));
        System.out.println(s.replaceAll("tx", "#"));
        System.out.println(s.replaceFirst("\\.", "#"));


        String s2 = "1\\2\\3";
        System.out.println(s2);
        System.out.println(s2.replaceAll("\\\\","\\\\\\\\"));
    }

}

