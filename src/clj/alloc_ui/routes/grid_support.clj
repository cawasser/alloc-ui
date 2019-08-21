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
   structure for use with allocation requests"

  (clojure.edn/read-string (:cells (first raw-sql))))


(comment

  ; code to support testing
  (in-ns 'alloc-ui.routes.grid-support)

  (in-ns 'alloc-ui.db.core)

  ; clear the grid table
  (db/delete-all-cells!)

  ; load some default data into the grid table
  (db/create-grid! {:owner "current" :cells (pr-str current-grid)})

  ; test fetching and processing the SQL data for return back to clients
  (to-grid (db/get-current-grid))


  ())







