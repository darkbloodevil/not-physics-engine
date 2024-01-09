package game;

import java.awt.*;

import tools.cmdLine.AnsiWrapper;
import tools.cmdLine.CmdCanvas;

import java.awt.image.BufferedImage;

public class NotPhysicsEngineCLI {
    // ANSI颜色代码
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String BOLD_RED = "\033[1;31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public NotPhysicsEngineCLI() {
        System.out.println(RED + " two heart ♥♥ is ok" + RESET);
        System.out.println(BOLD_RED + " two heart ♥♥ is ok" + RESET);
        System.out.println(PURPLE + " one heart ♥ is not" + RESET);

        CmdCanvas cc=new CmdCanvas(128,64);
//        cc.graphics2D().drawRect(3,3,10,5);
        cc.render();
        
//        System.out.format("%-10s %-10s %-10s%n","ha","wa","ka");
//        System.out.format("   %-10s %-10s %-10s%n","ha","wa","ka");

        // https://stackoverflow.com/questions/7098972/ascii-art-java
//        BufferedImage image = new BufferedImage(144, 32, BufferedImage.TYPE_INT_RGB);
//        Graphics graphics = image.getGraphics();
//        graphics.setFont(new Font("Dialog", Font.ITALIC, 14));
//        Graphics2D graphics2D = (Graphics2D) graphics;
//        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        graphics2D.drawString(String.join(" ", new String[]{"Hello,", "world"}), 6, 24);
//        graphics2D.drawRect(0, 12, 72, 20);
//        String ha = "你好";
//        AnsiWrapper ansiWrapper = new AnsiWrapper(AnsiWrapper.ITALIC(),new AnsiWrapper(AnsiWrapper.BG_MAGENTA()));
//        for (int y = 0; y < 32; y++) {
//            StringBuilder builder = new StringBuilder();
//            for (int x = 0; x < 144; x++)
//                builder.append(image.getRGB(x, y) == -16777216 ? " " : image.getRGB(x, y) == -1 ? "#" : "*");
//
//            if (y == 16 || y == 17) {
//
//                int verbose_length = 8;
//                builder.replace(16, 17 + verbose_length, ("%-" + verbose_length + "s").formatted(ha));
//            }
//            if (builder.toString().trim().isEmpty()) continue;
//            var result = builder.toString();
//            if (y == 16) {
//                result = ansiWrapper.wrap(result, ha, true);
//            }
//            System.out.println(result);
//        }

    }
}
