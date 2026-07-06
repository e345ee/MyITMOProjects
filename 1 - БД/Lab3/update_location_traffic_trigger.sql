CREATE OR REPLACE FUNCTION update_location_traffic()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        UPDATE location
        SET traffic_human = traffic_human + 1
        WHERE id = NEW.location_id;
    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE location
        SET traffic_human = traffic_human - 1
        WHERE id = OLD.location_id;
    ELSIF (TG_OP = 'UPDATE') THEN
        UPDATE location
        SET traffic_human = traffic_human - 1
        WHERE id = OLD.location_id;
		UPDATE location
        SET traffic_human = traffic_human + 1
        WHERE id = NEW.location_id;

    END IF;
    RETURN NULL;
END
$$
LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER update_location_traffic_trigger
AFTER INSERT OR DELETE OR UPDATE ON human 
FOR EACH ROW 
EXECUTE FUNCTION update_location_traffic();

