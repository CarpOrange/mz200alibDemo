package com.haoxueche.winterlog.timber;

import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.haoxueche.winterlog.BuildConfig;
import com.haoxueche.winterlog.L;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.unmodifiableList;


/**
 * Logging for lazy people.
 */
@SuppressWarnings({"WeakerAccess", "unused"}) // Public API.
public final class Timber {
    /**
     * Log a verbose message with optional format args.
     */
    public static void v(boolean persistence, String message, Object... args) {
        TREE_OF_SOULS.v(persistence, message, args);
    }

    /**
     * Log a verbose exception and a message with optional format args.
     */
    public static void v(boolean persistence, Throwable t, String message, Object... args) {
        TREE_OF_SOULS.v(persistence, t, message, args);
    }

    /**
     * Log a verbose exception.
     */
    public static void v(boolean persistence, Throwable t) {
        TREE_OF_SOULS.v(persistence, t);
    }

    /**
     * Log a debug message with optional format args.
     */
    public static void d(boolean persistence, String message, Object... args) {
        TREE_OF_SOULS.d(persistence, message, args);
    }

    /**
     * Log a debug exception and a message with optional format args.
     */
    public static void d(boolean persistence, Throwable t, String message, Object... args) {
        TREE_OF_SOULS.d(persistence, t, message, args);
    }

    /**
     * Log a debug exception.
     */
    public static void d(boolean persistence, Throwable t) {
        TREE_OF_SOULS.d(persistence, t);
    }

    /**
     * Log an info message with optional format args.
     */
    public static void i(boolean persistence, String message, Object... args) {
        TREE_OF_SOULS.i(persistence, message, args);
    }

    /**
     * Log an info exception and a message with optional format args.
     */
    public static void i(boolean persistence, Throwable t, String message, Object... args) {
        TREE_OF_SOULS.i(persistence, t, message, args);
    }

    /**
     * Log an info exception.
     */
    public static void i(boolean persistence, Throwable t) {
        TREE_OF_SOULS.i(persistence, t);
    }

    /**
     * Log a warning message with optional format args.
     */
    public static void w(boolean persistence, String message, Object... args) {
        TREE_OF_SOULS.w(persistence, message, args);
    }

    /**
     * Log a warning exception and a message with optional format args.
     */
    public static void w(boolean persistence, Throwable t, String message, Object... args) {
        TREE_OF_SOULS.w(persistence, t, message, args);
    }

    /**
     * Log a warning exception.
     */
    public static void w(boolean persistence, Throwable t) {
        TREE_OF_SOULS.w(persistence, t);
    }

    /**
     * Log an error message with optional format args.
     */
    public static void e(boolean persistence, String message, Object... args) {
        TREE_OF_SOULS.e(persistence, message, args);
    }

    /**
     * Log an error exception and a message with optional format args.
     */
    public static void e(boolean persistence, Throwable t, String message, Object... args) {
        TREE_OF_SOULS.e(persistence, t, message, args);
    }

    /**
     * Log an error exception.
     */
    public static void e(boolean persistence, Throwable t) {
        TREE_OF_SOULS.e(persistence, t);
    }

    /**
     * Log an assert message with optional format args.
     */
    public static void wtf(boolean persistence, String message, Object... args) {
        TREE_OF_SOULS.wtf(persistence, message, args);
    }

    /**
     * Log an assert exception and a message with optional format args.
     */
    public static void wtf(boolean persistence, Throwable t, String message, Object... args) {
        TREE_OF_SOULS.wtf(persistence, t, message, args);
    }

    /**
     * Log an assert exception.
     */
    public static void wtf(boolean persistence, Throwable t) {
        TREE_OF_SOULS.wtf(persistence, t);
    }

    /**
     * Log at {@code priority} a message with optional format args.
     */
    public static void log(boolean persistence, int priority, String message, Object... args) {
        TREE_OF_SOULS.log(persistence, priority, message, args);
    }

