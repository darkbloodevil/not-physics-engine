package npe.game;

import npe.cmdLine.*;

public class NotPhysicsEngineCLI {
    GameWorld gameWorld;
    InputParser parser;

    public NotPhysicsEngineCLI() {
        parser = new InputParser();
        gameWorld = new GameWorld();
        gameWorld.create();
        parser.add_int_command("charge");
        parser.add_str_command("fire", new String[]{
                "holy", "evil"
        });
        parser.add_list_command("heal");
        parser.add_list_command("eat", new String[]{
                "apple", "pear"
        });
        var input = new CmdInput();
        input.set_parser(parser);
        while (true) {
            this.gameWorld.update();
            var command = input.next_command();

            if (command.isDefined()) {
                if (command.get().get_name().equals("exit")) {
                    break;
                }
                if (command.get().get_name().equals("charge")) {
                    parser.clear();
                    parser.add_int_command("target");
                    input.set_parser(parser);
                }
            } else {
                System.out.println("null");
            }
        }
    }

}
