SELECT product.*
FROM Product p
         JOIN Creator cr ON p.id = cr.product_id
WHERE cr.user_id = <USER_ID>;