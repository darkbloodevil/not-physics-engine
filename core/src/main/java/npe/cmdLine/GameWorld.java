package npe.cmdLine;

import npe.cmdLine.*;
import npe.tools.MapForJava;

public class GameWorld {
    com.artemis.World engine;
    CmdCanvas cmdCanvas;

    public GameWorld() {
        init();
    }

    private void init() {
        cmdCanvas = new CmdCanvas(128, 10);
    }

    private void initialize_engine() {

    }

    public void create() {
        var textBlock = new CmdTextBlock(20, 5);
        /**
         * 牌型有如下：
         * 同花顺
         * 葫芦
         * 同花
         * 顺子
         * 四条
         * 三条
         * 两对
         * 一对
         * 高牌
         */
        textBlock.update_property("name", "March 7th");
        textBlock.update_property("sex", "female");
        textBlock.update_color("name", new AnsiWrapper(AnsiWrapper.RED()), CmdTextBlock.VALUE_TAR());
        

        var textBlock2 =new CmdTextBlock(10, 10);
        textBlock2.update_property("name", "darkbloodevil");
        textBlock2.update_property("kind", "evil");
        textBlock2.update_color("name", new AnsiWrapper(AnsiWrapper.RED()), CmdTextBlock.VALUE_TAR());
        textBlock2.set_symbols_order(new String[]{"", "name", "-", "kind", "#"});


        var textBlock3 = textBlock.clone_prototype();
        textBlock3.update_property("name", "<name>");
        textBlock3.update_property("sex", "<sex>");
        MapForJava<String,Object> map=new MapForJava<>();
        map.put("year","<year>");
        map.put("job","<job>");
        textBlock3.update_properties(map.get_map());
        cmdCanvas.add_renderer(textBlock);
        cmdCanvas.add_renderer(textBlock2);
        cmdCanvas.add_renderer(textBlock3);
        
    }

    public void update() {
        cmdCanvas.render();
    }
}
