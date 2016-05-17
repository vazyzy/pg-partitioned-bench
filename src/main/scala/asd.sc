import slick.driver.PostgresDriver.api._
sqlu"""
        INSERT INTO partition_master(partitionKey, attr) VALUES("1", "2")
      """.overrideStatements(Seq("!","23")).toString