package npe.cmdLine;

import npe.tools.ArrayTool;
import npe.tools.MapForJava;

import java.util.*;


public class GameWorld {
    com.artemis.World engine;
    CmdCanvas cmdCanvas;
    ArrayList<String> deck;
    String[] current_hand;
    CmdTextBlock last_played_block;

    public GameWorld() {
        init();
    }

    private void init() {
        cmdCanvas = new CmdCanvas(128, 10);
    }

    private void initialize_engine() {

    }

    public void generateDeck() {
        this.deck = new ArrayList<>();
        String[] suits = {"♠", "♥", "♦", "♣"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(rank + suit);
            }
        }
        this.current_hand = this.drawHand(8);
    }

    public void create() {
        this.generateDeck();

        this.last_played_block = new CmdTextBlock(36, 6);
        this.last_played_block.update_property("last_played", "None");
        this.last_played_block.update_color("last_played", new AnsiWrapper(AnsiWrapper.BG_CYAN()), CmdTextBlock.VALUE_TAR());
        this.cmdCanvas.add_renderer(this.last_played_block);


    }

    public void update() {
        cmdCanvas.render();
    }

    public String check_hand_type(String[] played_cards) {
        Map<Character, Integer> rankCounts = new HashMap<>();
        Set<Character> suits = new HashSet<>();
        List<Integer> ranks = new ArrayList<>();

        // 解析每张牌的数字和花色
        for (String card : played_cards) {
            char rank = card.charAt(0);
            char suit = card.charAt(1);
            int rank_num;
            if (rank == '1' && suit == '0') {
                rank_num = 10;
                suit = card.charAt(2);
            } else {
                rank_num = parseRank(rank);
            }
            ranks.add(rank_num);
            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            suits.add(suit);

        }

        // 判断是否为同花
        boolean isFlush = suits.size() == 1;

        // 判断是否为顺子
        Collections.sort(ranks);
        boolean isStraight = true;
        for (int i = 1; i < ranks.size(); i++) {
            if (ranks.get(i) != ranks.get(i - 1) + 1) {
                isStraight = false;
                break;
            }
        }

        // 判断是否有一对、两对、三条或四条
        int pairCount = 0;
        int threeOfAKindCount = 0;
        for (int count : rankCounts.values()) {
            if (count == 2) {
                pairCount++;
            } else if (count == 3) {
                threeOfAKindCount++;
            }
        }

        // 根据同花、顺子和对子数量确定牌型
        if (isFlush && isStraight) {
            return "Straight Flush ";
        } else if (isFlush) {
            return "Flush ";
        } else if (isStraight) {
            return "Straight ";
        } else if (threeOfAKindCount == 1 && pairCount == 1) {
            return "Full House";
        } else if (rankCounts.containsValue(4)) {
            return "Four of a Kind";
        } else if (threeOfAKindCount == 1) {
            return "Three of a Kind ";
        } else if (pairCount == 1) {
            return "Pair ";
        } else if (pairCount == 2) {
            return "Two Pair ";
        } else {
            return "High Card";
        }
    }

    public int parseRank(char rank) {
        if (Character.isDigit(rank)) {
            return rank - '0';
        } else {
            return switch (rank) {
                case 'J' -> 11;
                case 'Q' -> 12;
                case 'K' -> 13;
                case 'A' -> 14;
                default -> -1; // 错误情况
            };
        }
    }

    /**
     * 对于输入的command进行处理
     *
     * @param command 要处理的命令
     */
    public void handle_command(CommandData command) {


        if (command.get_type().equals(CommandData.LIST_TYPE())) {
            boolean valid_played = true;

            String[] input_arr = ArrayTool.get_str_arr(command.get_list());
            Set<String> input_set = new HashSet<>(List.of(input_arr));
            // 判断是否有重复牌或者牌大于5张
            if (input_set.size() < input_arr.length || input_set.size() > 5) {
                valid_played = false;
            }
            // 判断是否有出现不在当前手的牌
            for (String s : input_arr) {
                if (!valid_played) {
                    break;
                }
                int index = Arrays.asList(this.current_hand).indexOf(s);
                if (index < 0) {
                    valid_played = false;
                    break;
                }
            }

            // 成功打牌才去抽新的
            if (valid_played) {
                StringBuilder last_played = new StringBuilder();
                for (String s : input_arr) {
                    last_played.append(s).append(" ");
                }
                this.last_played_block.update_property("last played hand", check_hand_type(input_arr));
                this.last_played_block.update_color("last played hand", new AnsiWrapper(AnsiWrapper.BG_MAGENTA()), CmdTextBlock.VALUE_TAR());
                this.last_played_block.update_property("last_played", last_played.toString());
                this.current_hand = this.drawHand(8);
            }
        }

    }

    /**
     * 更新parser（加入对应的内容）
     *
     * @param parser parser
     */
    public void update_parser(InputParser parser) {
        parser.clear();
        parser.add_unique_list_command("play", this.current_hand);
    }

    /**
     * 从牌堆中抽取指定数量的牌
     *
     * @param numCards 抽卡数
     * @return 抽出来的卡
     */
    public String[] drawHand(int numCards) {
        String[] hand = new String[numCards];
        Random random = new Random();
        for (int i = 0; i < Math.min(numCards, deck.size()); i++) {
            int index = random.nextInt(deck.size());
            String card = deck.remove(index);
            hand[i] = card;
        }
        return hand;
    }
}
