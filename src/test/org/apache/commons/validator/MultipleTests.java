/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/MultipleTests.java,v 1.1 2002/09/24 20:23:35 turner Exp $
 * $Revision: 1.1 $
 * $Date: 2002/09/24 20:23:35 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.commons.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.AssertionFailedError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogSource;

/**
 * <p>Performs Validation Test.</p>
 *
 * @author James Turner
 * @version $Revision: 1.1 $ $Date: 2002/09/24 20:23:35 $
*/
public class MultipleTests extends TestCase {

   /**
    * The key used to retrieve the set of validation
    * rules from the xml file.
   */
   protected static String FORM_KEY = "nameForm";

   /**
    * The key used to retrieve the validator action.
   */
   protected static String ACTION = "required";


   /**
    * Commons Logging instance.
   */
   private Log log = LogSource.getInstance(this.getClass().getName());

   /**
    * Resources used for validation tests.
   */
   private ValidatorResources resources = null;

   public MultipleTests(String name) {
       super(name);
   }

   /**
    * Start the tests.
    *
    * @param theArgs the arguments. Not used
    */
   public static void main(String[] theArgs) {
       junit.awtui.TestRunner.main(new String[] {MultipleTests.class.getName()});
   }

   /**
    * @return a test suite (<code>TestSuite</code>) that includes all methods
    *         starting with "test"
    */
   public static Test suite() {
       // All methods starting with "test" will be executed in the test suite.
       return new TestSuite(MultipleTests.class);
   }

   /**
    * Load <code>ValidatorResources</code> from
    * validator-name-required.xml.
   */
   protected void setUp() throws IOException {
      // Load resources
      InputStream in = null;
      resources = new ValidatorResources();

      try {
         in = this.getClass().getResourceAsStream("validator-multipletest.xml");
         ValidatorResourcesInitializer.initialize(resources, in);
      } catch (IOException e) {
         log.error(e.getMessage(), e);
         throw e;
      } finally {
         if (in != null) {
            try { in.close(); } catch (Exception e) {}
         }
      }
   }

   protected void tearDown() {
   }

   /**
    * With nothing provided, we should fail both because both are required.
   */
   public void testBothBlank() throws ValidatorException {
      // Create bean to run test on.
      NameBean name = new NameBean();

      // Construct validator based on the loaded resources
      // and the form key
      Validator validator = new Validator(resources, FORM_KEY);
      // add the name bean to the validator as a resource
      // for the validations to be performed on.
      validator.addResource(Validator.BEAN_KEY, name);

      // Get results of the validation.
      ValidatorResults results = null;

      // throws ValidatorException,
      // but we aren't catching for testing
      // since no validation methods we use
      // throw this
      results = validator.validate();

      assertNotNull("Results are null.", results);

      ValidatorResult firstNameResult = results.getValidatorResult("firstName");
      ValidatorResult lastNameResult = results.getValidatorResult("lastName");

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have failed.", !firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the '" + ACTION +"' action.", lastNameResult.containsAction(ACTION));
      assertTrue("Last Name ValidatorResult for the '" + ACTION +"' action should have failed.", !lastNameResult.isValid(ACTION));
      assertTrue("Last Name ValidatorResults should not contain the 'int' action.", !lastNameResult.containsAction("int"));
   }

   /**
    * If the first name fails required, and the second test fails int, we should get two errors.
   */
   public void testRequiredFirstNameBlankLastNameShort() throws ValidatorException {
      // Create bean to run test on.
      NameBean name = new NameBean();
      name.setFirstName("");
      name.setLastName("Test");

      // Construct validator based on the loaded resources
      // and the form key
      Validator validator = new Validator(resources, FORM_KEY);
      // add the name bean to the validator as a resource
      // for the validations to be performed on.
      validator.addResource(Validator.BEAN_KEY, name);

      // Get results of the validation.
      ValidatorResults results = null;

      results = validator.validate();

      assertNotNull("Results are null.", results);

      ValidatorResult firstNameResult = results.getValidatorResult("firstName");
      ValidatorResult lastNameResult = results.getValidatorResult("lastName");

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have failed.", !firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the 'int' action.", lastNameResult.containsAction("int"));
      assertTrue("Last Name ValidatorResult for the 'int' action should have failed.", !lastNameResult.isValid("int"));
   }

   /**
    * If the first name is there, and the last name fails int, we should get one error.
   */
   public void testRequiredLastNameShort() throws ValidatorException {
      // Create bean to run test on.
      NameBean name = new NameBean();
      name.setFirstName("Test");
      name.setLastName("Test");

      // Construct validator based on the loaded resources
      // and the form key
      Validator validator = new Validator(resources, FORM_KEY);
      // add the name bean to the validator as a resource
      // for the validations to be performed on.
      validator.addResource(Validator.BEAN_KEY, name);

      // Get results of the validation.
      ValidatorResults results = null;

      results = validator.validate();

      assertNotNull("Results are null.", results);

      ValidatorResult firstNameResult = results.getValidatorResult("firstName");
      ValidatorResult lastNameResult = results.getValidatorResult("lastName");

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have passed.", firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the 'int' action.", lastNameResult.containsAction("int"));
      assertTrue("Last Name ValidatorResult for the 'int' action should have failed.", !lastNameResult.isValid("int"));
   }

   /**
    * If first name is ok and last name is ok and is an int, no errors.
   */
   public void testRequiredLastNameLong() throws ValidatorException {
      // Create bean to run test on.
      NameBean name = new NameBean();
      name.setFirstName("Joe");
      name.setLastName("12345678");

      // Construct validator based on the loaded resources
      // and the form key
      Validator validator = new Validator(resources, FORM_KEY);
      // add the name bean to the validator as a resource
      // for the validations to be performed on.
      validator.addResource(Validator.BEAN_KEY, name);

      // Get results of the validation.
      ValidatorResults results = null;

      results = validator.validate();

      assertNotNull("Results are null.", results);

      ValidatorResult firstNameResult = results.getValidatorResult("firstName");
      ValidatorResult lastNameResult = results.getValidatorResult("lastName");

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have passed.", firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the 'int' action.", lastNameResult.containsAction("int"));
      assertTrue("Last Name ValidatorResult for the 'int' action should have passed.", lastNameResult.isValid("int"));
   }


}
