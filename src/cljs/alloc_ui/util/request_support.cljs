(ns alloc-ui.util.request-support
  (:require
    [re-frame.core :as rf]))

(defn make-combos
  "combos will be use to determining all the possible combinations of the candidate requests
   that produce workable solutions"
  [requests grid]

  ; TODO replace static answer with real calls to the service

  (rf/dispatch [:new-combo #{"k"}])
  (rf/dispatch [:new-combo #{"l"}])
  (rf/dispatch [:new-combo #{"k" "l"}]))

(defn expound-requests [app-db requester]
  {requester (get-in app-db [:data :local :requests requester])})



(comment

  (require '[alloc-ui.dev-db :as d-d])

  (def current-grid {})

  (def potential-grid {})

  (def requests {"j" #{[0 0] [[1 2 3] 1]}
                 "k" #{[0 2] [3 0]}
                 "l" #{[[2 3] 0] [[2 3] 1] [[2 3] 2]}
                 "m" #{[[2 3] 2] [[0 1 2] 1]}})

  (def default-last-service-version "unknown service")

  (def default-last-service-sha "00000")

  (def db {:data {:last-service-version default-last-service-version
                  :last-service-sha     default-last-service-sha
                  :current              {:grid current-grid}
                  :local                {:grid               potential-grid
                                         :requests           requests
                                         :potential-requests #{}
                                         :combos             []}}})

  (into {}
    (map (partial expound-requests db) #{"k" "l"}))

  ())