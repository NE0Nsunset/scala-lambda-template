import lambda.models.ExampleDynamoItem
import org.scalatest.BeforeAndAfterEach

import scala.collection.JavaConversions._

class ExampleDynamoItemServiceTest extends TestBase with BeforeAndAfterEach {
  override def beforeEach(): Unit = {
    super.beforeEach()
    exampleDynamoService.client.createTableIfNotExists
  }

  override def afterEach(): Unit = {
    super.afterEach()
    exampleDynamoService.client.destroyTable
  }

  describe("ExampleDynamoItemService Tests") {
    it("Can describe its table") {
      println(exampleDynamoService.describeTable)
      assert(exampleDynamoService.describeTable.nonEmpty)
    }

    it("Should use the alpakaka connector to list tables") {
      exampleDynamoService.client.listTables map { r =>
        println(r.toString)
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

    it("Should put an item") {
      exampleDynamoService.put(
        ExampleDynamoItem("dsdas",
                          "description",
                          new java.util.Date,
                          new java.util.Date)) map { r =>
        println(r)
        assert(true)
      }
    }

  }
}
