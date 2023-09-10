package undertest.junit5
// spotless:off
import com.diffplug.selfie.expectSelfie
import kotlin.test.Test

// spotless:on

class UT_WithinMethodGCTest {
//    @Test fun selfie2() {
  @Test fun selfie() {
    expectSelfie("root").toMatchDisk()
    expectSelfie("maple").toMatchDisk("leaf")
  }
}