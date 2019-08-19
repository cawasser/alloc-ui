(ns alloc-ui.app
  (:require [alloc-ui.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
