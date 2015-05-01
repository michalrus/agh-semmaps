package agh.semmaps

import java.io.File

final class ConfigSpec extends UnitSpec {

  "Config" should {
    "correctly parse --input and --output" in {
      Config(List("--input", "file.xml", "--output", "file.pl")) shouldEqual
        Some(Config(new File("file.xml"), new File("file.pl"), owl = false))
    }
    "fail on additional args" in {
      Config(List("--input", "file.xml", "--output", "file.pl", "additional")) shouldEqual None
    }
    "accept abbreviated args and --owl" in {
      Config(List("-i", "file.xml", "-o", "file.pl", "--owl")) shouldEqual
        Some(Config(new File("file.xml"), new File("file.pl"), owl = true))
    }
  }

}
