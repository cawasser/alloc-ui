(ns alloc-ui.util.subs
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [alloc-ui.util.color-pallet :as cp]))



;;subscriptions

(rf/reg-sub
  :current-grid
  (fn [db _]
    (-> db :data :current :grid)))


(rf/reg-sub
  :all-local-grid
  (fn [db _]
    (-> db :data :local :grid)))

(rf/reg-sub
  :local-grid

  :<- [:local-potential-requests]
  :<- [:all-local-grid]
  :<- [:local-combos]

  (fn [[local-requests all-local-grid local-combos] _]
    (prn ":local-grid" local-requests ", " local-combos)
    (if (contains? (into #{} local-combos) local-requests)
      (get all-local-grid local-requests)
      (get all-local-grid #{}))))



(rf/reg-sub
  :local-requests
  (fn [db _]
    (-> db :data :local :requests)))

(rf/reg-sub
  :local-potential-requests
  (fn [db _]
    (-> db :data :local :potential-requests)))


(rf/reg-sub
  :local-combos

  :<- [:all-local-grid]

  (fn [all-local-grid _]
    (prn "subscribe :local-combos " all-local-grid)
    (filter #(not (empty? %)) (keys all-local-grid))))


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
