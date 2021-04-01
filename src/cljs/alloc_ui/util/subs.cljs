(ns alloc-ui.util.subs
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [alloc-ui.util.color-pallet :as cp]
    [clojure.math.combinatorics :as combo]))



;;subscriptions

(rf/reg-sub
  :current-grid
  (fn [db _]
    (-> db :data :current :grid)))


(rf/reg-sub
  :all-potential-grids
  (fn [db _]
    (-> db :data :local :all-potential-grids)))

(rf/reg-sub
  :local-grid

  ;:<- [:requests-under-consideration]
  :<- [:selected-request-set]
  :<- [:selected-request-subset]
  :<- [:all-potential-grids]
  :<- [:local-combos]

  (fn [[selected subset all-local-grid local-combos] _]
    ;(prn ":local-grid" selected ", " local-combos)
    (if (contains? (into #{} local-combos) selected)
      (get-in all-local-grid [selected subset])
      (get all-local-grid #{}))))



(rf/reg-sub
  :local-requests
  (fn [db _]
    (-> db :data :local :requests)))

(rf/reg-sub
  :requests-under-consideration
  (fn [db _]
    (-> db :data :local :requests-under-consideration)))


(rf/reg-sub
  :local-combos

  :<- [:all-potential-grids]

  (fn [all-potential-grids _]
    ;(prn "subscribe :local-combos " all-all-potential-grids-grid)
    (filter #(not (empty? %)) (keys all-potential-grids))))


(rf/reg-sub
  :color-matching

  :<- [:current-grid]
  :<- [:local-requests]

  (fn [[grid requests] _]
    (cp/color-match grid requests)))




(rf/reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(rf/reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))


(rf/reg-sub
  :last-service-version
  (fn [db _]
    (-> db :data :last-service-version)))

(rf/reg-sub
  :last-service-sha
  (fn [db _]
    (-> db :data :last-service-sha)))


(rf/reg-sub
  :editing
  (fn [db _]
    (-> db :data :local :editing)))


(rf/reg-sub
  :editing-requests
  (fn [db _]
    (-> db :data :local :editing-requests)))


(rf/reg-sub
  :selected-potential-grids

  :<- [:requests-under-consideration]
  :<- [:all-potential-grids]

  (fn [[reqs grids] _]
    (->> reqs
      seq
      (combo/subsets)
      (filter not-empty)
      (map #(into #{} %))
      (map (fn [n] {n (get grids n)}))
      (into {}))))


; work out how to get just the potential grids for all the combinations
; of the requests that are selected in the list
;
; this should be a strict subset of all the potential grids computed by the
; server
;
(comment
  (def reqs #{"k"})
  (def reqs #{"k" "l"})

  (def reqs @(rf/subscribe [:requests-under-consideration]))
  (def grids @(rf/subscribe [:all-potential-grids]))
  (def local-combos @(rf/subscribe [:local-combos]))

  (combo/subsets [1 2 3])
  (combo/subsets (seq #{1 2 3}))

  (combo/subsets (seq reqs))
  (map #(into #{} %) (combo/subsets (seq reqs)))
  (->> reqs
    seq
    (combo/subsets)
    (filter not-empty)
    (map #(into #{} %))
    (map (fn [n] {n (get grids n)}))
    (into {}))

  (= #{"k" "l"} #{"l" "k"})

  (count grids)

  (def i-r '(#{"k"} #{"l"} #{"k" "l"}))

  (get grids #{"k" "l" "s"})
  (into {} (map (fn [n] {n (get grids n)}) i-r))



  (->> (clojure.set/intersection
         (into #{} (combo/subsets (seq reqs)))
         local-combos)
    (filter some?)
    (map (into #{})))

  ())


