package hohserg.advancedauromancy.hooklib.asm;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface HookLogger {

    void debug(String message);

    void warning(String message);

    void severe(String message);

    void severe(String message, Throwable cause);

    class SystemOutLogger implements HookLogger {


        public void debug(String message) {
            System.out.println("[DEBUG] " + message);
        }


        public void warning(String message) {
            System.out.println("[WARNING] " + message);
        }


        public void severe(String message) {
            System.out.println("[SEVERE] " + message);
        }


        public void severe(String message, Throwable cause) {
            severe(message);
            cause.printStackTrace();
        }
    }

    class VanillaLogger implements HookLogger {

        private Logger logger;

        public VanillaLogger(Logger logger) {
            this.logger = logger;
        }


        public void debug(String message) {
            logger.fine(message);
        }


        public void warning(String message) {
            logger.warning(message);
        }


        public void severe(String message) {
            logger.severe(message);
        }


        public void severe(String message, Throwable cause) {
            logger.log(Level.SEVERE, message, cause);
        }
    }

}
