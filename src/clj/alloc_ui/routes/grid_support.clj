(ns alloc-ui.routes.grid-support
  (:require [alloc-ui.db.core :as db]
            [alloc-ui.grid.request-rules :as r]
            [alloc-ui.grid.allocation :as a]))


(def current-grid [[#{"a"} #{} #{} #{} #{}]
                   [#{"a"} #{} #{} #{} #{}]
                   [#{} #{"b"} #{"b"} #{} #{}]
                   [#{} #{"c"} #{"c"} #{"z"} #{"z"}]
                   [#{} #{"aa"} #{"aa"} #{} #{"z"}]])

(def sample-requests {"q" #{[1 0]}})

; TODO: move to a sparse matrix for the grid, across all parts of the system
(defn- null-tx [grid requests]
  {:before grid
   :after grid
   :sat {}
   :rej requests
   :error "Engine rejects the requests"})



(defn to-grid [raw-sql]
  "transforms raw SQL cell data form the grid table into the correct data
   structure for use with allocation requests"

  (prn "to-grid " (clojure.edn/read-string (:cells (first raw-sql))))
  (:cells (first raw-sql)))


(def sat-rule #(and (<= (count %) 1)
                    (> (count %) 0)))
(def rej-rule #(<= (count %) 1))

(defn apply-requests-to-grid
      ([requests]
       (apply-requests-to-grid requests (-> (db/get-current-grid)
                                            first
                                            :cells
                                            clojure.edn/read-string)))

  ([requests grid]
   (let [adjusted-reqs (r/generate-acceptable-requests grid requests)]

     {:adjusted-requests adjusted-reqs
      :tx                (if (empty? adjusted-reqs)
                           (null-tx grid requests)
                           (a/test-requests
                             sat-rule
                             rej-rule
                             grid
                             adjusted-reqs))})))




(comment

  ; code to support testing
  (in-ns 'alloc-ui.routes.grid-support)

  (in-ns 'alloc-ui.db.core)

  ; clear the grid table
  (db/delete-all-grids!)

  ; load some default data into the grid table
  (db/create-grid! {:owner "current" :cells (pr-str current-grid)})

  ; test fetching and processing the SQL data for return back to clients
  (to-grid (db/get-current-grid))

  (prn-str sample-requests)


  (r/generate-acceptable-requests current-grid sample-requests)
  (apply-requests-to-grid sample-requests current-grid)

  (pr-str (apply-requests-to-grid sample-requests current-grid))

  (pr-str (apply-requests-to-grid sample-requests))


  (-> (db/get-current-grid)
      first
      :cells
      clojure.edn/read-string)


  (def j-req (pr-str {"j" #{[[1 2 3] 1]}}))
  (def j-req "{\"j\" #{[0 0] [[1 2 3] 1]}}")
  (def k-req "{\"k\" #{[3 0]}}")

  (pr-str (apply-requests-to-grid
            (clojure.edn/read-string j-req)
            current-grid))
  (pr-str (apply-requests-to-grid
            (clojure.edn/read-string k-req)
            current-grid))

  (r/generate-acceptable-requests
    current-grid
    (clojure.edn/read-string k-req))


  (def color-pallet   [["green" "white"] ["blue" "white"] ["orange" "black"]
                       ["grey" "white"] ["cornflowerblue" "white"] ["darkcyan" "white"]
                       ["goldenrod" "black"] ["khaki" "black"] ["deepskyblue" "black"]
                       ["navy" "white"] ["red" "white"] ["orangered" "white"]])


  (defn- grid-keys [grid]
    (remove nil?
      (into #{}
            (for [ch (range (count (first grid)))
                  ts (range (count grid))]
              (first (get-in grid [ts ch]))))))

  (defn- color-match [grid requestors]
    (let [c-reqs (grid-keys grid)]
      (zipmap (concat c-reqs (keys requestors)) color-pallet)))

  (def j-atom (atom {"j" #{[[1 2 3] 1]}}))
  (def g-atom (atom current-grid))

  (range (count (first current-grid)))
  (range (count current-grid))
  (first (get-in current-grid [0 0]))
  (grid-keys current-grid)

  (color-match (clojure.edn/read-string
                 (:cells (first (db/get-current-grid))))
               @j-atom)

  ())







