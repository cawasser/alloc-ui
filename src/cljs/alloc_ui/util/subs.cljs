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
  :local-grid
  (fn [db _]
    (-> db :data :local :grid)))

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
  (fn [db _]
    (-> db :data :local :combos)))

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
