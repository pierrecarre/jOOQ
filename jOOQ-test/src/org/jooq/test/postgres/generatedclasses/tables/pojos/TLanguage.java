/**
 * This class is generated by jOOQ
 */
package org.jooq.test.postgres.generatedclasses.tables.pojos;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TLanguage implements org.jooq.test.postgres.generatedclasses.tables.interfaces.ITLanguage {

	private static final long serialVersionUID = 1580552657;

	private final java.lang.String  cd;
	private final java.lang.String  description;
	private final java.lang.String  descriptionEnglish;
	private final java.lang.Integer id;

	public TLanguage(
		java.lang.String  cd,
		java.lang.String  description,
		java.lang.String  descriptionEnglish,
		java.lang.Integer id
	) {
		this.cd = cd;
		this.description = description;
		this.descriptionEnglish = descriptionEnglish;
		this.id = id;
	}

	@Override
	public java.lang.String getCd() {
		return this.cd;
	}

	@Override
	public java.lang.String getDescription() {
		return this.description;
	}

	@Override
	public java.lang.String getDescriptionEnglish() {
		return this.descriptionEnglish;
	}

	@Override
	public java.lang.Integer getId() {
		return this.id;
	}
}
