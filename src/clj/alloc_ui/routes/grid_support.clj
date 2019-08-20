(ns alloc-ui.routes.grid-support
  (:require [alloc-ui.db.core :as db]))


(def current-grid [[#{"a"} #{""} #{""} #{""} #{""}]
                   [#{"a"} #{""} #{""} #{""} #{""}]
                   [#{""} #{"b"} #{"b"} #{""} #{""}]
                   [#{""} #{"c"} #{"c"} #{"z"} #{"z"}]
                   [#{""} #{"aa"} #{"aa"} #{""} #{"z"}]])

; TODO: move to a sparse matrix for the grid, across all parts of the system

(defn to-grid [raw-sql]
  "transforms raw SQL cell data form the grid table into the correct data
   structure for use with allocation requests

   NOTE: restricted to a 5x5 fixed grid"

  (let [grid [[#{} #{} #{} #{} #{}]
              [#{} #{} #{} #{} #{}]
              [#{} #{} #{} #{} #{}]
              [#{} #{} #{} #{} #{}]
              [#{} #{} #{} #{} #{}]]]
    (reduce (fn [g {:keys [channel timeslot requestorid]}]
              (assoc-in g [timeslot channel]
                        (merge (get-in g [timeslot channel])
                               (if (not (empty? requestorid))
                                 requestorid
                                 ""))))
            grid raw-sql)))


(comment

  ; code to support testing
  (in-ns 'alloc-ui.routes.grid-support)

  (get-in current-grid [0 0])

  (in-ns 'alloc-ui.db.core)

  ; clear the grid table
  (db/delete-all-cells!)

  ; load some default data inot the grid table
  (for [ch (range (count (first current-grid)))
        ts (range (count current-grid))]
    (let [req-id (get-in current-grid [ts ch])]
      (prn req-id)
      (db/create-cell! {:channel ch
                        :timeslot ts
                        :requestorid (first req-id)})))

  ; test fetching and processing the SQL data for return back to clients
  (to-grid (db/get-current-grid))

  ())







