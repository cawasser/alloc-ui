-- :name get-current-grid :? :*
-- :doc retrieves the current grid
SELECT * FROM grid;



-- :name create-cell! :! :n
-- :doc inserts a cell
insert into grid
(channel, timeslot, requestorid)
VALUES (:channel, :timeslot, :requestorid);



-- :name update-cell! :! :n
-- :doc updates, or inserts a cell
update grid
SET channel = :channel, timeslot = :timeslot, requestorid = :requestorid
WHERE channel = :channel, timeslot = :timeslot;



-- :name delete-cell! :! :n
-- :doc deletes a cell given the channel and timeslot
DELETE FROM grid
WHERE channel = :channel, timeslot = :timeslot;


-- :name delete-all-cells! :! :n
-- :doc deletes all cells
DELETE FROM grid;
