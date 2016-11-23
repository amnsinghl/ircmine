package ircmine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by aman on 22/11/16.
 */
public class MapReduce {

    public List<Callable<String>> list = new ArrayList<>();

    public MapReduce() {

    }

    public void add(Callable<String> st) {
        list.add(st);
    }

    public void waito() {
        CountDownLatch cl = new CountDownLatch(list.size());
        for (Callable<String> st : list) {
            new Thread(() -> {
                try {
                    st.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            cl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
