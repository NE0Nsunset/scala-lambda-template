import com.amazonaws.services.dynamodbv2.model.TableDescription
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import service.LocalDynamoUtil

class ExampleDynamoItemServiceTest extends FunSuite with BeforeAndAfterEach {
  val testTableName = "testTable"

  override def beforeEach(): Unit = {
    super.beforeEach()
    LocalDynamoUtil.getOrCreateTable(testTableName)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    LocalDynamoUtil.destroyTable(testTableName)
  }

  test("Table is created") {
    val tableDescription: String =
      LocalDynamoUtil.getTable(testTableName)
    assert(tableDescription.nonEmpty)
  }

  test("Can insert object") {}

}
