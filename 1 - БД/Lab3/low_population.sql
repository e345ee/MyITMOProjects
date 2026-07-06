CREATE OR REPLACE  FUNCTION low_population() RETURNS integer AS $$
DECLARE
    min_human integer;
BEGIN
	IF (EXISTS (
		SELECT * 
		FROM location 
		LEFT JOIN human 
		ON human.location_id = location.id
		WHERE human.id IS NULL
	)) THEN
		SELECT location.id INTO min_human
		FROM location 
		LEFT JOIN human 
		ON human.location_id = location.id
		WHERE human.id IS NULL
		LIMIT 1;
		RETURN min_human;
    ELSE
	    SELECT location_id INTO min_human
	    FROM human 
	    GROUP BY human.location_id 
		ORDER BY COUNT(*)
	    LIMIT 1;
	    RETURN min_human;
	END IF;
END;
$$
LANGUAGE plpgsql;
