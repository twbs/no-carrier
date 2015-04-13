import org.specs2.mutable._
import com.getbootstrap.no_carrier.util.RichTraversableOnce

class RichTraversableOnceSpec extends Specification {
  "maxOption" should {
    "be None when the collection is empty" in {
      val emptySeq: Seq[Int] = Seq()
      emptySeq.maxOption must beNone
    }
    "be the maximum when the collection is non-empty" in {
      Seq(7).maxOption mustEqual Some(7)
      Seq(1, 2, 6, 5, 4).maxOption mustEqual Some(6)
    }
  }
}
