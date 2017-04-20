package gov.cms.qpp.conversion.model.error;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class AllErrorsTest {
	@Test
	public void addErrorSource() {
		AllErrors objectUnderTest = new AllErrors();

		assertThat("The error sources should have been null at first", objectUnderTest.getErrorSources(), is(nullValue()));

		objectUnderTest.addErrorSource(new ErrorSource());

		assertThat("The error sources should no longer be null", objectUnderTest.getErrorSources(), is(not(nullValue())));
		assertThat("The list should be one", objectUnderTest.getErrorSources(), hasSize(1));
	}
}