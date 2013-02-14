/**
 * This class is generated by jOOQ
 */
package org.jooq.test.sqlite.generatedclasses.tables.records;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings("all")
public class TLanguageRecord extends org.jooq.impl.UpdatableRecordImpl<org.jooq.test.sqlite.generatedclasses.tables.records.TLanguageRecord> implements org.jooq.Record4<java.lang.String, java.lang.String, java.lang.String, java.lang.Integer> {

	private static final long serialVersionUID = -1644532888;

	/**
	 * Setter for <code>t_language.cd</code>. 
	 */
	public void setCd(java.lang.String value) {
		setValue(org.jooq.test.sqlite.generatedclasses.tables.TLanguage.CD, value);
	}

	/**
	 * Getter for <code>t_language.cd</code>. 
	 */
	public java.lang.String getCd() {
		return getValue(org.jooq.test.sqlite.generatedclasses.tables.TLanguage.CD);
	}

	/**
	 * Setter for <code>t_language.description</code>. 
	 */
	public void setDescription(java.lang.String value) {
		setValue(org.jooq.test.sqlite.generatedclasses.tables.TLanguage.DESCRIPTION, value);
	}

	/**
	 * Getter for <code>t_language.description</code>. 
	 */
	public java.lang.String getDescription() {
		return getValue(org.jooq.test.sqlite.generatedclasses.tables.TLanguage.DESCRIPTION);
	}

	/**
	 * Setter for <code>t_language.description_english</code>. 
	 */
	public void setDescriptionEnglish(java.lang.String value) {
		setValue(org.jooq.test.sqlite.generatedclasses.tables.TLanguage.DESCRIPTION_ENGLISH, value);
	}

	/**
	 * Getter for <code>t_language.description_english</code>. 
	 */
	public java.lang.String getDescriptionEnglish() {
		return getValue(org.jooq.test.sqlite.generatedclasses.tables.TLanguage.DESCRIPTION_ENGLISH);
	}

	/**
	 * Setter for <code>t_language.id</code>. 
	 */
	public void setId(java.lang.Integer value) {
		setValue(org.jooq.test.sqlite.generatedclasses.tables.TLanguage.ID, value);
	}

	/**
	 * Getter for <code>t_language.id</code>. 
	 */
	public java.lang.Integer getId() {
		return getValue(org.jooq.test.sqlite.generatedclasses.tables.TLanguage.ID);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Integer> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.String, java.lang.String, java.lang.String, java.lang.Integer> fieldsRow() {
		return org.jooq.impl.Factory.row(field1(), field2(), field3(), field4());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.String, java.lang.String, java.lang.String, java.lang.Integer> valuesRow() {
		return org.jooq.impl.Factory.row(value1(), value2(), value3(), value4());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field1() {
		return org.jooq.test.sqlite.generatedclasses.tables.TLanguage.CD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return org.jooq.test.sqlite.generatedclasses.tables.TLanguage.DESCRIPTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return org.jooq.test.sqlite.generatedclasses.tables.TLanguage.DESCRIPTION_ENGLISH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field4() {
		return org.jooq.test.sqlite.generatedclasses.tables.TLanguage.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value1() {
		return getCd();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value3() {
		return getDescriptionEnglish();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value4() {
		return getId();
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached TLanguageRecord
	 */
	public TLanguageRecord() {
		super(org.jooq.test.sqlite.generatedclasses.tables.TLanguage.T_LANGUAGE);
	}
}