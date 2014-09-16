/**
 * 
 */
package edu.buffalo.cse.irf14.analysis.test;


import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.TokenFilterType;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

/**
 * @author nikhillo
 *
 */
public class AccentRuleTest extends TFRuleBaseTest {
	
	@Test
	public void testRule() {
		try {
			assertArrayEquals(new String[]{"The", "urban", "counterpart", "of", "chateau", "is", "palais"},
					runTest(TokenFilterType.ACCENT, "The urban counterpart of chÃ¢teau is palais"));
			assertArrayEquals(new String[]{"The", "expression", "hotel", "particulier", "is", "used", "for", "an", "urban", "'private", "house'"}, 
					runTest(TokenFilterType.ACCENT, "The expression hÃ´tel particulier is used for an urban 'private house'"));
			assertArrayEquals(new String[]{"Resumes", "can", "be", "used", "for", "a", "variety", "of", "reasons"}, 
					runTest(TokenFilterType.ACCENT, "RÃ©sumÃ©s can be used for a variety of reasons"));
			assertArrayEquals(new String[]{"naÑ€Ð°", "('steam/vapour')", "and", "napa", "('cent/penny,", "money')"},
					runTest(TokenFilterType.ACCENT, "nÐ°Ì€Ñ€Ð° ('steam/vapour') and nÐ°Ñ€Ð°Ì€ ('cent/penny, money')"));
			assertArrayEquals(new String[]{"for", "example", "vis-a-vis", "piece", "de", "resistance", "and", "creme", "brulee"}, 
					runTest(TokenFilterType.ACCENT, "for example vis-Ã -vis piÃ¨ce de rÃ©sistance and crÃ¨me brÃ»lÃ©e"));
			assertArrayEquals(new String[]{"Spanish", "pinguino", "French", "aigue", "or", "aigue"}, 
					runTest(TokenFilterType.ACCENT, "Spanish pingÃ¼ino French aiguÃ« or aigÃ¼e"));
			} catch (TokenizerException e) {
				fail("Exception thrown when not expected");
		}
	}

}
