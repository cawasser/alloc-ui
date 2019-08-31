(ns alloc-ui.grid.allocation)

;;;;;;;;;;;;;;;;;;;;;
;
; PROBLEM
;
; develop a simple (really simple) model and algorithm for handling resource "demand"
; requests made by a set of requestors
;
; return a data structure like Datomic, :before, :after, :satisfied, and :rejected
; for each invocation
;
;

{:namespace "grid.allocation"
 :public-api ["fixed-unit-grid" "test-requests" "retract-requests"]
 :effective-sloc 110}

;;;;;;;;;;;;;;;;;;;;;
;
; SOLUTION
;
; 1) apply the requests to the grid
;
; 2) then scan for grid for requests that overlap
;
;      a) requests that do overlap are marked as "rejected"
;
;      b) requests that don't overlap are marked as "satisfied"
;
; 3) back out any contested slots, so the final grid is acceptable
;
; 4) we also need to retract requests from the grid, because conditions
;    may change and resources are no longer needed
;




;;;;;;;;;;;;;;;;;;;;
;
; ISSUES DURING EXPERIMENTATION
;
;    a) subsequent invocations don't preserve existing allocations found
;       in the 'input' grid - this due to how check-rejected works: it doesn't
;       have enough info to figure this out
;
;       RESOLVED - change remove-rejects to set any bad slots back
;       to the original value. works in one case, but needs lots more testing
;
;    b) subsequent invocations prove not just the most recent set of
;       "satisfactions", but ALL of them, which isn't exactly what I want
;
;       RESOLVED - change check-satisfied to not include slots that haven't
;       changed from the original
;
;    c) do we really want to make (test-requests...) threadable? ie, do we
;       want a 'single' parameter in last position that is actually a tuple
;       of the input grid and the requests, just so we can thread?
;
;       how realistic it that is actual practice?
;
;       RESOLVED - no threading!
;
; TODO: any modification cause by the rules engine approach in loco-rules.clj
;
; TODO: sparse matrix for the resource GRID
;
; TODO: let's get some spec going, so we can generate some tests
;
; TODO: develop a simple UI to develop "requests"
;
; TODO: determine what makes sense to LOG (intermediates? params?)
;
; TODO: determine METRICS for performance
;
;


