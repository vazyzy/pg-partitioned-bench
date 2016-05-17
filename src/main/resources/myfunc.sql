CREATE OR REPLACE FUNCTION partition_insert()
RETURNS TRIGGER AS $$
BEGIN
    EXECUTE format('INSERT INTO %I VALUES ($1.*)', NEW.partitionKey) USING NEW;
    RETURN NULL;
EXCEPTION
	WHEN undefined_table THEN
	  EXECUTE format('CREATE TABLE %I(CHECK(partitionKey = %L )) INHERITS(partition_master)', NEW.partitionKey, NEW.partitionKey);
	  EXECUTE format('INSERT INTO %I VALUES ($1.*)', NEW.partitionKey) USING NEW;
	  RETURN NULL;
END; 
$$ LANGUAGE plpgsql;


CREATE TABLE IF NOT EXISTS partition_master(
  partitionKey varchar(100) NOT NULL,
  id SERIAL PRIMARY KEY,
  attr text
);


CREATE TRIGGER partition_trigger
    BEFORE INSERT ON partition_master
    FOR EACH ROW EXECUTE PROCEDURE partition_insert();
