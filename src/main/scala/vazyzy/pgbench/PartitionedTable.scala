package vazyzy.pgbench

import slick.driver.PostgresDriver.api._

object PartitionedTable {

  def createTable: DBIO[Int] =
    sqlu"""
          CREATE TABLE IF NOT EXISTS partition_master(
            partitionKey varchar(100) NOT NULL,
            id SERIAL PRIMARY KEY,
            attr text
          );
    """

  def createTrigger: DBIO[Int] =
    sqlu"""
          DROP TRIGGER partition_trigger ON partition_master;
          CREATE TRIGGER partition_trigger
              BEFORE INSERT ON partition_master
              FOR EACH ROW EXECUTE PROCEDURE partition_insert();
    """

  def createIsertFunction: DBIO[Int] =
    sqlu"""
        CREATE OR REPLACE FUNCTION partition_insert()
        RETURNS TRIGGER AS $$$$
        BEGIN
            EXECUTE format('INSERT INTO %I VALUES ($$1.*)', NEW.partitionKey) USING NEW;
            RETURN NULL;
        EXCEPTION
        	WHEN undefined_table THEN
        	  EXECUTE format('CREATE TABLE %I(CHECK(partitionKey = %L )) INHERITS(partition_master)', NEW.partitionKey, NEW.partitionKey);
          EXECUTE format('INSERT INTO %I VALUES ($$1.*)', NEW.partitionKey) USING NEW;
        	  RETURN NULL;
        END;
        $$$$ LANGUAGE plpgsql;
    """

  def insert(partition: Int, attr: String): DBIO[Int] ={
     sqlu"""
          INSERT INTO partition_master(partitionKey, attr) VALUES($partition, $attr)
        """
  }

  def read(partition: Int, attr: String): DBIO[Seq[(String, Int, String)]] ={
    sql"""
          Select * FROM partition_master WHERE partitionKey='$partition'
        """.as[(String, Int, String)]
  }

}
