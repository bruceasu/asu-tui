
package me.asu.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/** A low overhead, lightweight logging system.
 * @author Nathan Sweet <misc@n4te.com> */
public class Log {
	/** No logging at all. */
	public static final int LEVEL_NONE = 6;
	/** Critical errors. The application may no longer work correctly. */
	public static final int LEVEL_ERROR = 5;
	/** Important warnings. The application will continue to work correctly. */
	public static final int LEVEL_WARN = 4;
	/** Informative messages. Typically used for deployment. */
	public static final int LEVEL_INFO = 3;
	/** Debug messages. This level is useful during development. */
	public static final int LEVEL_DEBUG = 2;
	/** Trace messages. A lot of information is logged, so this level is usually only needed when debugging a problem. */
	public static final int LEVEL_TRACE = 1;

	/** The level of messages that will be logged. Compiling this and the booleans below as "final" will cause the compiler to
	 * remove all "if (Log.info) ..." type statements below the set level. */
	static private int level = LEVEL_INFO;

	/** True when the ERROR level will be logged. */
	public static boolean ERROR = level <= LEVEL_ERROR;
	/** True when the WARN level will be logged. */
	public static boolean WARN = level <= LEVEL_WARN;
	/** True when the INFO level will be logged. */
	public static boolean INFO = level <= LEVEL_INFO;
	/** True when the DEBUG level will be logged. */
	public static boolean DEBUG = level <= LEVEL_DEBUG;
	/** True when the TRACE level will be logged. */
	public static boolean TRACE = level <= LEVEL_TRACE;

	public static int get () {
		return level;
	}

	/** Sets the level to log. If a version of this class is being used that has a final log level, this has no affect. */
	public static void set (int level) {
		// Comment out method contents when compiling fixed level JARs.
		Log.level = level;
		ERROR = level <= LEVEL_ERROR;
		WARN = level <= LEVEL_WARN;
		INFO = level <= LEVEL_INFO;
		DEBUG = level <= LEVEL_DEBUG;
		TRACE = level <= LEVEL_TRACE;
	}
	public static void set (String strLevel) {
		// Comment out method contents when compiling fixed level JARs.
		if (strLevel == null) return;

		switch (strLevel) {
			case "ERROR": set(LEVEL_ERROR); break;
			case "WARN": set(LEVEL_WARN); break;
			case "INFO": set(LEVEL_INFO); break;
			case "DEBUG": set(LEVEL_DEBUG); break;
			case "TRACE": set(LEVEL_TRACE); break;
			default:
				error("Unknown level: " + strLevel);
				return;
		}
	}

	public static void NONE () {
		set(LEVEL_NONE);
	}

	public static void ERROR () {
		set(LEVEL_ERROR);
	}

	public static void WARN () {
		set(LEVEL_WARN);
	}

	public static void INFO () {
		set(LEVEL_INFO);
	}

	public static void DEBUG () {
		set(LEVEL_DEBUG);
	}

	public static void TRACE () {
		set(LEVEL_TRACE);
	}

	/** Sets the logger that will write the log messages. */
	public static void setLogger (Logger logger) {
		Log.logger = logger;
	}

	static private Logger logger = new Logger();

	public static void error (String message, Throwable ex) {if (ERROR) logger.log(LEVEL_ERROR, null, message, ex);}
	public static void error (String category, String message, Throwable ex) {if (ERROR) logger.log(LEVEL_ERROR, category, message, ex);}
	public static void error (String message) {if (ERROR) logger.log(LEVEL_ERROR, null, message, null);}
	public static void error (String category, String message) {if (ERROR) logger.log(LEVEL_ERROR, category, message, null);}
	public static void error (String fmt, Object... params) {if (ERROR) logger.log(LEVEL_ERROR, null, String.format(fmt, params), null);}

