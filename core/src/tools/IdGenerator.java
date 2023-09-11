package tools;
import static java.lang.System.out;

public enum IdGenerator {
    INSTANCE;
    long last_time=0L;
    long sequence=0L;


    public static void main(String[] args) {

        out.println(Integer.bitCount(Integer.MAX_VALUE));
        for (int i = 0; i < 1000; i++) {
            out.println(Long.toBinaryString(IdGenerator.INSTANCE.nextId()));
        }
    }

    public synchronized long nextId(){
        long current_time=System.currentTimeMillis();

        if (last_time>=current_time){
            sequence++;
        }else {
            sequence=0L;
            last_time=current_time;
        }

        return (current_time<<22) +sequence;
    }

}
