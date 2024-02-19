package npe.game;

import java.awt.*;

import npe.tools.cmdLine.AnsiWrapper;
import npe.tools.cmdLine.CmdCanvas;

import java.awt.image.BufferedImage;

public class NotPhysicsEngineCLI {

    public NotPhysicsEngineCLI() {

        CmdCanvas cc=new CmdCanvas(128,16);
//        cc.graphics2D().drawRect(3,3,10,5);
        cc.render();


    }
}
