package controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ValidatePhoneNumberTest.class, ValidateNameTest.class, ValidateAddressTest.class, ValidateRushTest.class})
public class AllTests {

}
