-- :name get-current-grid :? :*
-- :doc retrieves the current grid
SELECT * FROM grid
WHERE owner = "current";


-- :name get-grid-by-owner :? :*
-- :doc retrieves the grid specified by owner
SELECT * FROM grid
WHERE owner = :owner;


-- :name create-grid! :! :n
-- :doc inserts a grid
insert into grid
(owner, cells)
VALUES (:owner, :cells);



-- :name update-grid! :! :n
-- :doc updates a grid
update grid
SET content = :content
WHERE owner = :owner



-- :name delete-grid-by-owner! :! :n
-- :doc deletes a grid given an owner
DELETE FROM grid
WHERE owner = :owner


-- :name delete-all-grids! :! :n
-- :doc deletes all grids
DELETE FROM grid;
