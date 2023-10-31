package tests;

import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class test {
    public static void main(String[] args) {
        //List temp = Arrays.asList(1.1f, 2.2f, 3.3f);
//        double[] a=temp.stream().mapToDouble(i->(double)i).mapToObj(i->(Float)i);
//        assert false;
//        System.out.println("haha");
        Music music = Gdx.audio.newMusic(Gdx.files.internal("sound_test.mp3"));
        music.play();

    }
}