;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;
;
; DATA STRUCTURES
;
; GRID
;    "sparse" map of cells that have been allocated
;
;    eg.
;
;      {"0-0" {:channel 0 :timeslot 0 :allocated-to #{"a"}}
;       "1-1" {:channel 1 :timeslot 1 :allocated-to #{"a"}}
;       "98-1" {:channel 98 :timeslot 1 :allocated-to #{"a"}}
;       "99-1" {:channel 99 :timeslot 1 :allocated-to #{"a"}}}
;
; REQUESTS
;
;    map of requestor-id set of vectors of request-slot (ie. channel and time-slot)
;
;    eg.
;
;      {"b" #{[0 0] [1 1]}
;       "a" #{[0 1] [1 0] [1 1]}}
;
; GRID-TEST-TX
;
;   map of 4-tuple
;
;      :before    - GRID before any valid requests were added
;
;      :after     - GRID after all valid requests were added
;
;      :satisfied - map of request-slot to requestor-id
;
;      :rejected  - map of request-slot to set of requestor-ids
;
;   eg.
;
;      {:before {}
;       :after  {"0-0" {:channel 0 :timeslot 0 :allocated-to #{"b"}}
;                "1-1" {:channel 1 :timeslot 1 :allocated-to #{"b" "a"}}
;                "0-1" {:channel 0 :timeslot 1 :allocated-to #{"a"}}
;                "1-0" {:channel 1 :timeslot 0 :allocated-to #{"a"}}},
;       :sat    {[0 0] #{:b}, [0 1] #{:a}, [0 1] #{:a},
;       :rej    {[1 1] #{:b :a}}}
;
;
;;;;;;;;;;;;;;;;;;;;
;
; PUBLIC API
;
; empty-grid  - returns an empty GRID, shortcut to return {}
;
; test-requests    - given a GRID and a REQUESTS, apply the requests
;
;                    returns a map of:
;                       1) the GRID 'before' (:before)
;                       2) the GRID 'after' (:after)
;                       3) the set of requests that were applied (:satisfied)
;                       4) the set of requests that were NOT applied (:rejected)
;
; retract-requests - returns an updated grid that does NOT include and of the
;                    allocations provided in the 'rejects' parameter, which is
;                    formatted as REQUESTS
;
;                    returns - an updated 'clean' grid
;
;                    note: in true LISP fashion, passing in items that don't exist
;                          does NOT cause an error. "Make sure these aren't in there." "Okay"
;
;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;



(defn- gen-id
  "generates a string 'ID' from channel and timeslot data"

  ([ch ts]
   (str ch "-" ts))

  ([[ch ts]]
   (str ch "-" ts)))


(defn- prune-allocations
  "removes any allocation where the :allocated-to value is nil or empty"

  [grid]
  (remove (fn [[_ v]] (or (nil? (:allocated-to v))
                          (empty? (:allocated-to v))))
          (seq grid)))




(defn- populate
  "Assigns each of the cells specified as [channel time-unit]
  coordinates to the given val

      returns - an updated grid"

  [grid requestor-id request-cells]
  (reduce (fn [g [ch ts]]
            (assoc g (gen-id ch ts)
                     {:channel      ch
                      :timeslot     ts
                      :allocated-to (into #{}
                                          (concat (get-in g [(gen-id ch ts)
                                                             :allocated-to])
                                                  #{requestor-id}))}))
          grid request-cells))



(defn- apply-requests
  "apply a map of requests to the grid, updating recursively

      returns - an updated grid"

  [grid requests]
  (if (empty? requests)
    grid
    (let [[id allocs] (first requests)]
      (recur (populate grid id allocs) (rest requests)))))



(defn- check-satisfied
  "return a map of that can be applied to the grid without
  violating the (sat-rule) predicate, ie, 'we should keep these'

      returns - {<id> {:channel <int>,
                         :timeslot <int>,
                         :allocated-to #{<id>}}"

  [pred-fn grid original-grid]
  (into {}
        (for [alloc (keys grid)]
          (let [val (get-in grid [alloc :allocated-to])]
            (if (and (pred-fn val)
                     (not (= val (get-in original-grid
                                         [alloc :allocated-to]))))
              {alloc (get grid alloc)})))))



(defn- check-rejected
  "return a map of {id #{requestors}} that can be applied to the grid that
  pass the the (rej-rule) predicate, ie, 'we should reject these'

      returns - {<id> #{<id>}}"

  [pred-fn grid]
  (into {}
        (for [alloc (keys grid)]
          (if (not (pred-fn (get-in grid [alloc :allocated-to])))
            {alloc (get-in grid [alloc :allocated-to])}))))



(defn- remove-rejects
  "returns a grid that does NOT include and of the allocations provided
  in the 'rejects' parameter, ie, the resulting grid is 'clean'

      returns - an updated grid"

  [original-grid grid rejects]
  (into {}
        (reduce (fn [g [id _]]
                  (assoc g id (get original-grid id)))
                grid rejects)))



(defn- retract-one-requestor
  "removes the requestor-id from each cell specified by retraction-cells -
     side benefit: it works even if you pass it invalid slots (ie, the requestor
     doesn't have an allocation

      returns - an updated grid"

  [grid [requestor-id retraction-cells]]
  (into {}
        (prune-allocations
          (reduce (fn [g id]
                    (assoc-in g [(gen-id id) :allocated-to]
                              (disj (get-in g [(gen-id id) :allocated-to])
                                    requestor-id)))
                  grid retraction-cells))))



;PUBLIC

(def empty-grid {})



(defn test-requests
  "given a grid and a set of requests, apply the requests

      returns a map of:
         1) the grid 'before' (:before)
         2) the grid 'after' (:after)
         3) the set of requests that were applied (:satisfied)
         4) the set of requests that were NOT applied (:rejected)"

  [sat-rule rej-rule initial-grid requests]
  (let [g (into {}
                (remove (fn [[_ v]] (nil? v))
                        (seq (apply-requests initial-grid requests))))]
    (let [sat (check-satisfied sat-rule g initial-grid)
          rej (check-rejected rej-rule g)]
      {:before initial-grid
       :after  (into {}
                     (remove (fn [[_ v]] (or (nil? v) (empty? v)))
                             (seq (remove-rejects initial-grid g rej))))
       :sat    sat
       :rej    rej})))


(defn retract-requests
  "apply a map of retractions to the grid, updating recursively

      returns - an updated grid"

  [grid retractions]
  (if (empty? retractions)
    grid
    (recur
      (retract-one-requestor grid (first retractions))
      (rest retractions))))




; TESTS
(comment
  (use 'grid.allocation :reload)
  (in-ns 'grid.allocation)

  (def overlapping-requests {"b" #{[0 0] [1 1]}
                             "a" #{[0 1] [1 1] [1 2]}
                             "c" #{[3 3] [3 4] [4 4]}})

  (def sat-rule #(and (<= (count %) 1)
                      (> (count %) 0)))
  (def rej-rule #(<= (count %) 1))


  ; a very simple case
  ;
  (test-requests sat-rule rej-rule empty-grid-5-5 overlapping-requests)


  ; now try chaining a few requests together. do we lose any pre-existing
  ; allocating when we make the next attempt?
  ;
  (def requests {:d #{[0 0] [1 1]}
                 :e #{[2 2] [2 3] [2 4]}
                 :f #{[1 3] [1 4]}})
  (def current-grid (atom empty-grid))


  (reset! current-grid
          (:after (test-requests sat-rule
                                 rej-rule
                                 @current-grid
                                 overlapping-requests)))

  @current-grid

  (reset! current-grid
          (:after (test-requests sat-rule
                                 rej-rule
                                 @current-grid
                                 requests)))

  (def fill-in {:g #{[0 1] [0 2] [0 3] [0 4]}
                :h #{[1 0] [2 0] [3 0] [4 0]}})
  (reset! current-grid
          (:after (test-requests sat-rule
                                 rej-rule
                                 @current-grid
                                 fill-in)))

  ; some retractions
  ;
  (def retractions {:g #{[0 2]}})
  (def retractions-2 {:g #{[0 3] [0 4]}})
  (def retract-3 {:a #{[0 1]} :c #{[4 4]}})
  (def retract-4 {"a" #{[70 70]}})

  (defn- diff [g1 g2]
    (clojure.set/difference (set (keys g1)) (set (keys g2))))

  (diff @current-grid
        (retract-requests @current-grid {}))

  (diff @current-grid
        (retract-requests @current-grid retractions))

  (diff @current-grid
        (retract-requests empty-grid retractions))

  (diff @current-grid
        (retract-requests @current-grid retractions-2))

  (diff @current-grid
        (retract-requests @current-grid retract-3))

  (diff @current-grid
        (retract-requests @current-grid retract-4))


  ())