	public static void warn (String message, Throwable ex) {if (WARN) logger.log(LEVEL_WARN, null, message, ex);}
	public static void warn (String category, String message, Throwable ex) {if (WARN) logger.log(LEVEL_WARN, category, message, ex);}
	public static void warn (String message) {if (WARN) logger.log(LEVEL_WARN, null, message, null);}
	public static void warn (String category, String message) {if (WARN) logger.log(LEVEL_WARN, category, message, null);}
	public static void warn (String fmt, Object... params) {if (WARN) logger.log(LEVEL_WARN, null, String.format(fmt, params), null);}

	public static void info (String message, Throwable ex) {if (INFO) logger.log(LEVEL_INFO, null, message, ex);}
	public static void info (String category, String message, Throwable ex) {if (INFO) logger.log(LEVEL_INFO, category, message, ex);}
	public static void info (String message) {if (INFO) logger.log(LEVEL_INFO, null, message, null);}
	public static void info (String category, String message) {if (INFO) logger.log(LEVEL_INFO, category, message, null);}
	public static void info (String fmt, Object... params) {if (INFO) logger.log(LEVEL_INFO, null, String.format(fmt, params), null);}

	public static void debug (String message, Throwable ex) {if (DEBUG) logger.log(LEVEL_DEBUG, null, message, ex);}
	public static void debug (String category, String message, Throwable ex) {if (DEBUG) logger.log(LEVEL_DEBUG, category, message, ex);}
	public static void debug (String message) {if (DEBUG) logger.log(LEVEL_DEBUG, null, message, null);}
	public static void debug (String category, String message) {if (DEBUG) logger.log(LEVEL_DEBUG, category, message, null);}
	public static void debug (String fmt, Object... params) {if (DEBUG) logger.log(LEVEL_DEBUG, null, String.format(fmt, params), null);}

	public static void trace (String message, Throwable ex) {if (TRACE) logger.log(LEVEL_TRACE, null, message, ex);}
	public static void trace (String category, String message, Throwable ex) {if (TRACE) logger.log(LEVEL_TRACE, category, message, ex);}
	public static void trace (String message) {if (TRACE) logger.log(LEVEL_TRACE, null, message, null);}
	public static void trace (String category, String message) {if (TRACE) logger.log(LEVEL_TRACE, category, message, null);}
	public static void trace (String fmt, Object... params) {if (TRACE) logger.log(LEVEL_TRACE, null, String.format(fmt, params), null);}

	private Log() {}

	/** Performs the actual logging. Default implementation logs to System.out. Extended and use {@link Log#logger} set to handle
	 * logging differently. */
	public static class Logger {
		private final long firstLogTime = System.currentTimeMillis();
		private final NumberFormat formatter = new DecimalFormat("000");
		private final NumberFormat formatter2 = new DecimalFormat("00");
		public void log (int level, String category, String message, Throwable ex) {
			StringBuilder builder = new StringBuilder(256);

			long time = System.currentTimeMillis() - firstLogTime;
			long minutes = time / (1000 * 60);
			long seconds = time / (1000) % 60;
			long millis = time % 60;
			builder.append(formatter2.format(minutes));
			builder.append(':');
			builder.append(formatter2.format(seconds));
			builder.append('.').append(formatter.format(millis));
			switch (level) {
			case LEVEL_ERROR: builder.append(" ERROR: ");break;
			case LEVEL_WARN: builder.append("  WARN: ");break;
			case LEVEL_INFO: builder.append("  INFO: ");break;
			case LEVEL_DEBUG: builder.append(" DEBUG: ");break;
			case LEVEL_TRACE: builder.append(" TRACE: ");break;
			}

			if (category != null) {
				builder.append('[');
				builder.append(category);
				builder.append("] ");
			}

			builder.append(message);

			if (ex != null) {
				StringWriter writer = new StringWriter(256);
				ex.printStackTrace(new PrintWriter(writer));
				builder.append('\n');
				builder.append(writer.toString().trim());
			}

			print(builder.toString());
		}

		/** Prints the message to System.out. Called by the default implementation of {@link #log(int, String, String, Throwable)}. */
		protected void print (String message) {System.out.println(message);}
	}
}