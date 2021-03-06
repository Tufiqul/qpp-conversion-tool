package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertWithMessage;

public class AciMeasurePerformedRnRValidatorTest {

	private AciMeasurePerformedRnRValidator validator;
	private Node aciMeasurePerformedRnRNode;

	@Before
	public void init() {
		validator = new AciMeasurePerformedRnRValidator();

		aciMeasurePerformedRnRNode = new Node(TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS);
		aciMeasurePerformedRnRNode.putValue("measureId", "ACI_INFBLO_1");

		Node measurePerformed = new Node(TemplateId.MEASURE_PERFORMED);
		aciMeasurePerformedRnRNode.addChildNode(measurePerformed);
	}

	@Test
	public void testValidateGoodData() throws Exception {
		Set<Detail> errors = run();
		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	public void testWithNoMeasureId() throws Exception {
		aciMeasurePerformedRnRNode.removeValue("measureId");
		Set<Detail> errors = run();
		assertWithMessage("Should result in a MEASURE_ID_IS_REQUIRED error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_MEASURE_PERFORMED_RNR_MEASURE_ID_NOT_SINGULAR);
	}

	@Test
	public void testWithNoChildren() throws Exception {
		aciMeasurePerformedRnRNode.getChildNodes().clear();
		Set<Detail> errors = run();
		assertWithMessage("Validation error size should be 1")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_MISSING);
	}

	private Set<Detail> run() {
		return validator.validateSingleNode(aciMeasurePerformedRnRNode);
	}

}