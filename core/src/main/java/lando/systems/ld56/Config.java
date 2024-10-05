package lando.systems.ld56;

public class Config {

    public static final String window_title = "LudumDare56";

    public static class Debug {
        public static boolean general = false;
        public static boolean render = true;
        public static boolean ui = false;
        public static boolean logging = false;
        public static boolean frame_by_frame = false;
        public static boolean show_launch_screen = false;
        public static boolean start_on_game_screen = true;

        public static boolean shouldShowDebugUi() {
            return general || render || ui || logging || frame_by_frame;
        }
    }

    public static class Screen {
        public static final int window_width = 1280;
        public static final int window_height = 720;
        public static final int framebuffer_width = window_width;
        public static final int framebuffer_height = window_height;
    }
}
