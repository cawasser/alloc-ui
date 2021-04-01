(ns alloc-ui.routes.grid-support
  (:require [alloc-ui.db.core :as db]
            [alloc-ui.grid.request-rules :as r]
            [alloc-ui.grid.allocation :as a]
            [alloc-ui.sparse-grid.sparse-grid :as sparse-a]
            [alloc-ui.sparse-grid.sparse-request-rules :as sparse-r]
            [clojure.math.combinatorics :as combo]
            [clojure.tools.logging :as log]))


(def current-grid [[#{"a"} #{} #{} #{} #{}]
                   [#{"a"} #{} #{} #{} #{}]
                   [#{} #{"b"} #{"b"} #{} #{}]
                   [#{} #{"c"} #{"c"} #{"z"} #{"z"}]
                   [#{} #{"aa"} #{"aa"} #{} #{"z"}]])

(def sparse-grid {"0-0" {:channel 0 :timeslot 0 :allocated-to #{"a"}}
                  "0-1" {:channel 0 :timeslot 1 :allocated-to #{"a"}}
                  "1-2" {:channel 1 :timeslot 2 :allocated-to #{"b"}}
                  "2-2" {:channel 2 :timeslot 2 :allocated-to #{"b"}}
                  "1-3" {:channel 1 :timeslot 3 :allocated-to #{"c"}}
                  "2-3" {:channel 2 :timeslot 3 :allocated-to #{"c"}}
                  "1-4" {:channel 1 :timeslot 4 :allocated-to #{"aa"}}
                  "2-4" {:channel 2 :timeslot 4 :allocated-to #{"aa"}}
                  "3-3" {:channel 3 :timeslot 3 :allocated-to #{"z"}}
                  "3-4" {:channel 3 :timeslot 4 :allocated-to #{"z"}}
                  "4-4" {:channel 4 :timeslot 4 :allocated-to #{"z"}}})


(def sample-requests {"q" #{[1 0]}})

; TODO: move to a sparse matrix for the grid, across all parts of the system

(defn- null-tx [grid requests]
  {:before grid
   :after  grid
   :sat    {}
   :rej    requests
   :error  "Engine rejects the requests"})



(defn to-grid [raw-sql]
  "transforms raw SQL cell data form the grid table into the correct data
   structure for use with allocation requests"

  ;(prn "to-grid " (clojure.edn/read-string (:cells (first raw-sql))))
  (-> raw-sql
    first
    :cells
    clojure.edn/read-string))
;:cells (first raw-sql)))


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
   (let [adjusted-reqs (sparse-r/generate-acceptable-requests grid requests)]

     {:adjusted-requests adjusted-reqs
      :tx                (if (empty? adjusted-reqs)
                           ; TODO: call to null-tx may just need a (map)
                           (null-tx grid requests)
                           ; TODO: call to sparse-a/test-requests may just need a (map)
                           (sparse-a/test-requests
                             sat-rule
                             rej-rule
                             grid
                             adjusted-reqs))})))

(def testable-requests {})
(def testable-grid {})


(comment
  (sparse-r/generate-acceptable-requests-multi
    testable-grid testable-requests)

  (apply-requests-to-grid-multi
    testable-requests testable-grid)

  ())

(def last-request (atom {}))
(def last-grid (atom {}))

(defn analyze-combinations
  "analyses all possible combinations of the individual requests
  and results into a format for the client to use"

  ([requests]
   (analyze-combinations requests (-> (db/get-current-grid)
                                    first
                                    :cells
                                    clojure.edn/read-string)))

  ([requests grid]
   (log/info "analyze-combinations" requests)
   (reset! last-request requests)
   (reset! last-grid grid)
   (->> requests

     ; convert the request map to a collection of vectors so we
     ; can use a request as a single datum (rather than a k-v)
     seq

     ;  gets all the possible combinations of requests
     combo/subsets

     ; turn the results back into a map for further processing
     (map (fn [x] (flatten x)))
     (map (fn [x] (apply hash-map x)))

     ; run each through the solver
     (map (fn [x] (apply-requests-to-grid x grid)))

     ; pick out the results
     (map (fn [x]
            {(into #{} (keys (:adjusted-requests x)))
             (-> x :tx :after)}))

     ; and put everything into a single map (this serves to compress
     ; out the multiple copies of the #{} key...)
     (into {}))))


(defn analyze-combinations-multi
  "analyses all possible combinations of the individual requests
  and results into a format for the client to use"

  ([requests]
   (analyze-combinations-multi requests (-> (db/get-current-grid)
                                          first
                                          :cells
                                          clojure.edn/read-string)))

  ([requests grid]
   (log/info "analyze-combinations-multi" (count requests))
   (reset! last-request requests)
   (reset! last-grid grid)

   (->> @last-request

     ; convert the request map to a collection of vectors so we
     ; can use a request as a single datum (rather than a k-v)
     seq

     ;  gets all the possible combinations of requests
     combo/subsets

     ; turn the results back into a map for further processing
     (map (fn [x] (flatten x)))
     (map (fn [x] (apply hash-map x)))

     (map #(sparse-r/generate-acceptable-requests-multi @last-grid %))

     ;(map (fn [x] (apply-requests-to-grid-multi x @last-grid)))
     ((fn [adjusted-reqs]
        {:adjusted-requests adjusted-reqs
         :tx                (if (empty? adjusted-reqs)
                              (null-tx @last-grid @last-request)
                              (map
                                #(map (partial sparse-a/test-requests
                                        sat-rule
                                        rej-rule
                                        @last-grid) %)
                                adjusted-reqs))}))

     ; extract the results and reformat them so all the results for each combo are together

     :tx
     (map #(map (juxt (fn [x] (-> x :reqs keys)) :after) %))
     (map #(map (fn [[k v]] {(into #{} k) [v]}) %))
     flatten
     (apply merge-with clojure.set/union))))


(comment

  (analyze-combinations-multi @last-request @last-grid)

  (def part
    (->> @last-request

      ; convert the request map to a collection of vectors so we
      ; can use a request as a single datum (rather than a k-v)
      seq

      ;  gets all the possible combinations of requests
      combo/subsets

      ; turn the results back into a map for further processing
      (map (fn [x] (flatten x)))
      (map (fn [x] (apply hash-map x)))

      (map #(sparse-r/generate-acceptable-requests-multi @last-grid %))

      ;(map (fn [x] (apply-requests-to-grid-multi x @last-grid)))
      ((fn [adjusted-reqs]
         {:adjusted-requests adjusted-reqs
          :tx                (if (empty? adjusted-reqs)
                               (null-tx @last-grid @last-request)
                               (map
                                 #(map (partial sparse-a/test-requests
                                         sat-rule
                                         rej-rule
                                         @last-grid) %)
                                 adjusted-reqs))}))

      :tx
      (map #(map (juxt (fn [x] (-> x :reqs keys)) :after) %))
      (map #(map (fn [[k v]] {(into #{} k) [v]}) %))
      flatten
      (apply merge-with clojure.set/union)))

  ;(apply-requests-to-grid-multi part @last-grid)





  (into #{} (map #(into #{} (keys (first %)))
              (:adjusted-requests step-2)))

  ; no we got all the possibilities or


  ; trying to get the possibilities organized by the set of requests satisfied

  (map #(map (juxt :reqs :after) %)
    (-> step-2 :tx))


  (def step-4 (map #(map (juxt (fn [x] (-> x :reqs keys)) :after) %)
                (-> step-2 :tx)))
  (def step-5 (map #(map (fn [[k v]] {(into #{} k) [v]}) %) step-4))
  (apply merge-with clojure.set/union (flatten step-5))



  (->> @last-request
    seq
    combo/subsets
    (map (fn [x] (flatten x)))
    (map (fn [x] (apply hash-map x))))

  (def s (->> @last-request
           seq
           combo/subsets
           (map (fn [x] (flatten x)))
           (map (fn [x] (apply hash-map x)))))
  (map (fn [x] (apply-requests-to-grid-multi x @last-grid)) s)




  (def step-5
    (->> step-2
      :tx
      (map #(map (juxt (fn [x] (-> x :reqs keys)) :after) %))
      (map #(map (fn [[k v]] {(into #{} k) [v]}) %))
      flatten
      (apply merge-with clojure.set/union)))


  ())



(comment

  ; code to support testing
  (in-ns 'alloc-ui.routes.grid-support)

  (in-ns 'alloc-ui.db.core)

  ; clear the grid table
  (db/delete-all-grids!)

  ; load some default data into the grid table
  (db/create-grid! {:owner "current" :cells (pr-str sparse-grid)})

  ; test fetching and processing the SQL data for return back to clients
  (to-grid (db/get-current-grid))

  (prn-str sample-requests)


  (r/generate-acceptable-requests current-grid sample-requests)
  (apply-requests-to-grid sample-requests current-grid)

  (r/generate-acceptable-requests-multi current-grid sample-requests)


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


  (def color-pallet [["green" "white"] ["blue" "white"] ["orange" "black"]
                     ["grey" "white"] ["cornflowerblue" "white"] ["darkcyan" "white"]
                     ["goldenrod" "black"] ["khaki" "black"] ["deepskyblue" "black"]
                     ["navy" "white"] ["red" "white"] ["orangered" "white"]])

  (def color-pallet {:a ["green" "white"]
                     :b ["blue" "white"]
                     :c ["orange" "black"]})

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

  (require '[alloc-ui.util.color-pallet :as cp])
  (cp/color-match (clojure.edn/read-string
                    (:cells (first (db/get-current-grid))))
    @j-atom)

  ())

(comment
  (def requests {"j" #{[0 0] [[1 2 3] 1]},
                 "k" #{[3 0] [0 2]},
                 "l" #{[[2 3] 0] [[2 3] 2] [[2 3] 1]},
                 "m" #{[[2 3] 2] [[0 1 2] 1]}})

  (analyze-combinations requests)


  ())





