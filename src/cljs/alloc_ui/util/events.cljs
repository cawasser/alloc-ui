(ns alloc-ui.util.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [alloc-ui.dev-db :as d-d]
    [alloc-ui.util.request-support :as rs]
    [day8.re-frame.tracing :refer-macros [fn-traced]]))



(defn- set-version [db message]
  ;(prn "set-version" message)
  (assoc-in db [:data :last-service-version]
    (get message :service-version)))

(defn- set-sha [db message]
  ;(prn "set-sha" message)
  (assoc-in db [:data :last-service-sha]
    (get message :service-sha)))


;;dispatchers

(rf/reg-event-db
  :navigate
  (fn-traced [db [_ route]]
    (prn ":navigate")
    (assoc db :route route)))



(rf/reg-event-db
  :init-db
  (fn-traced [db [_]]
    (prn ":init-db")
    (assoc db :data {:last-service-version d-d/default-last-service-version
                     :last-service-sha     d-d/default-last-service-sha
                     :current              {:grid d-d/current-grid}
                     :local                {:all-potential-grids          d-d/potential-grid
                                            :requests                     d-d/requests
                                            :requests-under-consideration #{}
                                            :combos                       []
                                            :editing                      ""}})))



; TODO - add key so we track which requests match which grid
(rf/reg-event-db
  :set-local-grid
  (fn-traced [db [_ grid]]
    (assoc-in db [:data :local :all-potential-grids] grid)))

(rf/reg-event-db
  :set-local-requests
  (fn-traced [db [_ requests]]
    (assoc-in db [:data :local :requests] requests)))

(rf/reg-event-db
  :add-to-local-potential-requests
  (fn-traced [db [_ k]]
    (let [orig (-> db :data :local :requests-under-consideration)]
      (-> db
        (assoc-in [:data :local :requests-under-consideration]
          (conj orig k))
        (assoc-in [:data :local :selected-request-set] #{})))))


(rf/reg-event-db
  :remove-from-local-potential-requests
  (fn-traced [db [_ k]]
    (let [orig (-> db :data :local :requests-under-consideration)]
      (-> db
        (assoc-in [:data :local :requests-under-consideration]
          (disj orig k))
        (assoc-in [:data :local :selected-request-set] #{})))))


(rf/reg-event-db
  :set-current-grid
  (fn-traced [db [_ grid]]
    ;(prn ":set-current-grid" grid)
    ;(prn (get grid :service-version))
    ;(prn ":set-current-grid un" (cljs.reader/read-string (:result grid)))
    (-> db
      (set-version grid)
      (set-sha grid)
      (assoc-in [:data :current :grid] (cljs.reader/read-string
                                         (:result grid))))))

(rf/reg-event-fx
  :fetch-current-grid
  (fn-traced [_ _]
    ;(prn ":fetch-current-grid")
    {:http-xhrio {:method          :get
                  :uri             "/api/grid"
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:set-current-grid]}}))

(rf/reg-event-db
  :set-potential-grid-from-requests
  (fn-traced [db [_ results]]
    ;(prn ":set-potential-grid-from-requests" results)
    (let [res (cljs.reader/read-string (:result results))]
      ;(prn ":set-potential-grid-from-requests" (-> res keys second) (count (get res (-> res keys second))))
      (-> db
        (set-version results)
        (set-sha results)
        (assoc-in [:data :local :all-potential-grids] res)
        (assoc-in [:data :local :selected-request-set] #{}) ;(-> res keys "sort" "first valid"))
        (assoc-in [:data :local :selected-request-subset] 0)
        (assoc-in [:data :local :selected-request-subset-limit] 0)))))

(def last-request (atom {}))


(defn- internal-ex [db requests]
  (prn "internal-ex" requests)
  (into {}
    (map (partial rs/expound-requests (:db db))
      requests)))

(rf/reg-event-fx
  :allocate
  (fn-traced [db [_ requests]]
    (prn ":allocate" requests)
    (reset! last-request requests)
    {:http-xhrio {:method          :post
                  :uri             "/api/request"
                  :params          {:requests (pr-str requests)}
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:set-potential-grid-from-requests]
                  :on-failure      [:common/set-error]}}))


(rf/reg-event-db
  :new-combo
  (fn-traced
    [db [_ entry]]
    (assoc-in db [:data :local :combos]
      (cons entry (-> db :data :local :combos)))))



(rf/reg-event-db
  :common/set-error
  (fn-traced [db [_ error]]
    (assoc db :common/error error)))



(rf/reg-event-db
  :editing
  (fn-traced [db [_ id]]
    (-> db
      (assoc-in [:data :local :editing] id)
      (assoc-in [:data :local :editing-requests] (get-in db [:data :local :requests id])))))