    /**
     * Log at {@code priority} an exception and a message with optional format args.
     */
    public static void log(boolean persistence, int priority, Throwable t, String message,
                           Object... args) {
        TREE_OF_SOULS.log(persistence, priority, t, message, args);
    }

    /**
     * Log at {@code priority} an exception.
     */
    public static void log(boolean persistence, int priority, Throwable t) {
        TREE_OF_SOULS.log(persistence, priority, t);
    }

    /**
     * A view into Timber's planted trees as a tree itself. This can be used for injecting a logger
     * instance rather than using static methods or to facilitate testing.
     */

    public static Tree asTree() {
        return TREE_OF_SOULS;
    }

    /**
     * Set a one-time tag for use on the next logging call.
     */

    public static Tree tag(String tag) {
        Tree[] forest = forestAsArray;
        for (Tree tree : forest) {
            tree.explicitTag.set(tag);
        }
        return TREE_OF_SOULS;
    }

    /**
     * Add a new logging tree.
     */
    @SuppressWarnings("ConstantConditions") // Validating public API contract.
    public static void plant(Tree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        if (tree == TREE_OF_SOULS) {
            throw new IllegalArgumentException("Cannot plant Timber into itself.");
        }
        synchronized (FOREST) {
            FOREST.add(tree);
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
    }

    /**
     * Adds new logging trees.
     */
    @SuppressWarnings("ConstantConditions") // Validating public API contract.
    public static void plant(Tree... trees) {
        if (trees == null) {
            throw new NullPointerException("trees == null");
        }
        for (Tree tree : trees) {
            if (tree == null) {
                throw new NullPointerException("trees contains null");
            }
            if (tree == TREE_OF_SOULS) {
                throw new IllegalArgumentException("Cannot plant Timber into itself.");
            }
        }
        synchronized (FOREST) {
            Collections.addAll(FOREST, trees);
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
    }

    /**
     * Remove a planted tree.
     */
    public static void uproot(Tree tree) {
        synchronized (FOREST) {
            if (!FOREST.remove(tree)) {
                throw new IllegalArgumentException("Cannot uproot tree which is not planted: " +
                        tree);
            }
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
    }

    /**
     * Remove all planted trees.
     */
    public static void uprootAll() {
        synchronized (FOREST) {
            FOREST.clear();
            forestAsArray = TREE_ARRAY_EMPTY;
        }
    }

    /**
     * Return a copy of all planted {@linkplain Tree trees}.
     */

    public static List<Tree> forest() {
        synchronized (FOREST) {
            return unmodifiableList(new ArrayList<>(FOREST));
        }
    }

    public static int treeCount() {
        synchronized (FOREST) {
            return FOREST.size();
        }
    }

    private static final Tree[] TREE_ARRAY_EMPTY = new Tree[0];
    // Both fields guarded by 'FOREST'.
    private static final List<Tree> FOREST = new ArrayList<>();
    static volatile Tree[] forestAsArray = TREE_ARRAY_EMPTY;

    /**
     * A {@link Tree} that delegates to all planted trees in the {@linkplain #FOREST forest}.
     */
    private static final Tree TREE_OF_SOULS = new Tree() {
        @Override
        public void v(boolean persistence, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.v(persistence, message, args);
            }
        }

        @Override
        public void v(boolean persistence, Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.v(persistence, t, message, args);
            }
        }

        @Override
        public void v(boolean persistence, Throwable t) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.v(persistence, t);
            }
        }

