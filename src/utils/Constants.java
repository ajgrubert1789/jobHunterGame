package utils;

import main.Game;

public class Constants {


    public static class UI {
        public static class Buttons {
            public static final int B_WIDTH = 143;   // bigger
            public static final int B_HEIGHT = 75;   // bigger

            public static final int B_WIDTH_DEFAULT = 140;   // sprite slice size
            public static final int B_HEIGHT_DEFAULT = 56;

        }
    }

    // ------------------------------------------------------------
    // PLAYER CONSTANTS
    // ------------------------------------------------------------
    public static class PlayerConstants {

        public static final int IDLE   = 2;
        public static final int RUN    = 1;
        public static final int ATTACK = 0;
        public static final int JUMP   = 3;
        public static final int CLIMB  = 5;
        public static final int IDLE2 = 6;
        public static final int VICTORY = 7;

        public static int getSpriteAmount(int action) {
            return switch (action) {
                case IDLE, RUN, ATTACK, JUMP, IDLE2, VICTORY -> 24;
                case CLIMB -> 13;
                default -> 1;
            };
        }
    }
    /*
        Item Constants
     */
    public static class itemConstants{


        //Item Types
        public static final int FLOPPY_DISK = 0;


        //iTEM ANIMATION STATES
        public static final int IDLE = 0;
        public static final int COLLECTED = 1;

        //Sprite Atlas frame size
        public static final int FLOPPY_DISK_WIDTH_DEFAULT = 256;
        public static final int FLOPPY_DISK_HEIGHT_DEFAULT = 256;

        // In‑game draw size (Now dynamically scaling)
        public static final int FLOPPY_DISK_WIDTH  = (int) (64 * main.Game.SCALE);
        public static final int FLOPPY_DISK_HEIGHT = (int) (64 * main.Game.SCALE);

        public static int getSpriteAmount(int itemType, int itemState){
            if(itemType == FLOPPY_DISK){
                return switch (itemState) {
                    case IDLE, COLLECTED -> 25;
                    default -> 1;
                };
            }
            return 1;
        }

    }

    // ------------------------------------------------------------
    // ENEMY CONSTANTS
    // ------------------------------------------------------------
    public static class EnemyConstants {



        // Enemy types
        public static final int ROBOT = 0;
        public static final int DAVE = 1;

        // Robot animation states
        public static final int EXPLODE = 0;
        public static final int IDLE    = 1;
        public static final int TALK    = 2;
        public static final int WALK    = 3;

        //Dave animation states
        public static final int EXPLODE_DAVE = 0;
        public static final int IDLE_DAVE = 1;
        public static final int POINT_DAVE = 2;
        public static final int SITS_DAVE = 3;
        public static final int WALK_DAVE = 4;
        public static final int WAVE_DAVE = 5;
        public static final int COFFEE_TIME = 6;
        public static final int VICTORY_DAVE = 7;
        public static final int TALKING_DAVE = 8;


        // Sprite atlas frame size
        public static final int ROBOT_WIDTH_DEFAULT  = 256;
        public static final int ROBOT_HEIGHT_DEFAULT = 256;

        // DAVE Sprite atlas frame size
        public static final int DAVE_WIDTH_DEFAULT  = 256;
        public static final int DAVE_HEIGHT_DEFAULT = 256;

        // In‑game draw size (Now dynamically scaling)
        public static final int ROBOT_WIDTH  = (int) (64 * main.Game.SCALE);
        public static final int ROBOT_HEIGHT = (int) (64 * main.Game.SCALE);

        public static final int DAVE_WIDTH   = (int) (128 * main.Game.SCALE);
        public static final int DAVE_HEIGHT  = (int) (128 * main.Game.SCALE);

        public static int getSpriteAmount(int enemyType, int enemyState) {
            if (enemyType == ROBOT) {
                return switch (enemyState) {
                    case EXPLODE, IDLE, TALK, WALK -> 10;
                    default -> 1;
                };
            }

            if (enemyType == DAVE) {
                return switch (enemyState) {
                    case EXPLODE_DAVE, IDLE_DAVE, POINT_DAVE, SITS_DAVE, WALK_DAVE, WAVE_DAVE, VICTORY_DAVE, COFFEE_TIME,TALKING_DAVE -> 25;
                    default -> 1;
                };
            }
            return 1;
        }
    }
}
