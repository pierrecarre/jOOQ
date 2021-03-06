/**
 * This class is generated by jOOQ
 */
package org.jooq.util.mysql.mysql;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.1.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Mysql extends org.jooq.impl.SchemaImpl {

	private static final long serialVersionUID = -490480462;

	/**
	 * The singleton instance of <code>mysql</code>
	 */
	public static final Mysql MYSQL = new Mysql();

	/**
	 * No further instances allowed
	 */
	private Mysql() {
		super("mysql");
	}

	@Override
	public final java.util.List<org.jooq.Table<?>> getTables() {
		java.util.List result = new java.util.ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final java.util.List<org.jooq.Table<?>> getTables0() {
		return java.util.Arrays.<org.jooq.Table<?>>asList(
			org.jooq.util.mysql.mysql.tables.Proc.PROC,
			org.jooq.util.mysql.mysql.tables.ProcsPriv.PROCS_PRIV);
	}
}
