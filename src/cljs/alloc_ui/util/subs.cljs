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

  ;:<- [:local-potential-requests]
  :<- [:selected-request-set]
  :<- [:selected-request-subset]
  :<- [:all-local-grid]
  :<- [:local-combos]

  (fn [[selected subset all-local-grid local-combos] _]
    ;(prn ":local-grid" selected ", " local-combos)
    (if (contains? (into #{} local-combos) selected)
      (get-in all-local-grid [selected subset])
      (get all-local-grid #{}))))


(rf/reg-sub
  :selected-request-subset
  (fn [db _]
    (-> db :data :local :selected-request-subset)))

(rf/reg-sub
  :selected-request-set
  (fn [db _]
    (-> db :data :local :selected-request-set)))

(rf/reg-sub
  :selected-request-subset-limit
  (fn [db _]
    (-> db :data :local :selected-request-subset-limit)))



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
    ;(prn "subscribe :local-combos " all-local-grid)
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


(rf/reg-sub
  :editing
  (fn [db _]
    (-> db :data :local :editing)))