        @Override
        public void d(boolean persistence, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.d(persistence, message, args);
            }
        }

        @Override
        public void d(boolean persistence, Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.d(persistence, t, message, args);
            }
        }

        @Override
        public void d(boolean persistence, Throwable t) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.d(persistence, t);
            }
        }

        @Override
        public void i(boolean persistence, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.i(persistence, message, args);
            }
        }

        @Override
        public void i(boolean persistence, Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.i(persistence, t, message, args);
            }
        }

        @Override
        public void i(boolean persistence, Throwable t) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.i(persistence, t);
            }
        }

        @Override
        public void w(boolean persistence, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.w(persistence, message, args);
            }
        }

        @Override
        public void w(boolean persistence, Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.w(persistence, t, message, args);
            }
        }

        @Override
        public void w(boolean persistence, Throwable t) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.w(persistence, t);
            }
        }

        @Override
        public void e(boolean persistence, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.e(persistence, message, args);
            }
        }

        @Override
        public void e(boolean persistence, Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.e(persistence, t, message, args);
            }
        }

        @Override
        public void e(boolean persistence, Throwable t) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.e(persistence, t);
            }
        }

        @Override
        public void wtf(boolean persistence, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.wtf(persistence, message, args);
            }
        }

        @Override
        public void wtf(boolean persistence, Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.wtf(persistence, t, message, args);
            }
        }

        @Override
        public void wtf(boolean persistence, Throwable t) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.wtf(persistence, t);
            }
        }

        @Override
        public void log(boolean persistence, int priority, String message, Object... args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.log(persistence, priority, message, args);
            }
        }

        @Override
        public void log(boolean persistence, int priority, Throwable t, String message, Object...
                args) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.log(persistence, priority, t, message, args);
            }
        }

        @Override
        public void log(boolean persistence, int priority, Throwable t) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.log(persistence, priority, t);
            }
        }

        @Override
        protected void log(boolean persistence, int priority, String tag, String message,
                           Throwable t) {
            throw new AssertionError("Missing override for log method.");
        }
    };

    private Timber() {
        throw new AssertionError("No instances.");
    }

    /**
     * A facade for handling logging calls. Install instances via {@link #plant Timber.plant()}.
     */
    public static abstract class Tree {
        final ThreadLocal<String> explicitTag = new ThreadLocal<>();


        String getTag() {
            String tag = explicitTag.get();
            if (tag != null) {
                explicitTag.remove();
            }
            return tag;
        }

        /**
         * Log a verbose message with optional format args.
         */
        public void v(boolean persistence, String message, Object... args) {
            prepareLog(persistence, Log.VERBOSE, null, message, args);
        }

        /**
         * Log a verbose exception and a message with optional format args.
         */
        public void v(boolean persistence, Throwable t, String message, Object... args) {
            prepareLog(persistence, Log.VERBOSE, t, message, args);
        }

        /**
         * Log a verbose exception.
         */
        public void v(boolean persistence, Throwable t) {
            prepareLog(persistence, Log.VERBOSE, t, null);
        }

        /**
         * Log a debug message with optional format args.
         */
        public void d(boolean persistence, String message, Object... args) {
            prepareLog(persistence, Log.DEBUG, null, message, args);
        }

        /**
         * Log a debug exception and a message with optional format args.
         */
        public void d(boolean persistence, Throwable t, String message, Object... args) {
            prepareLog(persistence, Log.DEBUG, t, message, args);
        }

        /**
         * Log a debug exception.
         */
        public void d(boolean persistence, Throwable t) {
            prepareLog(persistence, Log.DEBUG, t, null);
        }

        /**
         * Log an info message with optional format args.
         */
        public void i(boolean persistence, String message, Object... args) {
            prepareLog(persistence, Log.INFO, null, message, args);
        }

        /**
         * Log an info exception and a message with optional format args.
         */
        public void i(boolean persistence, Throwable t, String message, Object... args) {
            prepareLog(persistence, Log.INFO, t, message, args);
        }

        /**
         * Log an info exception.
         */
        public void i(boolean persistence, Throwable t) {
            prepareLog(persistence, Log.INFO, t, null);
        }

        /**
         * Log a warning message with optional format args.
         */
        public void w(boolean persistence, String message, Object... args) {
            prepareLog(persistence, Log.WARN, null, message, args);
        }

        /**
         * Log a warning exception and a message with optional format args.
         */
        public void w(boolean persistence, Throwable t, String message, Object... args) {
            prepareLog(persistence, Log.WARN, t, message, args);
        }

        /**
         * Log a warning exception.
         */
        public void w(boolean persistence, Throwable t) {
            prepareLog(persistence, Log.WARN, t, null);
        }

        /**
         * Log an error message with optional format args.
         */
        public void e(boolean persistence, String message, Object... args) {
            prepareLog(persistence, Log.ERROR, null, message, args);
        }

        /**
         * Log an error exception and a message with optional format args.
         */
        public void e(boolean persistence, Throwable t, String message, Object... args) {
            prepareLog(persistence, Log.ERROR, t, message, args);
        }

        /**
         * Log an error exception.
         */
        public void e(boolean persistence, Throwable t) {
            prepareLog(persistence, Log.ERROR, t, null);
        }

        /**
         * Log an assert message with optional format args.
         */
        public void wtf(boolean persistence, String message, Object... args) {
            prepareLog(persistence, Log.ASSERT, null, message, args);
        }

        /**
         * Log an assert exception and a message with optional format args.
         */
        public void wtf(boolean persistence, Throwable t, String message, Object... args) {
            prepareLog(persistence, Log.ASSERT, t, message, args);
        }

        /**
         * Log an assert exception.
         */
        public void wtf(boolean persistence, Throwable t) {
            prepareLog(persistence, Log.ASSERT, t, null);
        }

        /**
         * Log at {@code priority} a message with optional format args.
         */
        public void log(boolean persistence, int priority, String message, Object... args) {
            prepareLog(persistence, priority, null, message, args);
        }

        /**
         * Log at {@code priority} an exception and a message with optional format args.
         */
        public void log(boolean persistence, int priority, Throwable t, String message, Object...
                args) {
            prepareLog(persistence, priority, t, message, args);
        }

        /**
         * Log at {@code priority} an exception.
         */
        public void log(boolean persistence, int priority, Throwable t) {
            prepareLog(persistence, priority, t, null);
        }

        /**
         * Return whether a message at {@code priority} should be logged.
         *
         * @deprecated use {@link #isLoggable(String, int)} instead.
         */
        @Deprecated
        protected boolean isLoggable(int priority) {
            return true;
        }

        /**
         * Return whether a message at {@code priority} or {@code tag} should be logged.
         */
        protected boolean isLoggable(String tag, int priority) {
            //noinspection deprecation
            return isLoggable(priority);
        }

        private void prepareLog(boolean persistence, int priority, Throwable t, String message,
                                Object... args) {
            // Consume tag even when message is not loggable so that next message is correctly
            // tagged.
            String tag = getTag();

            if (!isLoggable(tag, priority)) {
                return;
            }
            if (message != null && message.length() == 0) {
                message = null;
            }
            if (message == null) {
                if (t == null) {
                    return; // Swallow message if it's null and there's no throwable.
                }
                message = getStackTraceString(t);
            } else {
                if (args != null && args.length > 0) {
                    message = formatMessage(message, args);
                }
                if (t != null) {
                    message += "\n" + getStackTraceString(t);
                }
            }

            log(persistence, priority, tag, message, t);
        }

        /**
         * Formats a log message with optional arguments.
         */
        protected String formatMessage(String message, Object[] args) {
            return String.format(message, args);
        }

        private String getStackTraceString(Throwable t) {
            // Don't replace this with Log.getStackTraceString() - it hides
            // UnknownHostException, which is not what we want.
            StringWriter sw = new StringWriter(256);
            PrintWriter pw = new PrintWriter(sw, false);
            t.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        }

        /**
         * Write a log message to its destination. Called for all level-specific methods by default.
         *
         * @param priority Log level. See {@link Log} for constants.
         * @param tag      Explicit or inferred tag. May be {@code null}.
         * @param message  Formatted log message. May be {@code null}, but then {@code t} will
         *                 not be.
         * @param t        Accompanying exceptions. May be {@code null}, but then {@code message}
         *                 will not be.
         */
        protected abstract void log(boolean persistence, int priority, String tag, String message,
                                    Throwable t);
    }

    /**
     * A {@link Tree Tree} for debug builds. Automatically infers the tag from the calling class.
     */
    public static class DebugTree extends Tree {
        private static final int MAX_LOG_LENGTH = 4000;
        private static final int MAX_TAG_LENGTH = 23;
        private static final int MIN_STACK_OFFSET = 6;
        private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
        private static final String MESSAGE_FORMAT = "%s  [threadName:%s] [threadId:%d][isMain:%s]";
        public static final String TAG_FORMAT = "%s %s() %d";
        public static final String TAG_FORMAT1 = "%d %s";

        /**
         * Extract the tag which should be used for the message from the {@code element}. By default
         * this will use the class name without any anonymous class suffixes (e.g., {@code Foo$1}
         * becomes {@code Foo}).
         * <p>
         * Note: This will not be called if a {@linkplain #tag(String) manual tag} was specified.
         */

        protected String createStackElementTag(StackTraceElement element) {
            String tag = element.getClassName();
            Matcher m = ANONYMOUS_CLASS.matcher(tag);
            if (m.find()) {
                tag = m.replaceAll("");
            }
            tag = tag.substring(tag.lastIndexOf('.') + 1);
            if(BuildConfig.DEBUG) {
                tag = String.format(TAG_FORMAT, tag, element.getMethodName(), element.getLineNumber());
            } else {
                tag = String.format(TAG_FORMAT1, element.getLineNumber(), tag);
            }
            // Tag length limit was removed in API 24.
            if (BuildConfig.DEBUG || tag.length() <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return tag;
            }
            return tag.substring(0, MAX_TAG_LENGTH);
        }


        @Override
        final String getTag() {
            String tag = super.getTag();
            if (tag != null) {
                return tag;
            }

            // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
            // because Robolectric runs them on the JVM but on Android the elements are different.
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            if (stackTrace.length <= MIN_STACK_OFFSET) {
                throw new IllegalStateException(
                        "Synthetic stacktrace didn't have enough elements: are you using " +
                                "proguard?");
            }
            int stackOffset = MIN_STACK_OFFSET;
            for (int i = MIN_STACK_OFFSET; i < stackTrace.length; i++) {
                StackTraceElement e = stackTrace[i];
                String name = e.getClassName();
                if (!name.equals(Timber.class.getName()) && !name.equals(L.class.getName())) {
                    stackOffset = i;
                    break;
                }
            }
            return createStackElementTag(stackTrace[stackOffset]);
        }

        /**
         * Break up {@code message} into maximum-length chunks (if needed) and send to either
         * {@link Log#println(int, String, String) Log.println()} or
         * {@link Log#wtf(String, String) Log.wtf()} for logging.
         * <p>
         * {@inheritDoc}
         */
        @Override
        protected void log(boolean persistence, int priority, String tag, String message,
                           Throwable throwable) {
            String threadName = Thread.currentThread().getName();
            long threadId = Thread.currentThread().getId();
            boolean isMain = false;
            if (Looper.getMainLooper() == Looper.myLooper()) {
                isMain = true;
            }
            message = String.format(MESSAGE_FORMAT, message, threadName,
                    threadId, isMain);
            if (message.length() < MAX_LOG_LENGTH) {
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, message);
                } else {
                    Log.println(priority, tag, message);
                }
                return;
            }

            // Split by line, then ensure each line can fit into Log's maximum length.
            for (int i = 0, length = message.length(); i < length; i++) {
                int newline = message.indexOf('\n', i);
                newline = newline != -1 ? newline : length;
                do {
                    int end = Math.min(newline, i + MAX_LOG_LENGTH);
                    String part = message.substring(i, end);
                    if (priority == Log.ASSERT) {
                        Log.wtf(tag, part);
                    } else {
                        Log.println(priority, tag, part);
                    }
                    i = end;
                } while (i < newline);
            }
        }

        private int getStackOffset(StackTraceElement[] trace) {

            for (int i = 5; i < trace.length; i++) {
                StackTraceElement e = trace[i];
                String name = e.getClassName();
                if (!name.equals(L.class.getName()) && !name.equals(Timber.class.getName())) {
                    return --i;
                }
            }
            return -1;
        }

    }
}
