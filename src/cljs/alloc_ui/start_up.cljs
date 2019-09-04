(ns alloc-ui.start-up
  (:require
    [day8.re-frame.http-fx]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [alloc-ui.events]
    [reitit.core :as reitit]
    [clojure.string :as string])
  (:import goog.History))


;; -------------------------
;; Routes

(def router
  (reitit/router
    [["/" :home]
     ["/heatmap" :heatmap]
     ["/about" :about]]))


;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (let [uri (or (not-empty (string/replace (.-token event) #"^.*#" "")) "/")]
          (rf/dispatch
            [:navigate (reitit/match-by-path router uri)]))))
    (.setEnabled true)))



