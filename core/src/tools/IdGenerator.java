package tools;
import static java.lang.System.out;

public class IdGenerator {
    long start_time=1694426839985L;
    long last_time=0L;
    long sequence=0L;

    public static void main(String[] args) {
        out.println(System.currentTimeMillis());
        out.println(Long.toBinaryString(1694426839985L));
        out.println(Long.toBinaryString(1694426839985L<<5));
        out.println(Long.toBinaryString((1694426839985L<<5)+1));

        out.println(Integer.bitCount(Integer.MAX_VALUE));
        IdGenerator idGenerator=new IdGenerator();
        for (int i = 0; i < 1000; i++) {
            out.println(Long.toBinaryString(idGenerator.nextId()));
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

        return (current_time<<30) +sequence;
    }

}
