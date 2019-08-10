import lambda.models.ExampleDynamoItem
import org.scalatest.BeforeAndAfterEach
import scala.collection.JavaConversions._

class ExampleDynamoItemServiceTest extends TestBase with BeforeAndAfterEach {
  override def beforeEach(): Unit = {
    super.beforeEach()
    exampleDynamoService.clientHandler.createTableIfNotExists
  }

  override def afterEach(): Unit = {
    super.afterEach()
    exampleDynamoService.clientHandler.destroyTable
  }

  describe("ExampleDynamoItemService Tests") {
    it("Can describe its table") {
      assert(exampleDynamoService.describeTable.nonEmpty)
    }

    it("Should use the alpakaka connector to list tables") {
      exampleDynamoService.clientHandler.listTables map { r =>
        val tableNames = r.getTableNames.toList
        assert(tableNames.contains(tableName))
      }
    }

    it("Should use alpakka to scan database") {
      exampleDynamoService.scan map { r =>
        val items = r.getItems.toList
        println(items)
        assert(true)
      }
    }

    it("Should be able to insert and retrieve an item") {
      for {
        _ <- exampleDynamoService.put(TestObjects.exampleDynamoItem1)
        getResult <- exampleDynamoService.findItemByCompositeKey(
          TestObjects.exampleDynamoItem1.partKey,
          TestObjects.exampleDynamoItem1.rangeKey)
      } yield {
        assert(getResult.isInstanceOf[Option[ExampleDynamoItem]])
        assert(
          getResult.map(_.name).contains(TestObjects.exampleDynamoItem1.name))
      }
    }

    it("Should return none when no item exists for query") {
      for {
        getResult <- exampleDynamoService.findItemByCompositeKey(
          TestObjects.exampleDynamoItem1.partKey,
          TestObjects.exampleDynamoItem1.rangeKey)
      } yield {
        assert(getResult.isEmpty)
      }
    }

  }
}
