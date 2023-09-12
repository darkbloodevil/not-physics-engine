package tools;
import java.util.Random;

import static java.lang.System.out;

public enum IdGenerator {
    INSTANCE;
    long last_time=0L;
    long sequence=0L;

    int random_int=0;

    public static void main(String[] args) {

        out.println(Integer.bitCount(Integer.MAX_VALUE));
        for (int i = 0; i < 1000; i++) {
            out.println(Long.toBinaryString(IdGenerator.INSTANCE.nextId()));

        }
    }

    /**
     * 生成不重复的id
     * @return id
     */
    public synchronized long nextId(){
        long current_time=System.currentTimeMillis();

        if (last_time>=current_time){
            sequence++;
        }else {
            sequence=0L;
            random_int=(new Random()).nextInt(2047);
            last_time=current_time;
        }

        return (current_time<<21)+((long) random_int <<9) +sequence;
    }

}
