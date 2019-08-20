(ns alloc-ui.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [alloc-ui.dev-db :as d-d]
    [day8.re-frame.tracing :refer-macros [fn-traced]]))

;;dispatchers

(rf/reg-event-db
  :navigate
  (fn [db [_ route]]
    (prn ":navigate")
    (assoc db :route route)))



(rf/reg-event-db
  :init-db
  (fn-traced [db [_]]
    (prn ":init-db")
    (assoc db :data {:current {:grid d-d/current-grid}
                     :local {:grid d-d/potential-grid
                             :requests d-d/requests}})))




(rf/reg-event-db
  :set-local-grid
  (fn-traced [db [_ grid]]
      (assoc-in db [:data :local :grid] grid)))

(rf/reg-event-db
  :set-local-requests
  (fn-traced [db [_ requests]]
      (assoc-in db [:data :local :requests] requests)))

(rf/reg-event-db
  :set-current-grid
  (fn-traced [db [_ grid]]
      (prn ":set-current-grid")
      (assoc-in db [:data :current :grid] grid)))

(rf/reg-event-fx
  :fetch-current-grid
  (fn-traced [_ _]
             (prn ":fetch-current-grid")
      {:http-xhrio {:method          :get
                    :uri             "/api/grid"
                    :response-format (ajax/json-response-format)
                    :on-success       [:set-current-grid]}}))


(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))




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
